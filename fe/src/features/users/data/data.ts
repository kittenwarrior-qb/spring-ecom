import { Shield, User } from 'lucide-react'
import { type UserRole } from './schema'

// Status badge colors based on isActive
export const getStatusColor = (isActive: boolean, isEmailVerified: boolean): string => {
  if (!isActive) return 'bg-neutral-300/40 border-neutral-300'
  if (!isEmailVerified) return 'bg-sky-200/40 text-sky-900 dark:text-sky-100 border-sky-300'
  return 'bg-teal-100/30 text-teal-900 dark:text-teal-200 border-teal-200'
}

export const roles = [
  {
    label: 'Admin',
    value: 'ADMIN' as UserRole,
    icon: Shield,
  },
  {
    label: 'User',
    value: 'USER' as UserRole,
    icon: User,
  },
] as const
