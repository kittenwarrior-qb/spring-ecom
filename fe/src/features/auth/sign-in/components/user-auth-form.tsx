import { useState } from 'react'
import { z } from 'zod'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useNavigate } from '@tanstack/react-router'
import { Loader2, LogIn } from 'lucide-react'
import { toast } from 'sonner'
import { useAuth } from '@/stores/auth-store'
import { authApi } from '@/api/auth.api'
import { userApi } from '@/api/user.api'
import { handleServerError } from '@/lib/handle-server-error'
import { useSyncCart } from '@/hooks/use-cart'
import { useCartStore } from '@/stores/cart-store'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { PasswordInput } from '@/components/password-input'

const formSchema = z.object({
  email: z.email({
    error: (iss) => (iss.input === '' ? 'Please enter your email' : undefined),
  }),
  password: z
    .string()
    .min(1, 'Please enter your password')
    .min(1, 'Password is required'),
})

interface UserAuthFormProps extends React.HTMLAttributes<HTMLFormElement> {
  redirectTo?: string
}

export function UserAuthForm({
  className,
  redirectTo,
  ...props
}: UserAuthFormProps) {
  const [isLoading, setIsLoading] = useState(false)
  const navigate = useNavigate()
  const auth = useAuth()
  const syncCart = useSyncCart()
  const localCartItems = useCartStore((state) => state.items)

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  })

  async function onSubmit(data: z.infer<typeof formSchema>) {
    setIsLoading(true)

    try {
      const response = await authApi.login({
        email: data.email,
        password: data.password,
      })

      // Set access token first
      if (response.accessToken) {
        auth.setAccessToken(response.accessToken)
        
        // Now fetch user profile to get user info
        try {
          const userProfile = await userApi.getProfile()
          auth.setUser({
            id: userProfile.id,
            username: userProfile.username,
            email: userProfile.email,
            firstName: userProfile.firstName,
            lastName: userProfile.lastName,
            role: userProfile.role
          })
        } catch (profileError) {
          console.error('Failed to fetch user profile:', profileError)
          // Continue with login even if profile fetch fails
        }
      }

      // Sync cart if there are items
      if (localCartItems.length > 0) {
        try {
          await syncCart.mutateAsync(
            localCartItems.map((item) => ({
              productId: item.productId,
              quantity: item.quantity,
            }))
          )
        } catch (syncError) {
          console.error('Failed to sync cart:', syncError)
          // Don't block login if sync fails, but maybe log it
        }
      }

      toast.success(`Welcome back!`)

      // Redirect to the stored location or default to dashboard
      const targetPath = (redirectTo as any) || '/'
      navigate({ to: targetPath, replace: true })
    } catch (error) {
      handleServerError(error)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className={cn('grid gap-3', className)}
        {...props}
      >
        <FormField
          control={form.control}
          name='email'
          render={({ field }) => (
            <FormItem>
              <FormLabel>Email</FormLabel>
              <FormControl>
                <Input placeholder='name@example.com' {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name='password'
          render={({ field }) => (
            <FormItem className='relative'>
              <FormLabel>Password</FormLabel>
              <FormControl>
                <PasswordInput placeholder='********' {...field} />
              </FormControl>
              <FormMessage />
              <Link
                to='/forgot-password'
                className='absolute end-0 -top-0.5 text-sm font-medium text-muted-foreground hover:opacity-75'
              >
                Forgot password?
              </Link>
            </FormItem>
          )}
        />
        <Button className='mt-2' disabled={isLoading}>
          {isLoading ? <Loader2 className='animate-spin' /> : <LogIn />}
          Sign in
        </Button>

        {/* Raw fetch test button */}
        <div className='relative my-4'>
          <div className='absolute inset-0 flex items-center'>
            <span className='w-full border-t' />
          </div>
          <div className='relative flex justify-center text-xs uppercase'>
            <span className=' px-2 text-muted-foreground'>
              Bạn mới đến Spring Ecom?
            </span>
          </div>
        </div>

        <Link to='/sign-up'>
          <Button variant='outline' className='w-full' type='button'>
            Tạo tài khoản mới
          </Button>
        </Link>
      </form>
    </Form>
  )
}
