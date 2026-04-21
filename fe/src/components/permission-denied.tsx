import { ShieldX } from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

interface PermissionDeniedProps {
  permission?: string
  message?: string
}

export function PermissionDenied({ permission, message }: PermissionDeniedProps) {
  return (
    <div className="flex items-center justify-center min-h-[400px]">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-muted">
            <ShieldX className="h-8 w-8 text-muted-foreground" />
          </div>
          <CardTitle className="text-xl">Không đủ thẩm quyền</CardTitle>
        </CardHeader>
        <CardContent className="text-center">
          <p className="text-muted-foreground">
            {message || `Bạn không có quyền truy cập chức năng này.${permission ? ` (Can permission: ${permission})` : ''}`}
          </p>
          <p className="text-sm text-muted-foreground mt-2">
            Vui lòng liên hệ quản trị viên để được cấp quyền.
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
