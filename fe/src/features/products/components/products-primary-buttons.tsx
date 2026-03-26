import { Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useProductsContext } from './products-provider'

export function ProductsPrimaryButtons() {
  const { setIsCreateOpen } = useProductsContext()

  return (
    <Button onClick={() => setIsCreateOpen(true)}>
      <Plus className='mr-2 h-4 w-4' />
      Thêm Sản Phẩm
    </Button>
  )
}
