import { createFileRoute } from '@tanstack/react-router'
import { VerifyEmail } from '@/features/auth/verify-email'
import { z } from 'zod'

const verifyEmailSearchSchema = z.object({
    token: z.string().optional(),
    email: z.string().optional(),
})

export const Route = createFileRoute('/verify-email')({
    component: VerifyEmail,
    validateSearch: (search) => verifyEmailSearchSchema.parse(search),
})
