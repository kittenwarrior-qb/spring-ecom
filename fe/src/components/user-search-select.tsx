import { useState, useCallback, useRef, useEffect } from 'react'
import { Badge } from '@/components/ui/badge'
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from '@/components/ui/command'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { X, Search, User, Check } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface User {
  id: number
  email: string
  name?: string
  avatar?: string
}

interface UserSearchSelectProps {
  selectedUsers: User[]
  onChange: (users: User[]) => void
  searchUsers: (query: string) => Promise<User[]> | User[]
  placeholder?: string
  maxSelections?: number
  disabled?: boolean
}

export function UserSearchSelect({
  selectedUsers,
  onChange,
  searchUsers,
  placeholder = 'Tìm kiếm user...',
  maxSelections,
  disabled = false,
}: UserSearchSelectProps) {
  const [open, setOpen] = useState(false)
  const [inputValue, setInputValue] = useState('')
  const [searchResults, setSearchResults] = useState<User[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const inputRef = useRef<HTMLInputElement>(null)
  const debounceRef = useRef<NodeJS.Timeout | null>(null)

  // Search with debounce
  const handleSearch = useCallback(
    async (query: string) => {
      if (!query.trim()) {
        setSearchResults([])
        return
      }

      setIsLoading(true)
      try {
        const results = await searchUsers(query)
        // Filter out already selected users
        const filtered = results.filter(
          (user) => !selectedUsers.find((selected) => selected.id === user.id)
        )
        setSearchResults(filtered)
      } catch (error) {
        console.error('Search failed:', error)
        setSearchResults([])
      } finally {
        setIsLoading(false)
      }
    },
    [searchUsers, selectedUsers]
  )

  // Debounced search
  useEffect(() => {
    if (debounceRef.current) {
      clearTimeout(debounceRef.current)
    }

    debounceRef.current = setTimeout(() => {
      handleSearch(inputValue)
    }, 300)

    return () => {
      if (debounceRef.current) {
        clearTimeout(debounceRef.current)
      }
    }
  }, [inputValue, handleSearch])

  // Open popover when search results are available and input is focused
  useEffect(() => {
    if (searchResults.length > 0 && inputValue.trim() !== '') {
      setOpen(true)
    }
  }, [searchResults, inputValue])

  // Add user to selection
  const handleSelect = (user: User) => {
    if (maxSelections && selectedUsers.length >= maxSelections) {
      return
    }
    onChange([...selectedUsers, user])
    setInputValue('')
    setSearchResults([])
    inputRef.current?.focus()
  }

  // Remove user from selection
  const handleRemove = (userId: number) => {
    onChange(selectedUsers.filter((u) => u.id !== userId))
  }

  // Get initials from name or email
  const getInitials = (user: User) => {
    if (user.name) {
      return user.name
        .split(' ')
        .map((n) => n[0])
        .join('')
        .toUpperCase()
        .slice(0, 2)
    }
    return user.email.slice(0, 2).toUpperCase()
  }

  return (
    <div className="w-full space-y-2">
      {/* Selected Users Badges */}
      {selectedUsers.length > 0 && (
        <div className="flex flex-wrap gap-2">
          {selectedUsers.map((user) => (
            <Badge
              key={user.id}
              variant="secondary"
              className="flex items-center gap-2 px-3 py-1.5 text-sm font-medium transition-all hover:bg-secondary/80"
            >
              <Avatar className="h-5 w-5">
                <AvatarFallback className="text-xs bg-primary text-primary-foreground">
                  {getInitials(user)}
                </AvatarFallback>
              </Avatar>
              <div className="flex flex-col items-start">
                <span className="leading-tight">{user.name || user.email}</span>
                {user.name && <span className="text-xs text-muted-foreground leading-tight">{user.email}</span>}
              </div>
              <button
                type="button"
                onClick={() => handleRemove(user.id)}
                className="ml-1 rounded-full p-0.5 hover:bg-destructive/20 hover:text-destructive transition-colors"
                disabled={disabled}
              >
                <X className="h-3.5 w-3.5" />
              </button>
            </Badge>
          ))}
        </div>
      )}

      {/* Search Input */}
      <Popover open={open && !disabled} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <div
            className={cn(
              'flex items-center gap-2 rounded-md border bg-background px-3 py-2 text-sm',
              'focus-within:ring-2 focus-within:ring-ring focus-within:ring-offset-2',
              'cursor-text transition-all',
              disabled && 'opacity-50 cursor-not-allowed'
            )}
            onClick={() => !disabled && inputRef.current?.focus()}
          >
            <Search className="h-4 w-4 text-muted-foreground shrink-0" />
            <input
              ref={inputRef}
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              placeholder={
                selectedUsers.length === 0
                  ? placeholder
                  : maxSelections && selectedUsers.length >= maxSelections
                    ? `Đã chọn tối đa ${maxSelections} user`
                    : 'Thêm user...'
              }
              className="flex-1 bg-transparent outline-none placeholder:text-muted-foreground disabled:cursor-not-allowed"
              disabled={disabled || (maxSelections ? selectedUsers.length >= maxSelections : false)}
              onFocus={() => setOpen(true)}
            />
            {isLoading && (
              <div className="h-4 w-4 animate-spin rounded-full border-2 border-primary border-t-transparent" />
            )}
          </div>
        </PopoverTrigger>
        <PopoverContent className="w-[300px] p-0" align="start">
          <Command className="w-full">
            <CommandList className="max-h-[300px]">
              {inputValue.trim() === '' ? (
                <CommandEmpty className="py-6 text-center text-sm">
                  <User className="mx-auto h-8 w-8 text-muted-foreground/50 mb-2" />
                  <p className="text-muted-foreground">Nhập email để tìm kiếm</p>
                </CommandEmpty>
              ) : isLoading ? (
                <div className="py-6 text-center text-sm">
                  <div className="mx-auto h-6 w-6 animate-spin rounded-full border-2 border-primary border-t-transparent mb-2" />
                  <p className="text-muted-foreground">Đang tìm kiếm...</p>
                </div>
              ) : searchResults.length === 0 ? (
                <CommandEmpty className="py-6 text-center text-sm">
                  <Search className="mx-auto h-8 w-8 text-muted-foreground/50 mb-2" />
                  <p className="text-muted-foreground">Không tìm thấy user</p>
                </CommandEmpty>
              ) : (
                <CommandGroup heading={`Kết quả tìm kiếm (${searchResults.length})`}>
                  {searchResults.map((user) => (
                    <CommandItem
                      key={user.id}
                      value={user.email}
                      onSelect={() => handleSelect(user)}
                      className="flex items-center gap-3 px-3 py-2 cursor-pointer"
                    >
                      <Avatar className="h-8 w-8">
                        <AvatarFallback className="text-sm bg-primary/10 text-primary">
                          {getInitials(user)}
                        </AvatarFallback>
                      </Avatar>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium truncate">
                          {user.name || user.email}
                        </p>
                        <p className="text-xs text-muted-foreground truncate">
                          {user.email}
                        </p>
                      </div>
                      <Check className="h-4 w-4 text-primary opacity-0 group-hover:opacity-100" />
                    </CommandItem>
                  ))}
                </CommandGroup>
              )}
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>

      {/* Helper Text */}
      {maxSelections && selectedUsers.length > 0 && (
        <p className="text-xs text-muted-foreground">
          Đã chọn {selectedUsers.length}/{maxSelections} user
        </p>
      )}
    </div>
  )
}

// Example usage with API integration
export function useUserSearch() {
  const searchUsers = useCallback(async (query: string): Promise<User[]> => {
    // Replace with your actual API call
    // Example:
    // const response = await fetch(`/api/users/search?email=${encodeURIComponent(query)}`)
    // const data = await response.json()
    // return data

    // Mock data for demonstration
    await new Promise((resolve) => setTimeout(resolve, 300))
    
    const mockUsers: User[] = [
      { id: 1, email: 'user1@example.com', name: 'Nguyen Van A' },
      { id: 2, email: 'user2@example.com', name: 'Tran Thi B' },
      { id: 3, email: 'admin@example.com', name: 'Admin User' },
    ].filter(
      (u) =>
        u.email.toLowerCase().includes(query.toLowerCase()) ||
        u.name?.toLowerCase().includes(query.toLowerCase())
    )

    return mockUsers
  }, [])

  return { searchUsers }
}
