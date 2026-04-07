import { useEffect } from 'react'
import { z } from 'zod'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Loader2 } from 'lucide-react'
import { toast } from 'sonner'
import { useCreateProduct, useUpdateProduct, useDeleteProduct } from '@/hooks/use-product'
import { useCategories } from '@/hooks/use-category'
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { useProductsContext } from './products-provider'
import { ImageUpload } from '@/components/image-upload'

const productFormSchema = z.object({
  title: z.string().min(1, 'Title is required').max(255, 'Title must be at most 255 characters'),
  slug: z.string().max(255, 'Slug must be at most 255 characters').optional(),
  author: z.string().max(100, 'Author must be at most 100 characters').optional(),
  publisher: z.string().max(100, 'Publisher must be at most 100 characters').optional(),
  publicationYear: z.number().int().min(1000).max(2100).optional(),
  language: z.string().max(50, 'Language must be at most 50 characters').optional(),
  pages: z.number().int().min(1).optional(),
  format: z.string().max(50).optional(),
  description: z.string().max(2000, 'Description must be at most 2000 characters').optional(),
  price: z.number().min(0, 'Price must be non-negative'),
  discountPrice: z.number().min(0, 'Discount price must be non-negative').optional(),
  stockQuantity: z.number().int().min(0, 'Stock quantity must be non-negative').optional(),
  coverImageUrl: z.string().max(500).optional().or(z.literal('')),
  isBestseller: z.boolean(),
  isActive: z.boolean(),
  categoryId: z.number().optional(),
})

type ProductFormValues = z.input<typeof productFormSchema>

const formatOptions = ['Paperback', 'Hardcover', 'Ebook', 'Audiobook']

function ProductFormDialog({
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
  onSubmit: (data: ProductFormValues) => Promise<void>
  defaultValues?: Partial<ProductFormValues>
  title: string
  description: string
  isLoading: boolean
}) {
  const { data: categories, isLoading: categoriesLoading } = useCategories()
  
  const form = useForm<ProductFormValues>({
    resolver: zodResolver(productFormSchema),
    defaultValues: {
      title: '',
      slug: '',
      author: '',
      publisher: '',
      publicationYear: undefined,
      language: '',
      pages: undefined,
      format: 'Paperback',
      description: '',
      price: 0,
      discountPrice: undefined,
      stockQuantity: 0,
      coverImageUrl: '',
      isBestseller: false,
      isActive: true,
      categoryId: undefined,
      ...defaultValues,
    },
  })

  // Reset form when dialog opens with new values
  useEffect(() => {
    if (open && defaultValues) {
      form.reset({
        title: '',
        slug: '',
        author: '',
        publisher: '',
        publicationYear: undefined,
        language: '',
        pages: undefined,
        format: 'Paperback',
        description: '',
        price: 0,
        discountPrice: undefined,
        stockQuantity: 0,
        coverImageUrl: '',
        isBestseller: false,
        isActive: true,
        categoryId: undefined,
        ...defaultValues,
      })
    }
  }, [open, defaultValues, form])

  // Auto-generate slug from title
  const titleValue = form.watch('title')
  useEffect(() => {
    if (titleValue && !form.getValues('slug')) {
      const slug = titleValue
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/(^-|-$)/g, '')
      form.setValue('slug', slug)
    }
  }, [titleValue, form])

  const handleSubmit = async (data: ProductFormValues) => {
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
      <DialogContent className='sm:max-w-[600px] max-h-[90vh] overflow-y-auto'>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          <DialogDescription>{description}</DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className='space-y-4'>
            <div className='grid grid-cols-2 gap-4'>
              <FormField
                control={form.control}
                name='title'
                render={({ field }) => (
                  <FormItem className='col-span-2'>
                    <FormLabel>Title *</FormLabel>
                    <FormControl>
                      <Input placeholder='Product title' {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='slug'
                render={({ field }) => (
                  <FormItem className='col-span-2'>
                    <FormLabel>Slug</FormLabel>
                    <FormControl>
                      <Input placeholder='product-slug' {...field} />
                    </FormControl>
                    <FormDescription>
                      URL-friendly identifier. Auto-generated from title.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='author'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Author</FormLabel>
                    <FormControl>
                      <Input placeholder='Author name' {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='publisher'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Publisher</FormLabel>
                    <FormControl>
                      <Input placeholder='Publisher name' {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='publicationYear'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Publication Year</FormLabel>
                    <FormControl>
                      <Input
                        type='number'
                        placeholder='2024'
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) => {
                          const value = e.target.value
                          field.onChange(value ? parseInt(value, 10) : undefined)
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='pages'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Pages</FormLabel>
                    <FormControl>
                      <Input
                        type='number'
                        placeholder='100'
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) => {
                          const value = e.target.value
                          field.onChange(value ? parseInt(value, 10) : undefined)
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='language'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Language</FormLabel>
                    <FormControl>
                      <Input placeholder='Vietnamese' {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='format'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Format</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder='Select format' />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {formatOptions.map((format) => (
                          <SelectItem key={format} value={format}>
                            {format}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='categoryId'
                render={({ field }) => (
                  <FormItem className='col-span-2'>
                    <FormLabel>Category</FormLabel>
                    <Select 
                      onValueChange={(value) => field.onChange(value ? parseInt(value, 10) : undefined)} 
                      value={field.value?.toString() || undefined}
                      disabled={categoriesLoading}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder={categoriesLoading ? 'Loading categories...' : 'Select category'} />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {categoriesLoading ? (
                          <SelectItem value="loading" disabled>Loading...</SelectItem>
                        ) : categories && categories.length > 0 ? (
                          categories.filter(cat => cat.isActive).map((category) => (
                            <SelectItem key={category.id} value={category.id.toString()}>
                              {category.name}
                            </SelectItem>
                          ))
                        ) : (
                          <SelectItem value="no-categories" disabled>No categories available</SelectItem>
                        )}
                      </SelectContent>
                    </Select>
                    <FormDescription>
                      Select a category for this product (optional)
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='price'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Price (VNĐ) *</FormLabel>
                    <FormControl>
                      <Input
                        type='number'
                        placeholder='100000'
                        {...field}
                        onChange={(e) => {
                          const value = e.target.value
                          field.onChange(value ? parseFloat(value) : 0)
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='discountPrice'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Discount Price (VNĐ)</FormLabel>
                    <FormControl>
                      <Input
                        type='number'
                        placeholder='80000'
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) => {
                          const value = e.target.value
                          field.onChange(value ? parseFloat(value) : undefined)
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='stockQuantity'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Stock Quantity</FormLabel>
                    <FormControl>
                      <Input
                        type='number'
                        placeholder='100'
                        {...field}
                        onChange={(e) => {
                          const value = e.target.value
                          field.onChange(value ? parseInt(value, 10) : 0)
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='coverImageUrl'
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Cover Image</FormLabel>
                    <FormControl>
                      <ImageUpload
                        value={field.value}
                        onChange={field.onChange}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='description'
                render={({ field }) => (
                  <FormItem className='col-span-2'>
                    <FormLabel>Description</FormLabel>
                    <FormControl>
                      <Textarea
                        placeholder='Product description (optional)'
                        className='resize-none min-h-[100px]'
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='isBestseller'
                render={({ field }) => (
                  <FormItem className='flex items-center justify-between rounded-lg border p-3'>
                    <div className='space-y-0.5'>
                      <FormLabel>Bestseller</FormLabel>
                      <FormDescription>
                        Mark as bestseller product
                      </FormDescription>
                    </div>
                    <FormControl>
                      <Switch
                        checked={field.value}
                        onCheckedChange={field.onChange}
                      />
                    </FormControl>
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name='isActive'
                render={({ field }) => (
                  <FormItem className='flex items-center justify-between rounded-lg border p-3'>
                    <div className='space-y-0.5'>
                      <FormLabel>Active</FormLabel>
                      <FormDescription>
                        Product is available for sale
                      </FormDescription>
                    </div>
                    <FormControl>
                      <Switch
                        checked={field.value}
                        onCheckedChange={field.onChange}
                      />
                    </FormControl>
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

function CreateProductDialog() {
  const { isCreateOpen, setIsCreateOpen } = useProductsContext()
  const createProduct = useCreateProduct()

  const handleSubmit = async (data: ProductFormValues) => {
    await createProduct.mutateAsync(data)
    toast.success('Product created successfully')
  }

  return (
    <ProductFormDialog
      open={isCreateOpen}
      onOpenChange={setIsCreateOpen}
      onSubmit={handleSubmit}
      title='Create Product'
      description='Add a new product to your store.'
      isLoading={createProduct.isPending}
    />
  )
}

function EditProductDialog() {
  const { isEditOpen, setIsEditOpen, selectedProduct } = useProductsContext()
  const updateProduct = useUpdateProduct()

  const handleSubmit = async (data: ProductFormValues) => {
    if (!selectedProduct) return
    await updateProduct.mutateAsync({ id: selectedProduct.id, data })
    toast.success('Product updated successfully')
  }

  return (
    <ProductFormDialog
      open={isEditOpen}
      onOpenChange={setIsEditOpen}
      onSubmit={handleSubmit}
      defaultValues={
        selectedProduct
          ? {
              title: selectedProduct.title,
              slug: selectedProduct.slug,
              author: selectedProduct.author ?? '',
              publisher: selectedProduct.publisher ?? '',
              publicationYear: selectedProduct.publicationYear ?? undefined,
              language: selectedProduct.language ?? '',
              pages: selectedProduct.pages ?? undefined,
              format: selectedProduct.format ?? 'Paperback',
              description: selectedProduct.description ?? '',
              price: selectedProduct.price,
              discountPrice: selectedProduct.discountPrice ?? undefined,
              stockQuantity: selectedProduct.stockQuantity,
              coverImageUrl: selectedProduct.coverImageUrl ?? '',
              isBestseller: selectedProduct.isBestseller,
              isActive: selectedProduct.isActive,
              categoryId: selectedProduct.categoryId ?? undefined,
            }
          : undefined
      }
      title='Edit Product'
      description='Update product information.'
      isLoading={updateProduct.isPending}
    />
  )
}

function DeleteProductDialog() {
  const { isDeleteOpen, setIsDeleteOpen, selectedProduct, setSelectedProduct } = useProductsContext()
  const deleteProduct = useDeleteProduct()

  const handleDelete = async () => {
    if (!selectedProduct) return
    try {
      await deleteProduct.mutateAsync(selectedProduct.id)
      toast.success('Product deleted successfully')
      setIsDeleteOpen(false)
      setSelectedProduct(null)
    } catch (error) {
      handleServerError(error)
    }
  }

  return (
    <AlertDialog open={isDeleteOpen} onOpenChange={setIsDeleteOpen}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Delete Product</AlertDialogTitle>
          <AlertDialogDescription>
            Are you sure you want to delete "{selectedProduct?.title}"? This action cannot be undone.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction
            onClick={handleDelete}
            className='bg-destructive text-destructive-foreground hover:bg-destructive/90'
            disabled={deleteProduct.isPending}
          >
            {deleteProduct.isPending && <Loader2 className='mr-2 h-4 w-4 animate-spin' />}
            Delete
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}

export function ProductsDialogs() {
  return (
    <>
      <CreateProductDialog />
      <EditProductDialog />
      <DeleteProductDialog />
    </>
  )
}
