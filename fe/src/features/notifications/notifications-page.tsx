import { Bell, Check, Package, Truck, CheckCircle, XCircle, Megaphone, Gift } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { useNotifications, useMarkAllNotificationsAsRead, useMarkNotificationAsRead } from '@/hooks/use-notification'
import { useMqttStore } from '@/lib/mqtt-client'
import { formatDistanceToNow } from 'date-fns'
import { vi } from 'date-fns/locale'
import { cn } from '@/lib/utils'
import { Link } from '@tanstack/react-router'
import { useState } from 'react'

interface NotificationItem {
  id: number
  type: string
  title: string
  message: string
  isRead: boolean
  actionUrl: string | null
  createdAt: string
}

export function NotificationsPage() {
  const [tab, setTab] = useState<'all' | 'unread'>('all')
  const { data: notificationsData, isLoading } = useNotifications(0, 50)
  const { notifications: realtimeNotifications } = useMqttStore()
  const markAllAsRead = useMarkAllNotificationsAsRead()
  const markAsRead = useMarkNotificationAsRead()

  // Merge realtime with API data
  const notifications: NotificationItem[] = realtimeNotifications.length > 0
    ? realtimeNotifications
    : notificationsData?.content || []

  const unreadNotifications = notifications.filter((n) => !n.isRead)

  const displayNotifications = tab === 'unread' ? unreadNotifications : notifications

  const handleMarkAllRead = () => {
    markAllAsRead.mutate()
  }

  const handleMarkAsRead = (id: number) => {
    markAsRead.mutate([id])
  }

  const getNotificationIcon = (type: string) => {
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

  return (
    <div className="container max-w-4xl py-8">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Thông báo</h1>
          <p className="text-muted-foreground">
            {unreadNotifications.length > 0
              ? `Bạn có ${unreadNotifications.length} thông báo chưa đọc`
              : 'Tất cả thông báo đã được đọc'}
          </p>
        </div>
        {unreadNotifications.length > 0 && (
          <Button variant="outline" onClick={handleMarkAllRead} disabled={markAllAsRead.isPending}>
            <Check className="mr-2 h-4 w-4" />
            Đánh dấu tất cả đã đọc
          </Button>
        )}
      </div>

      <Tabs value={tab} onValueChange={(v) => setTab(v as 'all' | 'unread')}>
        <TabsList className="mb-4">
          <TabsTrigger value="all">
            Tất cả ({notifications.length})
          </TabsTrigger>
          <TabsTrigger value="unread">
            Chưa đọc ({unreadNotifications.length})
          </TabsTrigger>
        </TabsList>

        <TabsContent value="all">
          <NotificationsList
            notifications={displayNotifications}
            isLoading={isLoading}
            onMarkAsRead={handleMarkAsRead}
            getNotificationIcon={getNotificationIcon}
          />
        </TabsContent>

        <TabsContent value="unread">
          <NotificationsList
            notifications={displayNotifications}
            isLoading={isLoading}
            onMarkAsRead={handleMarkAsRead}
            getNotificationIcon={getNotificationIcon}
          />
        </TabsContent>
      </Tabs>
    </div>
  )
}

interface NotificationsListProps {
  notifications: NotificationItem[]
  isLoading: boolean
  onMarkAsRead: (id: number) => void
  getNotificationIcon: (type: string) => React.ReactNode
}

function NotificationsList({ notifications, isLoading, onMarkAsRead, getNotificationIcon }: NotificationsListProps) {
  if (isLoading) {
    return (
      <div className="flex h-[400px] items-center justify-center">
        <div className="text-muted-foreground">Đang tải...</div>
      </div>
    )
  }

  if (notifications.length === 0) {
    return (
      <Card>
        <CardContent className="flex h-[400px] flex-col items-center justify-center gap-4">
          <Bell className="h-16 w-16 text-muted-foreground/50" />
          <div className="text-center">
            <p className="text-lg font-medium">Không có thông báo</p>
            <p className="text-muted-foreground">Bạn đã đọc tất cả thông báo</p>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <div className="space-y-3">
      {notifications.map((notification) => (
        <Card
          key={notification.id}
          className={cn(
            'transition-colors hover:bg-muted/50',
            !notification.isRead && 'border-l-4 border-l-primary bg-primary/5'
          )}
        >
          <CardContent className="flex items-start gap-4 p-4">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-muted">
              {getNotificationIcon(notification.type)}
            </div>
            
            <div className="flex-1 space-y-1">
              <div className="flex items-start justify-between gap-2">
                <div>
                  <p className="font-medium">{notification.title}</p>
                  <p className="text-sm text-muted-foreground">{notification.message}</p>
                </div>
                {!notification.isRead && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => onMarkAsRead(notification.id)}
                    className="shrink-0"
                  >
                    <Check className="h-4 w-4" />
                  </Button>
                )}
              </div>
              
              <div className="flex items-center gap-4 text-xs text-muted-foreground">
                <span>
                  {formatDistanceToNow(new Date(notification.createdAt), {
                    addSuffix: true,
                    locale: vi,
                  })}
                </span>
                {notification.actionUrl && (
                  <Link
                    to={notification.actionUrl}
                    className="text-primary hover:underline"
                  >
                    Xem chi tiết
                  </Link>
                )}
              </div>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
