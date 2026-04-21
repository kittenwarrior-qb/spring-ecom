import { createContext, useContext, useMemo } from 'react'

type Theme = 'light'

type ThemeProviderProps = {
  children: React.ReactNode
}

type ThemeProviderState = {
  theme: Theme
  resolvedTheme: Theme
}

const initialState: ThemeProviderState = {
  theme: 'light',
  resolvedTheme: 'light',
}

const ThemeContext = createContext<ThemeProviderState>(initialState)

export function ThemeProvider({ children, ...props }: ThemeProviderProps) {
  // Always use light theme
  const theme: Theme = 'light'
  const resolvedTheme: Theme = 'light'

  // Apply light theme on mount
  useMemo(() => {
    const root = window.document.documentElement
    root.classList.remove('dark')
    root.classList.add('light')
  }, [])

  const contextValue = {
    theme,
    resolvedTheme,
  }

  return (
    <ThemeContext value={contextValue} {...props}>
      {children}
    </ThemeContext>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export const useTheme = () => {
  const context = useContext(ThemeContext)
  if (!context) throw new Error('useTheme must be used within a ThemeProvider')
  return context
}
