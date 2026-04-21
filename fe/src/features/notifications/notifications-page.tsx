import { Bell, Check, Package, Truck, CheckCircle, XCircle, Megaphone, Gift, Radio, ExternalLink, ArrowLeft } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { ScrollArea } from '@/components/ui/scroll-area'
import { useNotifications, useMarkAllNotificationsAsRead, useMarkNotificationAsRead } from '@/hooks/use-notification'
import { useMqttStore } from '@/lib/mqtt-client'
import { formatDistanceToNow, format } from 'date-fns'
import { vi } from 'date-fns/locale'
import { cn } from '@/lib/utils'
import { Link, useSearch } from '@tanstack/react-router'
import { useState, useMemo, useEffect, useRef } from 'react'
import type { NotificationResponse } from '@/types/api'

type NotificationItem = NotificationResponse & {
  eventId?: string
  eventType?: string
  timestamp?: string
  source?: string
  notificationId?: number | null
}

const getNotificationIcon = (type: string, isBroadcast?: boolean) => {
  if (isBroadcast) {
    return <Radio className="h-5 w-5 text-red-500" />
  }
  switch (type) {
    case 'ORDER_CONFIRMED':
    case 'ORDER_STATUS':
      return <Package className="h-5 w-5 text-blue-500" />
    case 'ORDER_SHIPPED':
      return <Truck className="h-5 w-5 text-orange-500" />
    case 'ORDER_DELIVERED':
      return <CheckCircle className="h-5 w-5 text-green-500" />
    case 'ORDER_CANCELLED':
      return <XCircle className="h-5 w-5 text-red-500" />
    case 'SYSTEM_ANNOUNCEMENT':
      return <Megaphone className="h-5 w-5 text-purple-500" />
    case 'PROMOTION':
      return <Gift className="h-5 w-5 text-pink-500" />
    default:
      return <Bell className="h-5 w-5 text-gray-500" />
  }
}

const getNotificationColor = (type: string, isBroadcast?: boolean) => {
  if (isBroadcast) return 'bg-red-100 text-red-600'
  switch (type) {
    case 'ORDER_CONFIRMED':
    case 'ORDER_STATUS':
      return 'bg-blue-100 text-blue-600'
    case 'ORDER_SHIPPED':
      return 'bg-orange-100 text-orange-600'
    case 'ORDER_DELIVERED':
      return 'bg-green-100 text-green-600'
    case 'ORDER_CANCELLED':
      return 'bg-red-100 text-red-600'
    case 'SYSTEM_ANNOUNCEMENT':
      return 'bg-purple-100 text-purple-600'
    case 'PROMOTION':
      return 'bg-pink-100 text-pink-600'
    default:
      return 'bg-gray-100 text-gray-600'
  }
}

export function NotificationsPage() {
  const search = useSearch({ from: '/notifications' })
  const [selectedId, setSelectedId] = useState<number | null>(search.id ?? null)
  const markedIdRef = useRef<number | null>(null)
  const { data: notificationsData, isLoading } = useNotifications(0, 50)
  const { notifications: realtimeNotifications } = useMqttStore()
  const markAllAsRead = useMarkAllNotificationsAsRead()
  const markAsRead = useMarkNotificationAsRead()

  // Merge realtime with API data
  const notifications: NotificationItem[] = useMemo(() => {
    const mergedMap = new Map<string, NotificationItem>()
    
    const apiNotifications = notificationsData?.content || []
    apiNotifications.forEach((n) => {
      mergedMap.set(`api-${n.id}`, n)
    })
    
    if (realtimeNotifications.length > 0) {
      realtimeNotifications.forEach((n) => {
        const key = n.notificationId ? `api-${n.notificationId}` : `mqtt-${n.eventId}`
        mergedMap.set(key, {
          id: n.notificationId || parseInt(n.eventId) || 0,
          eventId: n.eventId,
          eventType: n.eventType,
          timestamp: n.timestamp,
          source: n.source,
          notificationId: n.notificationId,
          userId: n.userId,
          type: n.type,
          title: n.title,
          message: n.message,
          referenceId: n.referenceId,
          referenceType: n.referenceType,
          imageUrl: n.imageUrl,
          actionUrl: n.actionUrl,
          isRead: n.isRead,
          isBroadcast: n.isBroadcast ?? false,
          createdAt: n.createdAt,
        })
      })
    }
    
    return Array.from(mergedMap.values())
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }, [realtimeNotifications, notificationsData?.content])

  // Mark as read when coming from dropdown (only once per id)
  useEffect(() => {
    if (search.id && markedIdRef.current !== search.id) {
      markedIdRef.current = search.id
      const notification = notifications.find((n) => n.id === search.id || n.notificationId === search.id)
      if (notification && !notification.isRead) {
        markAsRead.mutate([search.id])
      }
    }
  }, [search.id, notifications, markAsRead])

  const unreadCount = notifications.filter((n) => !n.isRead).length
  const selectedNotification = notifications.find((n) => n.id === selectedId || n.notificationId === selectedId)

  const handleSelectNotification = (notification: NotificationItem) => {
    const id = notification.id || notification.notificationId
    setSelectedId(id || null)
    
    // Mark as read when selected
    if (!notification.isRead && id) {
      markAsRead.mutate([id])
    }
  }

  const handleMarkAllRead = () => {
    markAllAsRead.mutate()
  }

  return (
    <div className="h-[calc(100vh-4rem)] flex flex-col">
      {/* Header */}
      <div className="border-b px-6 py-4 flex items-center justify-between bg-background">
        <div>
          <h1 className="text-2xl font-bold">Thông báo</h1>
          <p className="text-sm text-muted-foreground">
            {unreadCount > 0
              ? `Bạn có ${unreadCount} thông báo chưa đọc`
              : 'Tất cả thông báo đã được đọc'}
          </p>
        </div>
        {unreadCount > 0 && (
          <Button variant="outline" size="sm" onClick={handleMarkAllRead} disabled={markAllAsRead.isPending}>
            <Check className="mr-2 h-4 w-4" />
            Đánh dấu tất cả đã đọc
          </Button>
        )}
      </div>

      {/* Main content */}
      <div className="flex-1 flex overflow-hidden">
        {/* Left panel - Notification list */}
        <div className={cn(
          "border-r flex flex-col bg-muted/30",
          selectedId ? "hidden md:flex md:w-[360px]" : "w-full md:w-[360px]"
        )}>
          {/* Mobile back button */}
          {selectedId && (
            <Button
              variant="ghost"
              size="sm"
              className="md:hidden m-2"
              onClick={() => setSelectedId(null)}
            >
              <ArrowLeft className="mr-2 h-4 w-4" />
              Quay lại
            </Button>
          )}
          
          <ScrollArea className="flex-1">
            {isLoading ? (
              <div className="flex items-center justify-center h-[200px] text-muted-foreground">
                Đang tải...
              </div>
            ) : notifications.length === 0 ? (
              <div className="flex flex-col items-center justify-center h-[300px] text-muted-foreground">
                <Bell className="h-12 w-12 mb-3 opacity-50" />
                <p>Không có thông báo</p>
              </div>
            ) : (
              <div className="divide-y">
                {notifications.map((notification) => {
                  const id = notification.id || notification.notificationId
                  const isSelected = id === selectedId
                  
                  return (
                    <button
                      key={id}
                      onClick={() => handleSelectNotification(notification)}
                      className={cn(
                        "w-full p-4 text-left hover:bg-muted/50 transition-colors",
                        isSelected && "bg-muted",
                        !notification.isRead && "bg-primary/5"
                      )}
                    >
                      <div className="flex gap-3">
                        {/* Icon */}
                        <div className={cn(
                          "flex-shrink-0 flex items-center justify-center h-10 w-10 rounded-full",
                          getNotificationColor(notification.type, notification.isBroadcast)
                        )}>
                          {getNotificationIcon(notification.type, notification.isBroadcast)}
                        </div>
                        
                        {/* Content */}
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2 mb-1">
                            <p className={cn(
                              "text-sm font-medium truncate",
                              !notification.isRead && "font-semibold"
                            )}>
                              {notification.title}
                            </p>
                            {!notification.isRead && (
                              <span className="h-2 w-2 rounded-full bg-primary flex-shrink-0" />
                            )}
                          </div>
                          <p className="text-xs text-muted-foreground line-clamp-2 mb-1">
                            {notification.message}
                          </p>
                          <p className="text-[10px] text-muted-foreground">
                            {formatDistanceToNow(new Date(notification.createdAt), {
                              addSuffix: true,
                              locale: vi,
                            })}
                          </p>
                        </div>
                      </div>
                    </button>
                  )
                })}
              </div>
            )}
          </ScrollArea>
        </div>

        {/* Right panel - Notification detail */}
        {selectedNotification && (
          <div className="flex-1 flex flex-col bg-background">
            {/* Detail header */}
            <div className="border-b px-6 py-4 flex items-center gap-3">
              <Button
                variant="ghost"
                size="sm"
                className="md:hidden"
                onClick={() => setSelectedId(null)}
              >
                <ArrowLeft className="h-4 w-4" />
              </Button>
              <div className={cn(
                "flex items-center justify-center h-10 w-10 rounded-full",
                getNotificationColor(selectedNotification.type, selectedNotification.isBroadcast)
              )}>
                {getNotificationIcon(selectedNotification.type, selectedNotification.isBroadcast)}
              </div>
              <div className="flex-1">
                <div className="flex items-center gap-2">
                  <h2 className="font-semibold">{selectedNotification.title}</h2>
                  {selectedNotification.isBroadcast && (
                    <Badge variant="destructive" className="text-[10px]">Broadcast</Badge>
                  )}
                </div>
                <p className="text-xs text-muted-foreground">
                  {format(new Date(selectedNotification.createdAt), "HH:mm, dd/MM/yyyy", { locale: vi })}
                </p>
              </div>
            </div>

            {/* Detail content */}
            <ScrollArea className="flex-1">
              <div className="p-6">
                {/* Image if exists */}
                {selectedNotification.imageUrl && (
                  <div className="mb-6">
                    <img
                      src={selectedNotification.imageUrl}
                      alt={selectedNotification.title}
                      className="w-full max-h-[300px] object-cover rounded-lg"
                    />
                  </div>
                )}

                {/* Message */}
                <div className="prose prose-sm max-w-none">
                  <p className="text-base leading-relaxed whitespace-pre-wrap">
                    {selectedNotification.message}
                  </p>
                </div>

                {/* Reference info */}
                {selectedNotification.referenceId && selectedNotification.referenceType && (
                  <div className="mt-6 p-4 bg-muted/50 rounded-lg">
                    <p className="text-xs text-muted-foreground mb-1">Liên quan đến:</p>
                    <p className="text-sm font-medium">
                      {selectedNotification.referenceType}: #{selectedNotification.referenceId}
                    </p>
                  </div>
                )}

                {/* Action button */}
                {selectedNotification.actionUrl && (
                  <div className="mt-6">
                    <Button asChild>
                      <Link to={selectedNotification.actionUrl}>
                        Xem chi tiết
                        <ExternalLink className="ml-2 h-4 w-4" />
                      </Link>
                    </Button>
                  </div>
                )}
              </div>
            </ScrollArea>
          </div>
        )}

        {/* Empty state for right panel */}
        {!selectedNotification && (
          <div className="hidden md:flex flex-1 items-center justify-center bg-background">
            <div className="text-center text-muted-foreground">
              <Bell className="h-16 w-16 mx-auto mb-4 opacity-50" />
              <p className="text-lg font-medium">Chọn thông báo để xem chi tiết</p>
              <p className="text-sm">Hoặc tạo thông báo mới từ hộp thư</p>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
