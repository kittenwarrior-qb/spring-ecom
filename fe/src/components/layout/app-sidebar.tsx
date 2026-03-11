import { useLayout } from '@/context/layout-provider'
import { useUser, useAuthStore } from '@/stores/auth-store'
import { useUserProfile } from '@/hooks/use-user'
import { useEffect } from 'react'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from '@/components/ui/sidebar'
// import { AppTitle } from './app-title'
import { getStaticSidebarData } from './data/sidebar-data'
import { NavGroup } from './nav-group'
import { NavUser } from './nav-user'
import { TeamSwitcher } from './team-switcher'

export function AppSidebar() {
  const { collapsible, variant } = useLayout()
  const currentUser = useUser()
  const { data: userProfile, isLoading: isProfileLoading } = useUserProfile()
  const { auth } = useAuthStore()
  const sidebarData = getStaticSidebarData()

  // Update auth store user when profile is loaded
  useEffect(() => {
    if (userProfile && (!currentUser || currentUser.id !== userProfile.id)) {
      // Convert UserResponse to UserInfo format for auth store
      const userInfo = {
        id: userProfile.id,
        username: userProfile.username,
        email: userProfile.email,
        firstName: userProfile.firstName,
        lastName: userProfile.lastName,
        role: userProfile.role,
      }
      auth.setUser(userInfo)
    }
  }, [userProfile, currentUser, auth])

  // Use profile data if available, otherwise fall back to auth store user
  const user = userProfile || currentUser
  

  // Show loading state if profile is being fetched and no current user
  if (isProfileLoading && !currentUser) {
    return (
      <Sidebar collapsible={collapsible} variant={variant}>
        <SidebarHeader>
          <TeamSwitcher teams={sidebarData.teams} />
        </SidebarHeader>
        <SidebarContent>
          {sidebarData.navGroups.map((props) => (
            <NavGroup key={props.title} {...props} />
          ))}
        </SidebarContent>
        <SidebarFooter>
          <div className="flex items-center justify-center p-4">
            <div className="h-8 w-8 animate-pulse rounded-lg bg-muted" />
          </div>
        </SidebarFooter>
        <SidebarRail />
      </Sidebar>
    )
  }

  return (
    <Sidebar collapsible={collapsible} variant={variant}>
      <SidebarHeader>
        <TeamSwitcher teams={sidebarData.teams} />

        {/* Replace <TeamSwitch /> with the following <AppTitle />
         /* if you want to use the normal app title instead of TeamSwitch dropdown */}
        {/* <AppTitle /> */}
      </SidebarHeader>
      <SidebarContent>
        {sidebarData.navGroups.map((props) => (
          <NavGroup key={props.title} {...props} />
        ))}
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={user} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
}
