import { useEffect, useState } from 'react'
import { useNavigate, useSearch } from '@tanstack/react-router'
import { CheckCircle2, XCircle, Loader2, ArrowRight, Mail, RefreshCw } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from '@/components/ui/card'
import { emailApi } from '@/api/email.api'
import { AuthLayout } from '../auth-layout'
import { toast } from 'sonner'

export function VerifyEmail() {
    const { token, email } = useSearch({ from: '/verify-email' })
    const navigate = useNavigate()
    const [status, setStatus] = useState<'loading' | 'success' | 'error' | 'waiting'>('loading')
    const [message, setMessage] = useState('Verifying your email address...')
    const [isResending, setIsResending] = useState(false)

    useEffect(() => {
        // If no token provided, show waiting screen
        if (!token) {
            setStatus('waiting')
            setMessage(email 
                ? `Chúng tôi đã gửi email xác thực đến ${email}. Vui lòng kiểm tra hộp thư và nhấp vào liên kết xác thực.`
                : 'Vui lòng kiểm tra email của bạn và nhấp vào liên kết xác thực để hoàn tất đăng ký.'
            )
            return
        }

        const performVerification = async () => {
            try {
                await emailApi.verifyEmail(token)
                setStatus('success')
                setMessage('Email của bạn đã được xác thực thành công! Bây giờ bạn có thể đăng nhập vào tài khoản.')
            } catch (error: any) {
                setStatus('error')
                const errorMsg = error.response?.data?.message || 'Xác thực thất bại. Token có thể đã hết hạn hoặc không hợp lệ.'
                setMessage(errorMsg)
            }
        }

        performVerification()
    }, [token, email])

    const handleResendEmail = async () => {
        if (!email) {
            toast.error('Không tìm thấy địa chỉ email')
            return
        }

        setIsResending(true)
        try {
            // Call resend verification email API
            await emailApi.resendVerification(email)
            toast.success('Email xác thực đã được gửi lại!')
        } catch (error: unknown) {
            const errorMsg = error.response?.data?.message || 'Không thể gửi lại email xác thực'
            toast.error(errorMsg)
        } finally {
            setIsResending(false)
        }
    }

    return (
        <AuthLayout>
            <Card className='w-full max-w-md border-none bg-background/60 p-2 backdrop-blur-xl shadow-2xl'>
                <CardHeader className='text-center'>
                    <div className='flex justify-center mb-4'>
                        {status === 'loading' && (
                            <div className='rounded-full bg-primary/10 p-3 ring-8 ring-primary/5'>
                                <Loader2 className='h-10 w-10 animate-spin text-primary' />
                            </div>
                        )}
                        {status === 'waiting' && (
                            <div className='rounded-full bg-blue-500/10 p-3 ring-8 ring-blue-500/5 animate-in zoom-in duration-500'>
                                <Mail className='h-10 w-10 text-blue-500' />
                            </div>
                        )}
                        {status === 'success' && (
                            <div className='rounded-full bg-green-500/10 p-3 ring-8 ring-green-500/5 animate-in zoom-in duration-500'>
                                <CheckCircle2 className='h-10 w-10 text-green-500' />
                            </div>
                        )}
                        {status === 'error' && (
                            <div className='rounded-full bg-destructive/10 p-3 ring-8 ring-destructive/5 animate-in zoom-in duration-500'>
                                <XCircle className='h-10 w-10 text-destructive' />
                            </div>
                        )}
                    </div>
                    <CardTitle className='text-2xl font-bold tracking-tight'>
                        {status === 'loading' && 'Đang xác thực email'}
                        {status === 'waiting' && 'Kiểm tra email của bạn'}
                        {status === 'success' && 'Xác thực thành công'}
                        {status === 'error' && 'Xác thực thất bại'}
                    </CardTitle>
                    <CardDescription className='text-balance text-muted-foreground mt-2'>
                        {message}
                    </CardDescription>
                </CardHeader>
                <CardContent className='pt-4'>
                    {status === 'loading' && (
                        <div className='space-y-2'>
                            <div className='h-2 w-full animate-pulse rounded-full bg-muted' />
                            <div className='h-2 w-3/4 animate-pulse rounded-full bg-muted' />
                        </div>
                    )}
                    {status === 'waiting' && (
                        <div className='text-center space-y-4'>
                            <div className='text-sm text-muted-foreground'>
                                Không nhận được email? Kiểm tra thư mục spam hoặc
                            </div>
                        </div>
                    )}
                </CardContent>
                <CardFooter className='flex flex-col gap-3'>
                    {status === 'waiting' && (
                        <>
                            <Button
                                variant='outline'
                                className='w-full'
                                onClick={handleResendEmail}
                                disabled={isResending}
                            >
                                {isResending ? (
                                    <>
                                        <Loader2 className='mr-2 h-4 w-4 animate-spin' />
                                        Đang gửi...
                                    </>
                                ) : (
                                    <>
                                        <RefreshCw className='mr-2 h-4 w-4' />
                                        Gửi lại email xác thực
                                    </>
                                )}
                            </Button>
                            <Button
                                variant='ghost'
                                className='w-full'
                                onClick={() => navigate({ to: '/sign-in', search: { redirect: undefined } })}
                            >
                                Quay lại đăng nhập
                            </Button>
                        </>
                    )}
                    {status === 'success' && (
                        <Button
                            className='w-full group'
                            onClick={() => navigate({ to: '/sign-in', search: { redirect: undefined } })}
                        >
                            Đăng nhập ngay
                            <ArrowRight className='ml-2 h-4 w-4 transition-transform group-hover:translate-x-1' />
                        </Button>
                    )}
                    {status === 'error' && (
                        <>
                            <Button
                                variant='outline'
                                className='w-full'
                                onClick={() => navigate({ to: '/sign-up' })}
                            >
                                Thử đăng ký lại
                            </Button>
                            <Button
                                variant='ghost'
                                className='w-full'
                                onClick={() => navigate({ to: '/sign-in', search: { redirect: undefined } })}
                            >
                                Quay lại đăng nhập
                            </Button>
                        </>
                    )}
                </CardFooter>
            </Card>
        </AuthLayout>
    )
}
