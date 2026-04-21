import { Bell, Package, Truck, CheckCircle, XCircle, Gift, Megaphone, BellRing, Radio } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { ScrollArea } from '@/components/ui/scroll-area'
import { useMqttStore } from '@/lib/mqtt-client'
import { useNotifications, useMarkNotificationAsRead, useMarkAllNotificationsAsRead } from '@/hooks/use-notification'
import { formatDistanceToNow } from 'date-fns'
import { vi } from 'date-fns/locale'
import { cn } from '@/lib/utils'
import { Link, useNavigate } from '@tanstack/react-router'
import { useAuth, useUser } from '@/stores/auth-store'
import { memo, useMemo } from 'react'

interface NotificationBellProps {
  className?: string
}

// Status color mapping - static object for performance
const STATUS_COLORS: Record<string, string> = {
  ORDER_CONFIRMED: 'text-blue-600 bg-blue-50',
  ORDER_STATUS: 'text-orange-600 bg-orange-50',
  ORDER_SHIPPED: 'text-purple-600 bg-purple-50',
  ORDER_DELIVERED: 'text-green-600 bg-green-50',
  ORDER_CANCELLED: 'text-red-600 bg-red-50',
  NEW_COUPON: 'text-pink-600 bg-pink-50',
  PROMOTION: 'text-pink-600 bg-pink-50',
  SYSTEM_ANNOUNCEMENT: 'text-indigo-600 bg-indigo-50',
}

const getStatusColor = (type: string) => STATUS_COLORS[type] || 'text-gray-600 bg-gray-50'

// Status icon mapping - static object for performance
const STATUS_ICONS: Record<string, React.ReactNode> = {
  ORDER_CONFIRMED: <Package className="h-5 w-5" />,
  ORDER_STATUS: <Package className="h-5 w-5" />,
  ORDER_SHIPPED: <Truck className="h-5 w-5" />,
  ORDER_DELIVERED: <CheckCircle className="h-5 w-5" />,
  ORDER_CANCELLED: <XCircle className="h-5 w-5" />,
  NEW_COUPON: <Gift className="h-5 w-5" />,
  PROMOTION: <Gift className="h-5 w-5" />,
  SYSTEM_ANNOUNCEMENT: <Megaphone className="h-5 w-5" />,
}

const getStatusIcon = (type: string) => STATUS_ICONS[type] || <BellRing className="h-5 w-5" />

// Status badge text - static object for performance
const STATUS_BADGES: Record<string, { text: string; variant: 'default' | 'secondary' | 'destructive' }> = {
  ORDER_CONFIRMED: { text: 'Đã xác nhận', variant: 'default' },
  ORDER_STATUS: { text: 'Đang xử lý', variant: 'secondary' },
  ORDER_SHIPPED: { text: 'Đang giao', variant: 'default' },
  ORDER_DELIVERED: { text: 'Hoàn thành', variant: 'default' },
  ORDER_CANCELLED: { text: 'Đã hủy', variant: 'destructive' },
  NEW_COUPON: { text: 'Coupon mới', variant: 'default' },
  PROMOTION: { text: 'Khuyến mãi', variant: 'default' },
}

const getStatusBadge = (type: string) => STATUS_BADGES[type] || null

// Memoized notification item component for performance
interface NotificationItemProps {
  notification: {
    id?: number
    notificationId?: number
    eventId?: string
    type: string
    title: string
    message: string
    actionUrl?: string | null
    isRead: boolean
    isBroadcast?: boolean
    createdAt: string
  }
  onRead?: (id: number) => void
}

const NotificationItem = memo(function NotificationItem({ notification, onRead }: NotificationItemProps) {
  const statusBadge = getStatusBadge(notification.type)
  const statusColor = notification.isBroadcast ? 'text-red-600 bg-red-50' : getStatusColor(notification.type)
  const statusIcon = notification.isBroadcast ? <Radio className="h-5 w-5" /> : getStatusIcon(notification.type)

  const handleClick = () => {
    // Mark as read when clicked
    const notificationId = notification.id || notification.notificationId
    if (!notification.isRead && notificationId && onRead) {
      onRead(notificationId)
    }
  }

  const notificationId = notification.id || notification.notificationId

  return (
    <Link
      to="/notifications"
      search={{ id: notificationId }}
      onClick={handleClick}
      className={cn(
        'flex gap-3 border-b p-4 hover:bg-muted/50 transition-colors',
        !notification.isRead && 'bg-primary/5'
      )}
    >
      {/* Icon with background */}
      <div className={cn(
        'flex-shrink-0 flex items-center justify-center h-10 w-10 rounded-full',
        statusColor
      )}>
        {statusIcon}
      </div>

      {/* Content */}
      <div className="flex-1 min-w-0 space-y-1.5">
        {/* Title with badge */}
        <div className="flex items-center gap-2 flex-wrap">
          <p className="text-sm font-semibold leading-none truncate flex-1">
            {notification.title}
          </p>
          {notification.isBroadcast && (
            <Badge variant="destructive" className="text-[10px] px-1.5 py-0 h-auto">
              Broadcast
            </Badge>
          )}
          {statusBadge && (
            <Badge
              variant={statusBadge.variant}
              className="text-[10px] px-2 py-0.5 h-auto"
            >
              {statusBadge.text}
            </Badge>
          )}
        </div>

        {/* Message */}
        <p className="text-xs text-muted-foreground line-clamp-2 leading-relaxed">
          {notification.message}
        </p>

        {/* Time */}
        <div className="flex items-center gap-2">
          <p className="text-[10px] text-muted-foreground">
            {formatDistanceToNow(new Date(notification.createdAt), {
              addSuffix: true,
              locale: vi,
            })}
          </p>
          {!notification.isRead && (
            <span className="h-1.5 w-1.5 rounded-full bg-primary animate-pulse" />
          )}
        </div>
      </div>
    </Link>
  )
})

export function NotificationBell({ className }: NotificationBellProps) {
  const user = useUser()
  const auth = useAuth()
  const navigate = useNavigate()
  const { notifications: realtimeNotifications } = useMqttStore()
  const { data: notificationsData } = useNotifications(0, 10)
  const markAsRead = useMarkNotificationAsRead()
  const markAllAsRead = useMarkAllNotificationsAsRead()

  const isAuthenticated = auth.accessToken && user

  // Merge realtime MQTT notifications with API notifications
  // Dedup by eventId (MQTT) or id (API)
  const notifications = useMemo(() => {
    const mergedMap = new Map<string, typeof realtimeNotifications[0]>()
    
    // Add API notifications first
    const apiNotifications = notificationsData?.content || []
    apiNotifications.forEach((n) => {
      mergedMap.set(`api-${n.id}`, {
        ...n,
        eventId: n.id.toString(),
        eventType: n.type,
        timestamp: n.createdAt,
        source: 'api',
        notificationId: n.id,
      })
    })
    
    // Override/add MQTT notifications (more recent)
    realtimeNotifications.forEach((n) => {
      const key = n.notificationId 
        ? `api-${n.notificationId}` 
        : `mqtt-${n.eventId}`
      mergedMap.set(key, n)
    })
    
    return Array.from(mergedMap.values())
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 50)
  }, [realtimeNotifications, notificationsData?.content])

  const unread = notifications.filter((n) => !n.isRead).length
  const displayCount = unread

  const handleMarkAllRead = () => {
    markAllAsRead.mutate()
  }

  const handleMarkAsRead = (id: number) => {
    markAsRead.mutate([id])
  }

  // If not authenticated, show simple button that redirects to login
  if (!isAuthenticated) {
    return (
      <Button
        variant="ghost"
        size="icon"
        className={cn('relative', className)}
        onClick={() => navigate({ to: '/sign-in', search: { redirect: undefined } })}
      >
        <Bell className="h-5 w-5" />
        <span className="sr-only">Đăng nhập để xem thông báo</span>
      </Button>
    )
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className={cn('relative', className)}
        >
          <Bell className="h-5 w-5" />
          {displayCount > 0 && (
            <Badge className="absolute -top-1 -right-1 bg-red-600 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center font-semibold animate-pulse">
              {displayCount > 9 ? '9+' : displayCount}
            </Badge>
          )}
          <span className="sr-only">Thông báo</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-96">
        <div className="flex items-center justify-between border-b px-4 py-3">
          <h4 className="font-semibold text-base">Thông báo</h4>
          {displayCount > 0 && (
            <Button
              variant="ghost"
              size="sm"
              onClick={handleMarkAllRead}
              className="h-auto px-3 py-1 text-xs text-primary hover:bg-primary/10"
            >
              Đánh dấu đã đọc
            </Button>
          )}
        </div>
        <ScrollArea className="h-[400px]">
          {notifications.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-[300px] text-muted-foreground">
              <BellRing className="h-12 w-12 mb-3 opacity-50" />
              <p className="text-sm">Không có thông báo</p>
            </div>
          ) : (
            <div className="flex flex-col">
              {notifications.map((notification) => (
                <NotificationItem
                  key={notification.eventId || notification.notificationId}
                  notification={notification}
                  onRead={handleMarkAsRead}
                />
              ))}
            </div>
          )}
        </ScrollArea>
        <div className="border-t p-2">
          <Button variant="ghost" size="sm" className="w-full" asChild>
            <Link to="/notifications">Xem tất cả thông báo</Link>
          </Button>
        </div>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
