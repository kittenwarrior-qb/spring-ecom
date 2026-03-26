import { Link } from '@tanstack/react-router'
import { ArrowRight } from 'lucide-react'
import { Button } from '@/components/ui/button'

// Import banner images
import backgroundBanner from '@/assets/images/background-banner.webp'
import banner from '@/assets/images/banner.webp'
import noImage from '@/assets/images/no-image.jpg'

export function HeroBanner() {
  return (
    <div 
      className="relative bg-cover bg-center mb-8"
      style={{ backgroundImage: `url(${backgroundBanner})` }}
    >
      <div className="container mx-auto px-4 max-w-7xl">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-center py-16">
          {/* Left Content */}
          <div>
            <h1 className="text-4xl md:text-6xl lg:text-7xl font-bold mb-4 leading-tight">
              Thư Viện<br />
              <span className="text-orange-400">Tri thức</span>
            </h1>
            <p className="text-xl md:text-2xl mb-2">#1 Ứng dụng sách tại Việt Nam trên Kho</p>
            <p className="text-xl md:text-2xl mb-4">ứng dụng Apple & Google store.</p>
            <p className="text-lg md:text-xl mb-6">
              Với 13,000+ nội dung Sách nói có bản<br />
              quyền, Podcourse, Ebook, Podcast cùng<br />
              nhiều nội dung phong phú khác.
            </p>
            <Link to="/settings">
              <Button 
                className="px-16 py-2 bg-primary text-white rounded-[40px] text-xl font-semibold hover:opacity-90 transition-opacity"
              >
                Trở thành hội viên
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
            </Link>
            <div className="mt-6 flex items-center gap-3">
              <span className="text-sm">với hơn 400K Hội viên khác</span>
            </div>
          </div>
          
          {/* Right Image */}
          <div className="flex justify-center items-center">
            <img 
              src={banner} 
              alt="Mobile App Preview" 
              className="max-w-full h-auto"
              onError={(e) => {
                const target = e.target as HTMLImageElement
                target.src = noImage
              }}
            />
          </div>
        </div>
      </div>
    </div>
  )
}
