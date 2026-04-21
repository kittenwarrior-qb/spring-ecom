import { useLayout } from '@/context/layout-provider'
import { useUser, useAuthStore } from '@/stores/auth-store'
import { useUserProfile } from '@/hooks/use-user'
import { useEffect } from 'react'
import { Command } from 'lucide-react'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'
import { getStaticSidebarData } from './data/sidebar-data'
import { NavGroup } from './nav-group'
import { NavUser } from './nav-user'

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
        roles: userProfile.roles,
        permissions: userProfile.permissions,
      }
      auth.setUser(userInfo)
    }
  }, [userProfile, currentUser, auth])

  // Use profile data if available, otherwise fall back to auth store user
  const user = userProfile || currentUser

  // Show all nav groups without filtering - permission check is done in each page
  const navGroups = sidebarData.navGroups
  

  // Show loading state if profile is being fetched and no current user
  if (isProfileLoading && !currentUser) {
    return (
      <Sidebar collapsible={collapsible} variant={variant}>
        <SidebarHeader>
          <SidebarMenu>
            <SidebarMenuItem>
              <SidebarMenuButton size="lg">
                <div className="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
                  <Command className="size-4" />
                </div>
                <div className="grid flex-1 text-start text-sm leading-tight">
                  <span className="truncate font-semibold">Spring Ecom</span>
                  <span className="truncate text-xs">Bảng Quản Trị</span>
                </div>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarHeader>
        <SidebarContent>
          {navGroups.map((props) => (
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
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton size="lg" className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground">
              <div className="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
                <Command className="size-4" />
              </div>
              <div className="grid flex-1 text-start text-sm leading-tight">
                <span className="truncate font-semibold">Spring Ecom</span>
                <span className="truncate text-xs">Bảng Quản Trị</span>
              </div>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
      <SidebarContent>
        {navGroups.map((props) => (
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
