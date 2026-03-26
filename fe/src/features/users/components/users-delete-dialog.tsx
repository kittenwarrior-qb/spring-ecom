'use client'

import { AlertTriangle, Loader2 } from 'lucide-react'
import { toast } from 'sonner'
import { useDeleteUser } from '@/hooks/use-user'
import { handleServerError } from '@/lib/handle-server-error'
import { ConfirmDialog } from '@/components/confirm-dialog'
import { type User } from '../data/schema'

type UserDeleteDialogProps = {
  open: boolean
  onOpenChange: (open: boolean) => void
  currentRow: User
}

export function UsersDeleteDialog({
  open,
  onOpenChange,
  currentRow,
}: UserDeleteDialogProps) {
  const deleteUser = useDeleteUser()

  const handleDelete = async () => {
    try {
      await deleteUser.mutateAsync(currentRow.id)
      toast.success(`Người dùng "${currentRow.username}" đã được xóa`)
      onOpenChange(false)
    } catch (error) {
      handleServerError(error)
    }
  }

  return (
    <ConfirmDialog
      open={open}
      onOpenChange={onOpenChange}
      handleConfirm={handleDelete}
      disabled={deleteUser.isPending}
      title={
        <span className='text-destructive'>
          <AlertTriangle
            className='me-1 inline-block stroke-destructive'
            size={18}
          />{' '}
          Xóa Người Dùng
        </span>
      }
      desc={
        <div className='space-y-4'>
          <p className='mb-2'>
            Bạn có chắc chắn muốn vô hiệu hóa và xóa người dùng{' '}
            <span className='font-bold'>{currentRow.username}</span>?
            <br />
            Người dùng sẽ bị đổi trạng thái thành "Đã xóa".
          </p>
        </div>
      }
      confirmText={deleteUser.isPending ? <Loader2 className='h-4 w-4 animate-spin' /> : 'Xóa'}
      destructive
    />
  )
}
