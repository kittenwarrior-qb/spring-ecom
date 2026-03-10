import { useEffect, useState } from 'react'
import { useNavigate, useSearch } from '@tanstack/react-router'
import { CheckCircle2, XCircle, Loader2, ArrowRight } from 'lucide-react'
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

export function VerifyEmail() {
    const { token } = useSearch({ from: '/verify-email' })
    const navigate = useNavigate()
    const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading')
    const [message, setMessage] = useState('Verifying your email address...')

    useEffect(() => {
        const performVerification = async () => {
            if (!token) {
                setStatus('error')
                setMessage('Invalid or missing verification token.')
                return
            }

            try {
                await emailApi.verifyEmail(token)
                setStatus('success')
                setMessage('Your email has been successfully verified! You can now log into your account.')
            } catch (error: any) {
                setStatus('error')
                const errorMsg = error.response?.data?.message || 'Verification failed. The token might be expired or invalid.'
                setMessage(errorMsg)
            }
        }

        performVerification()
    }, [token])

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
                        {status === 'loading' && 'Verifying Email'}
                        {status === 'success' && 'Verification Successful'}
                        {status === 'error' && 'Verification Failed'}
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
                </CardContent>
                <CardFooter className='flex flex-col gap-3'>
                    {status === 'success' && (
                        <Button
                            className='w-full group'
                            onClick={() => navigate({ to: '/sign-in', search: { redirect: undefined } })}
                        >
                            Back to Sign In
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
                                Try Registering Again
                            </Button>
                            <Button
                                variant='ghost'
                                className='w-full'
                                onClick={() => navigate({ to: '/sign-in', search: { redirect: undefined } })}
                            >
                                Back to Sign In
                            </Button>
                        </>
                    )}
                </CardFooter>
            </Card>
        </AuthLayout>
    )
}
