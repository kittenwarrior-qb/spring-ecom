'use client'

import { useState } from 'react'
import { AlertTriangle, Loader2 } from 'lucide-react'
import { toast } from 'sonner'
import { useDeleteUser } from '@/hooks/use-user'
import { handleServerError } from '@/lib/handle-server-error'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
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
  const [value, setValue] = useState('')
  const deleteUser = useDeleteUser()

  const handleDelete = async () => {
    if (value.trim() !== currentRow.username) return

    try {
      await deleteUser.mutateAsync(currentRow.id)
      toast.success(`Người dùng "${currentRow.username}" đã được xóa`)
      setValue('')
      onOpenChange(false)
    } catch (error) {
      handleServerError(error)
    }
  }

  return (
    <ConfirmDialog
      open={open}
      onOpenChange={(state) => {
        if (!state) setValue('')
        onOpenChange(state)
      }}
      handleConfirm={handleDelete}
      disabled={value.trim() !== currentRow.username || deleteUser.isPending}
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
            Bạn có chắc chắn muốn xóa{' '}
            <span className='font-bold'>{currentRow.username}</span>?
            <br />
            Hành động này sẽ xóa vĩnh viễn người dùng với vai trò{' '}
            <span className='font-bold'>
              {currentRow.role}
            </span>{' '}
            khỏi hệ thống. Điều này không thể hoàn tác.
          </p>

          <Label className='my-2'>
            Tên đăng nhập:
            <Input
              value={value}
              onChange={(e) => setValue(e.target.value)}
              placeholder='Nhập tên đăng nhập để xác nhận xóa.'
            />
          </Label>

          <Alert variant='destructive'>
            <AlertTitle>Warning!</AlertTitle>
            <AlertDescription>
              Please be careful, this operation can not be rolled back.
            </AlertDescription>
          </Alert>
        </div>
      }
      confirmText={deleteUser.isPending ? <Loader2 className='h-4 w-4 animate-spin' /> : 'Xóa'}
      destructive
    />
  )
}
