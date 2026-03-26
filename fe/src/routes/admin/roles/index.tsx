import { createFileRoute } from '@tanstack/react-router'
import { RolesFeature } from '@/features/roles'

export const Route = createFileRoute('/admin/roles/')({
  component: RolesFeature,
})
