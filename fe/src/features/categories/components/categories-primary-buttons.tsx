import { Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useCategoriesContext } from './categories-provider'

export function CategoriesPrimaryButtons() {
  const { setIsCreateOpen } = useCategoriesContext()

  return (
    <Button onClick={() => setIsCreateOpen(true)}>
      <Plus className='mr-2 h-4 w-4' />
      Thêm Danh Mục
    </Button>
  )
}
