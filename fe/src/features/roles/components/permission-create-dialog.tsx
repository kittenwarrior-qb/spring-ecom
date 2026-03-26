import { useState } from 'react'
import { Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useCreatePermission } from '@/hooks/use-roles'

export function PermissionCreateDialog() {
  const [open, setOpen] = useState(false)
  const [name, setName] = useState('')

  const createPermission = useCreatePermission()

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!name.trim()) return

    createPermission.mutate(
      { name },
      {
        onSuccess: () => {
          setOpen(false)
          setName('')
        },
      }
    )
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      {/* <DialogTrigger asChild>
        <Button>
          <Plus className='mr-2 h-4 w-4' /> Thêm Quyền
        </Button>
      </DialogTrigger> */}
      <DialogContent className='sm:max-w-[425px]'>
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Tạo Quyền Hạn Mới</DialogTitle>
            <DialogDescription>
              Nhập tên cho quyền hạn mới (VD: MANAGE_USERS).
            </DialogDescription>
          </DialogHeader>
          <div className='grid gap-4 py-4'>
            <div className='grid gap-2'>
              <Label htmlFor='perm-name'>Tên quyền hạn <span className="text-red-500">*</span></Label>
              <Input
                id='perm-name'
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder='VD: ORDER_CANCEL'
                required
              />
            </div>
          </div>
          <div className='flex justify-end'>
            <Button
              type='submit'
              disabled={createPermission.isPending || !name.trim()}
            >
              {createPermission.isPending ? 'Đang tạo...' : 'Tạo mới'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
}
