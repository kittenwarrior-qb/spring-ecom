import { useUserProfile } from '@/hooks/use-user'
import { useAuth } from '@/stores/auth-store'
import { getCookie } from '@/lib/cookies'

export function DebugProfile() {
  const { data: profile, isLoading, error } = useUserProfile()
  const { user, accessToken, isAuthenticated } = useAuth()
  const accessTokenCookie = getCookie('accessToken')

  return (
    <div className="fixed bottom-4 hidden right-4 bg-black text-white p-4 rounded-lg text-xs max-w-sm z-50 max-h-96 overflow-y-auto">
      <h3 className="font-bold mb-2">Debug Profile</h3>
      <div className="space-y-1">
        <div>Cookie Token: {accessTokenCookie ? 'Yes' : 'No'}</div>
        <div>Store Token: {accessToken ? 'Yes' : 'No'}</div>
        <div>Is Authenticated: {isAuthenticated() ? 'Yes' : 'No'}</div>
        <div>Profile Loading: {isLoading ? 'Yes' : 'No'}</div>
        <div>Profile Error: {error ? String(error) : 'None'}</div>
        <div>Profile Data: {profile ? 'Loaded' : 'None'}</div>
        <div>Store User: {user ? 'Loaded' : 'None'}</div>
        {profile && (
          <div className="mt-2 text-xs border-t pt-2">
            <div>Profile Name: {profile.firstName} {profile.lastName}</div>
            <div>Profile Email: {profile.email}</div>
            <div>Profile Phone: {profile.phoneNumber}</div>
          </div>
        )}
        {user && (
          <div className="mt-2 text-xs border-t pt-2">
            <div>Store Name: {user.firstName} {user.lastName}</div>
            <div>Store Email: {user.email}</div>
            <div>Store Role: {user.role}</div>
          </div>
        )}
      </div>
    </div>
  )
}