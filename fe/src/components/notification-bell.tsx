import { Bell, Package, Truck, CheckCircle, XCircle, Gift, Megaphone, BellRing } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { ScrollArea } from '@/components/ui/scroll-area'
import { useMqttStore } from '@/lib/mqtt-client'
import { useNotifications, useMarkAllNotificationsAsRead } from '@/hooks/use-notification'
import { formatDistanceToNow } from 'date-fns'
import { vi } from 'date-fns/locale'
import { cn } from '@/lib/utils'
import { Link, useNavigate } from '@tanstack/react-router'
import { useAuth, useUser } from '@/stores/auth-store'

interface NotificationBellProps {
  className?: string
}

// Status color mapping
const getStatusColor = (type: string) => {
  switch (type) {
    case 'ORDER_CONFIRMED':
      return 'text-blue-600 bg-blue-50'
    case 'ORDER_STATUS':
      return 'text-orange-600 bg-orange-50'
    case 'ORDER_SHIPPED':
      return 'text-purple-600 bg-purple-50'
    case 'ORDER_DELIVERED':
      return 'text-green-600 bg-green-50'
    case 'ORDER_CANCELLED':
      return 'text-red-600 bg-red-50'
    case 'NEW_COUPON':
    case 'PROMOTION':
      return 'text-pink-600 bg-pink-50'
    case 'SYSTEM_ANNOUNCEMENT':
      return 'text-indigo-600 bg-indigo-50'
    default:
      return 'text-gray-600 bg-gray-50'
  }
}

// Status icon mapping with Lucide icons
const getStatusIcon = (type: string) => {
  switch (type) {
    case 'ORDER_CONFIRMED':
      return <Package className="h-5 w-5" />
    case 'ORDER_STATUS':
      return <Package className="h-5 w-5" />
    case 'ORDER_SHIPPED':
      return <Truck className="h-5 w-5" />
    case 'ORDER_DELIVERED':
      return <CheckCircle className="h-5 w-5" />
    case 'ORDER_CANCELLED':
      return <XCircle className="h-5 w-5" />
    case 'NEW_COUPON':
    case 'PROMOTION':
      return <Gift className="h-5 w-5" />
    case 'SYSTEM_ANNOUNCEMENT':
      return <Megaphone className="h-5 w-5" />
    default:
      return <BellRing className="h-5 w-5" />
  }
}

// Status badge text
const getStatusBadge = (type: string) => {
  switch (type) {
    case 'ORDER_CONFIRMED':
      return { text: 'Đã xác nhận', variant: 'default' as const }
    case 'ORDER_STATUS':
      return { text: 'Đang xử lý', variant: 'secondary' as const }
    case 'ORDER_SHIPPED':
      return { text: 'Đang giao', variant: 'default' as const }
    case 'ORDER_DELIVERED':
      return { text: 'Hoàn thành', variant: 'success' as const }
    case 'ORDER_CANCELLED':
      return { text: 'Đã hủy', variant: 'destructive' as const }
    case 'NEW_COUPON':
      return { text: 'Coupon mới', variant: 'default' as const }
    case 'PROMOTION':
      return { text: 'Khuyến mãi', variant: 'default' as const }
    default:
      return null
  }
}

export function NotificationBell({ className }: NotificationBellProps) {
  const user = useUser()
  const auth = useAuth()
  const navigate = useNavigate()
  const { unreadCount, notifications: realtimeNotifications } = useMqttStore()
  const { data: notificationsData } = useNotifications(0, 10)
  const markAllAsRead = useMarkAllNotificationsAsRead()

  const isAuthenticated = auth.accessToken && user

  // Merge realtime notifications with API notifications
  const notifications = realtimeNotifications.length > 0
    ? realtimeNotifications
    : notificationsData?.content || []

  const unread = notifications.filter((n) => !n.isRead).length
  const displayCount = unreadCount || unread

  const handleMarkAllRead = () => {
    markAllAsRead.mutate()
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
              {notifications.map((notification) => {
                const key = 'eventId' in notification ? notification.eventId : notification.id
                const statusBadge = getStatusBadge(notification.type)
                const statusColor = getStatusColor(notification.type)
                const statusIcon = getStatusIcon(notification.type)

                return (
                  <Link
                    key={key}
                    to={notification.actionUrl || '/notifications'}
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
              })}
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
