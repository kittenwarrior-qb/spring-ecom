import { useEffect } from 'react'
import { useNavigate } from '@tanstack/react-router'
import { useAuth } from '@/stores/auth-store'
import { AuthAnimation } from './auth-animation'

type AuthLayoutProps = {
  children: React.ReactNode
}

export function AuthLayout({ children }: AuthLayoutProps) {
  const navigate = useNavigate()
  const auth = useAuth()

  // Redirect if already authenticated
  useEffect(() => {
    if (auth.isAuthenticated()) {
      navigate({ to: '/', replace: true })
    }
  }, [auth, navigate])
  return (
    <div className='container grid !bg-[#f7f3ed] max-h-[1000px] max-w-none grid-cols-1 items-center justify-center lg:grid-cols-2 lg:px-0'>
      <div className='mx-auto flex w-full flex-col justify-center space-y-2 py-8 sm:w-[480px] lg:p-12'>
        {children}
      </div>

      <div className='hidden h-full !bg-[#f7f3ed] lg:block'>
        <AuthAnimation />
      </div>
    </div>
  )
}
