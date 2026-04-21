import mqtt from 'mqtt'
import type { MqttClient } from 'mqtt'
import { create } from 'zustand'

interface NotificationMessage {
  eventId: string
  eventType: string
  timestamp: string
  source: string
  notificationId: number | null
  userId: number | null
  type: string
  title: string
  message: string
  referenceId: number | null
  referenceType: string | null
  imageUrl: string | null
  actionUrl: string | null
  isRead: boolean
  isBroadcast: boolean  // true if this is a broadcast notification
  createdAt: string
}

interface MqttState {
  client: MqttClient | null
  isConnected: boolean
  isConnecting: boolean
  currentUserId: number | null
  notifications: NotificationMessage[]
  unreadCount: number
  connect: (userId: number, token: string) => void
  disconnect: () => void
  addNotification: (notification: NotificationMessage) => void
  markAsRead: (eventId: string) => void
  markAllAsRead: () => void
}

const MQTT_WS_URL = import.meta.env.VITE_MQTT_WS_URL || 'ws://localhost:8083/mqtt'

export const useMqttStore = create<MqttState>((set, get) => ({
  client: null,
  isConnected: false,
  isConnecting: false,
  currentUserId: null,
  notifications: [],
  unreadCount: 0,

  connect: (userId: number, token: string) => {
    const { client: existingClient, currentUserId, isConnected, isConnecting } = get()
    
    // Skip if already connecting or connected with same userId
    if (isConnecting) {
      // eslint-disable-next-line no-console
      console.log('[MQTT] Already connecting, skipping...')
      return
    }
    
    if (existingClient && isConnected && currentUserId === userId) {
      // eslint-disable-next-line no-console
      console.log('[MQTT] Already connected for user:', userId)
      return
    }
    
    // Set connecting flag
    set({ isConnecting: true })
    
    // Disconnect existing client if any
    if (existingClient) {
      // eslint-disable-next-line no-console
      console.log(`[MQTT] [${new Date().toISOString()}] DISCONNECTING EXISTING | Previous User: ${currentUserId}`)
      existingClient.end()
    }

    const clientId = `user-${userId}-${Date.now()}`
    
    // eslint-disable-next-line no-console
    console.log(`[MQTT] [${new Date().toISOString()}] INIT CONNECTION | URL: ${MQTT_WS_URL} | User: ${userId} | ClientID: ${clientId}`)
    
    const newClient = mqtt.connect(MQTT_WS_URL, {
      clientId,
      username: `user-${userId}`,
      password: token || 'dev-mode',  // Fallback for dev mode
      clean: true,
      reconnectPeriod: 5000,
      connectTimeout: 30000,
    })

    newClient.on('connect', () => {
      // eslint-disable-next-line no-console
      console.log(`[MQTT] [${new Date().toISOString()}] CONNECTED | User: ${userId} | ClientID: ${clientId} | URL: ${MQTT_WS_URL}`)
      set({ isConnected: true, isConnecting: false, currentUserId: userId })

      // Subscribe to user's notification topics
      const userTopic = `notifications/${userId}/#`
      newClient.subscribe(userTopic, (err) => {
        if (err) {
          // eslint-disable-next-line no-console
          console.error(`[MQTT] [${new Date().toISOString()}] SUBSCRIBE FAILED | Topic: ${userTopic} | Error:`, err.message)
        } else {
          // eslint-disable-next-line no-console
          console.log(`[MQTT] [${new Date().toISOString()}] SUBSCRIBED | Topic: ${userTopic} | Type: USER_SPECIFIC`)
        }
      })

      // Subscribe to broadcast topics (for all users)
      const broadcastTopic = 'notifications/broadcast/#'
      newClient.subscribe(broadcastTopic, (err) => {
        if (err) {
          // eslint-disable-next-line no-console
          console.error(`[MQTT] [${new Date().toISOString()}] SUBSCRIBE FAILED | Topic: ${broadcastTopic} | Error:`, err.message)
        } else {
          // eslint-disable-next-line no-console
          console.log(`[MQTT] [${new Date().toISOString()}] SUBSCRIBED | Topic: ${broadcastTopic} | Type: BROADCAST`)
        }
      })
    })

    newClient.on('message', (topic, payload) => {
      try {
        const notification: NotificationMessage = JSON.parse(payload.toString())
        const timestamp = new Date().toISOString()
        const eventCategory = topic.includes('/broadcast/') ? 'BROADCAST' : 'USER_DIRECT'

        // eslint-disable-next-line no-console
        console.log(`[MQTT] [${timestamp}] MESSAGE RECEIVED | Topic: ${topic} | Category: ${eventCategory}`)
        // eslint-disable-next-line no-console
        console.log(`  └─ EventID: ${notification.eventId}`)
        // eslint-disable-next-line no-console
        console.log(`  └─ EventType: ${notification.eventType}`)
        // eslint-disable-next-line no-console
        console.log(`  └─ Type: ${notification.type}`)
        // eslint-disable-next-line no-console
        console.log(`  └─ Title: ${notification.title}`)
        // eslint-disable-next-line no-console
        console.log(`  └─ Source: ${notification.source}`)
        // eslint-disable-next-line no-console
        console.log(`  └─ UserID: ${notification.userId || 'null'} | Ref: ${notification.referenceType}:${notification.referenceId || 'null'}`)

        set((state) => ({
          notifications: [notification, ...state.notifications].slice(0, 50),
          unreadCount: state.unreadCount + 1,
        }))
      } catch (error) {
        // eslint-disable-next-line no-console
        console.error(`[MQTT] [${new Date().toISOString()}] PARSE ERROR | Topic: ${topic} | Payload:`, payload.toString().substring(0, 200), error)
      }
    })

    newClient.on('error', (error) => {
      // eslint-disable-next-line no-console
      console.error(`[MQTT] [${new Date().toISOString()}] CONNECTION ERROR | User: ${userId} | Error:`, error.message || error)
    })

    newClient.on('close', () => {
      // eslint-disable-next-line no-console
      console.log(`[MQTT] [${new Date().toISOString()}] CONNECTION CLOSED | User: ${userId || 'unknown'}`)
      set({ isConnected: false, isConnecting: false })
    })

    newClient.on('reconnect', () => {
      // eslint-disable-next-line no-console
      console.log(`[MQTT] [${new Date().toISOString()}] RECONNECTING | User: ${userId} | ClientID: ${clientId}`)
    })

    set({ client: newClient })
  },

  disconnect: () => {
    const { client } = get()
    if (client) {
      // eslint-disable-next-line no-console
      console.log(`[MQTT] [${new Date().toISOString()}] USER DISCONNECT | User: ${get().currentUserId || 'unknown'}`)
      client.end()
      set({ client: null, isConnected: false, isConnecting: false, currentUserId: null })
    }
  },

  addNotification: (notification) => {
    set((state) => ({
      notifications: [notification, ...state.notifications].slice(0, 50),
      unreadCount: state.unreadCount + 1,
    }))
  },

  markAsRead: (eventId: string) => {
    set((state) => {
      const notifications = state.notifications.map((n) =>
        n.eventId === eventId ? { ...n, isRead: true } : n
      )
      const unreadCount = notifications.filter((n) => !n.isRead).length
      return { notifications, unreadCount }
    })
  },

  markAllAsRead: () => {
    set((state) => ({
      notifications: state.notifications.map((n) => ({ ...n, isRead: true })),
      unreadCount: 0,
    }))
  },
}))
