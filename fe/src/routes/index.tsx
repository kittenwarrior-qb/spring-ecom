import { createFileRoute } from '@tanstack/react-router'
import {
  HeroSection,
  FeaturesSection,
  BestsellersSection,
  CategoriesSection,
} from '@/features/home/components'

export const Route = createFileRoute('/')({
  component: HomePage,
})

function HomePage() {
  return (
    <div className="space-y-12">
      <HeroSection />
      <FeaturesSection />
      <BestsellersSection />
      <CategoriesSection />
    </div>
  )
}
