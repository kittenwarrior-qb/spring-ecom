import { useEffect } from 'react'
import { toast } from 'sonner'
import { useMqttStore } from '@/lib/mqtt-client'

export function useNotificationToast() {
  const { notifications } = useMqttStore()

  useEffect(() => {
    // Only show toast for the latest notification
    if (notifications.length > 0) {
      const latest = notifications[0]
      
      // Check if this is a new notification (less than 5 seconds old)
      const notificationTime = new Date(latest.createdAt).getTime()
      const now = new Date().getTime()
      const isRecent = (now - notificationTime) < 5000

      if (isRecent && !latest.isRead) {
        toast(latest.title, {
          description: latest.message,
          action: latest.actionUrl ? {
            label: 'Xem',
            onClick: () => {
              if (latest.actionUrl) {
                window.location.href = latest.actionUrl
              }
            }
          } : undefined,
          duration: 5000,
        })
      }
    }
  }, [notifications])
}
