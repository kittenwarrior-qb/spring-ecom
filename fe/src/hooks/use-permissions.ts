import { useUserProfile } from './use-user'

/**
 * Hook to check user permissions
 */
export function usePermissions() {
  const { data: profile } = useUserProfile()
  const permissions = profile?.permissions ?? []
  
  return {
    permissions,
    hasPermission: (permission: string) => permissions.includes(permission),
    hasAnyPermission: (perms: string[]) => perms.some(p => permissions.includes(p)),
    hasAllPermissions: (perms: string[]) => perms.every(p => permissions.includes(p)),
  }
}
