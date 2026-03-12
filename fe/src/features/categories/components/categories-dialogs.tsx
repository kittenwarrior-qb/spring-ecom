import { useEffect } from 'react'
import { z } from 'zod'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Loader2 } from 'lucide-react'
import { toast } from 'sonner'
import { useCreateCategory, useUpdateCategory, useDeleteCategory } from '@/hooks/use-category'
import { handleServerError } from '@/lib/handle-server-error'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Switch } from '@/components/ui/switch'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { useCategoriesContext } from './categories-provider'

const categoryFormSchema = z.object({
  name: z.string().min(1, 'Name is required').max(100, 'Name must be at most 100 characters'),
  slug: z.string().min(1, 'Slug is required').max(100, 'Slug must be at most 100 characters'),
  description: z.string().max(500, 'Description must be at most 500 characters').optional(),
  parentId: z.number().nullable().optional(),
  displayOrder: z.number().int().min(0).nullable().optional(),
  isActive: z.boolean(),
})

type CategoryFormValues = z.input<typeof categoryFormSchema>

function CategoryFormDialog({
  open,
  onOpenChange,
  onSubmit,
  defaultValues,
  title,
  description,
  isLoading,
}: {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (data: CategoryFormValues) => Promise<void>
  defaultValues?: Partial<CategoryFormValues>
  title: string
  description: string
  isLoading: boolean
}) {
  const form = useForm<CategoryFormValues>({
    resolver: zodResolver(categoryFormSchema),
    defaultValues: {
      name: '',
      slug: '',
      description: '',
      parentId: null,
      displayOrder: null,
      isActive: true,
      ...defaultValues,
    },
  })

  // Reset form when dialog opens with new values
  useEffect(() => {
    if (open && defaultValues) {
      form.reset({
        name: '',
        slug: '',
        description: '',
        parentId: null,
        displayOrder: null,
        isActive: true,
        ...defaultValues,
      })
    }
  }, [open, defaultValues, form])

  // Auto-generate slug from name
  const name = form.watch('name')
  useEffect(() => {
    if (name && !form.getValues('slug')) {
      const slug = name
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/(^-|-$)/g, '')
      form.setValue('slug', slug)
    }
  }, [name, form])

  const handleSubmit = async (data: CategoryFormValues) => {
    try {
      await onSubmit(data)
      onOpenChange(false)
      form.reset()
    } catch (error) {
      handleServerError(error)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className='sm:max-w-[500px]'>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          <DialogDescription>{description}</DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className='space-y-4'>
            <FormField
              control={form.control}
              name='name'
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Name</FormLabel>
                  <FormControl>
                    <Input placeholder='Category name' {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name='slug'
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Slug</FormLabel>
                  <FormControl>
                    <Input placeholder='category-slug' {...field} />
                  </FormControl>
                  <FormDescription>
                    URL-friendly identifier. Auto-generated from name.
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name='description'
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <Textarea
                      placeholder='Category description (optional)'
                      className='resize-none'
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className='grid grid-cols-2 gap-4'>
              <FormField
                control={form.control}
                name='displayOrder'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Display Order</FormLabel>
                    <FormControl>
                      <Input
                        type='number'
                        placeholder='0'
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) => {
                          const value = e.target.value
                          field.onChange(value ? parseInt(value, 10) : null)
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='isActive'
                render={({ field }) => (
                  <FormItem className='flex flex-col justify-end'>
                    <div className='flex items-center space-x-2 pt-2'>
                      <FormControl>
                        <Switch
                          checked={field.value}
                          onCheckedChange={field.onChange}
                        />
                      </FormControl>
                      <FormLabel className='!mt-0'>Active</FormLabel>
                    </div>
                  </FormItem>
                )}
              />
            </div>
            <DialogFooter>
              <Button type='button' variant='outline' onClick={() => onOpenChange(false)}>
                Cancel
              </Button>
              <Button type='submit' disabled={isLoading}>
                {isLoading && <Loader2 className='mr-2 h-4 w-4 animate-spin' />}
                Save
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}

function CreateCategoryDialog() {
  const { isCreateOpen, setIsCreateOpen } = useCategoriesContext()
  const createCategory = useCreateCategory()

  const handleSubmit = async (data: CategoryFormValues) => {
    await createCategory.mutateAsync(data)
    toast.success('Category created successfully')
  }

  return (
    <CategoryFormDialog
      open={isCreateOpen}
      onOpenChange={setIsCreateOpen}
      onSubmit={handleSubmit}
      title='Create Category'
      description='Add a new category to organize your products.'
      isLoading={createCategory.isPending}
    />
  )
}

function EditCategoryDialog() {
  const { isEditOpen, setIsEditOpen, selectedCategory } = useCategoriesContext()
  const updateCategory = useUpdateCategory()

  const handleSubmit = async (data: CategoryFormValues) => {
    if (!selectedCategory) return
    await updateCategory.mutateAsync({ id: selectedCategory.id, data })
    toast.success('Category updated successfully')
  }

  return (
    <CategoryFormDialog
      open={isEditOpen}
      onOpenChange={setIsEditOpen}
      onSubmit={handleSubmit}
      defaultValues={
        selectedCategory
          ? {
              name: selectedCategory.name,
              slug: selectedCategory.slug,
              description: selectedCategory.description ?? '',
              parentId: selectedCategory.parentId,
              displayOrder: selectedCategory.displayOrder,
              isActive: selectedCategory.isActive,
            }
          : undefined
      }
      title='Chỉnh Sửa Danh Mục'
      description='Cập nhật thông tin danh mục.'
      isLoading={updateCategory.isPending}
    />
  )
}

function DeleteCategoryDialog() {
  const { isDeleteOpen, setIsDeleteOpen, selectedCategory, setSelectedCategory } = useCategoriesContext()
  const deleteCategory = useDeleteCategory()

  const handleDelete = async () => {
    if (!selectedCategory) return
    try {
      await deleteCategory.mutateAsync(selectedCategory.id)
      toast.success('Danh mục đã được xóa thành công')
      setIsDeleteOpen(false)
      setSelectedCategory(null)
    } catch (error) {
      handleServerError(error)
    }
  }

  return (
    <AlertDialog open={isDeleteOpen} onOpenChange={setIsDeleteOpen}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Xóa Danh Mục</AlertDialogTitle>
          <AlertDialogDescription>
            Bạn có chắc chắn muốn xóa "{selectedCategory?.name}"? Hành động này không thể hoàn tác.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Hủy</AlertDialogCancel>
          <AlertDialogAction
            onClick={handleDelete}
            className='bg-destructive text-destructive-foreground hover:bg-destructive/90'
            disabled={deleteCategory.isPending}
          >
            {deleteCategory.isPending && <Loader2 className='mr-2 h-4 w-4 animate-spin' />}
            Xóa
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}

export function CategoriesDialogs() {
  return (
    <>
      <CreateCategoryDialog />
      <EditCategoryDialog />
      <DeleteCategoryDialog />
    </>
  )
}
