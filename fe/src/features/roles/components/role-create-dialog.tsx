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
import { Checkbox } from '@/components/ui/checkbox'
import { useCreateRole, usePermissions } from '@/hooks/use-roles'

export function RoleCreateDialog() {
  const [open, setOpen] = useState(false)
  const [name, setName] = useState('')
  const [selectedPermissions, setSelectedPermissions] = useState<number[]>([])

  const { data: permissions } = usePermissions()
  const createRole = useCreateRole()

  const handleTogglePermission = (id: number) => {
    setSelectedPermissions((prev) =>
      prev.includes(id) ? prev.filter((p) => p !== id) : [...prev, id]
    )
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!name.trim()) return

    createRole.mutate(
      { name, permissionIds: selectedPermissions },
      {
        onSuccess: () => {
          setOpen(false)
          setName('')
          setSelectedPermissions([])
        },
      }
    )
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className='mr-2 h-4 w-4' /> Thêm Vai Trò
        </Button>
      </DialogTrigger>
      <DialogContent className='sm:max-w-[425px]'>
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Tạo Vai Trò Mới</DialogTitle>
            <DialogDescription>
              Nhập tên vai trò và chọn các quyền tương ứng.
            </DialogDescription>
          </DialogHeader>
          <div className='grid gap-4 py-4'>
            <div className='grid gap-2'>
              <Label htmlFor='name'>Tên vai trò <span className="text-red-500">*</span></Label>
              <Input
                id='name'
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder='VD: MANAGER'
                required
              />
            </div>
            <div className='grid gap-2'>
              <Label>Các quyền (Permissions)</Label>
              <div className='max-h-[200px] overflow-y-auto rounded-md border p-2'>
                {permissions?.map((permission) => (
                  <div key={permission.id} className='flex items-center space-x-2 py-1'>
                    <Checkbox
                      id={`perm-${permission.id}`}
                      checked={selectedPermissions.includes(permission.id)}
                      onCheckedChange={() => handleTogglePermission(permission.id)}
                    />
                    <label
                      htmlFor={`perm-${permission.id}`}
                      className='text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70'
                    >
                      {permission.name}
                    </label>
                  </div>
                ))}
                {(!permissions || permissions.length === 0) && (
                  <div className='text-sm text-muted-foreground p-2'>Không có quyền nào</div>
                )}
              </div>
            </div>
          </div>
          <div className='flex justify-end'>
            <Button
              type='submit'
              disabled={createRole.isPending || !name.trim()}
            >
              {createRole.isPending ? 'Đang tạo...' : 'Tạo mới'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
}
