import { createFileRoute } from '@tanstack/react-router'
import { NotificationsPage } from '@/features/notifications/notifications-page'
import { z } from 'zod'

const searchSchema = z.object({
  id: z.number().optional(),
})

export const Route = createFileRoute('/notifications')({
  component: NotificationsPage,
  validateSearch: searchSchema,
})
