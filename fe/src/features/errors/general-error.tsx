import { Link } from '@tanstack/react-router'
import { AlertTriangle, RefreshCw, Home } from 'lucide-react'
import { Button } from '@/components/ui/button'

export function GeneralError() {
  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-6 p-4">
      <div className="flex flex-col items-center gap-4">
        <div className="rounded-full bg-destructive/10 p-6">
          <AlertTriangle className="h-12 w-12 text-destructive" />
        </div>
        <h1 className="text-3xl font-bold">Something went wrong</h1>
        <p className="text-center text-muted-foreground max-w-md">
          An unexpected error occurred. Please try again or contact support if the problem persists.
        </p>
      </div>
      <div className="flex gap-4">
        <Button variant="outline" onClick={() => window.location.reload()}>
          <RefreshCw className="mr-2 h-4 w-4" />
          Try Again
        </Button>
        <Link to="/">
          <Button>
            <Home className="mr-2 h-4 w-4" />
            Back to Home
          </Button>
        </Link>
      </div>
    </div>
  )
}
