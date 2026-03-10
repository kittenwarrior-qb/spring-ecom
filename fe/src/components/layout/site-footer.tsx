import { Link } from '@tanstack/react-router'
import { MapPin, Phone, Mail, Facebook, Youtube, Instagram } from 'lucide-react'

// Import logo
import logoImg from '@/assets/images/logo.png'

export function SiteFooter() {
  return (
    <footer className="bg-gray-900 text-gray-300">
      <div className="container mx-auto px-4 py-12 max-w-7xl">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Company Info */}
          <div>
            <img src={logoImg} alt="Logo" className="h-10 mb-4 brightness-0 invert" />
            <p className="text-sm mb-4">
              Ứng dụng sách số 1 Việt Nam với hơn 13,000+ nội dung có bản quyền.
            </p>
            <div className="flex space-x-4">
              <a href="#" className="hover:text-primary transition-colors">
                <Facebook className="h-5 w-5" />
              </a>
              <a href="#" className="hover:text-primary transition-colors">
                <Youtube className="h-5 w-5" />
              </a>
              <a href="#" className="hover:text-primary transition-colors">
                <Instagram className="h-5 w-5" />
              </a>
              <a href="#" className="hover:text-primary transition-colors text-sm font-bold">
                TikTok
              </a>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h3 className="text-white font-semibold text-lg mb-4">Liên kết nhanh</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/" className="hover:text-primary transition-colors">
                  Trang chủ
                </Link>
              </li>
              <li>
                <Link 
                  to="/products" 
                  search={{ category: undefined, keyword: undefined }}
                  className="hover:text-primary transition-colors"
                >
                  Sách
                </Link>
              </li>
              <li>
                <a href="#" className="hover:text-primary transition-colors">
                  Giới thiệu
                </a>
              </li>
              <li>
                <Link to="/cart" className="hover:text-primary transition-colors">
                  Giỏ hàng
                </Link>
              </li>
            </ul>
          </div>

          {/* Support */}
          <div>
            <h3 className="text-white font-semibold text-lg mb-4">Hỗ trợ</h3>
            <ul className="space-y-2">
              <li>
                <a href="#" className="hover:text-primary transition-colors">
                  Câu hỏi thường gặp
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-primary transition-colors">
                  Chính sách bảo mật
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-primary transition-colors">
                  Điều khoản sử dụng
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-primary transition-colors">
                  Liên hệ
                </a>
              </li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h3 className="text-white font-semibold text-lg mb-4">Liên hệ</h3>
            <ul className="space-y-3">
              <li className="flex items-start">
                <MapPin className="mt-1 mr-3 h-4 w-4" />
                <span className="text-sm">Tòa nhà 3/6, Đường 36, Hồ Chí Minh</span>
              </li>
              <li className="flex items-center">
                <Phone className="mr-3 h-4 w-4" />
                <span className="text-sm">1900 xxxx</span>
              </li>
              <li className="flex items-center">
                <Mail className="mr-3 h-4 w-4" />
                <span className="text-sm">support@thuvientritthuc.vn</span>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="border-t border-gray-800 mt-8 pt-8 text-center text-sm">
          <p>&copy; 2026 Thư Viện Tri Thức. All rights reserved.</p>
        </div>
      </div>
    </footer>
  )
}
