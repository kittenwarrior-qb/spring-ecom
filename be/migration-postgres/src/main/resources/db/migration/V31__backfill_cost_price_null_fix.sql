-- V31: Backfill cost_price cho các sản phẩm và order_items cũ đang NULL/0
-- Nguyên nhân: sản phẩm/đơn hàng tạo trước khi có hệ thống nhập hàng (PO) sẽ không có giá vốn
-- Hậu quả: COALESCE(NULL, NULL) = NULL → toàn bộ phép tính lợi nhuận bị NULL → hiển thị 0
--
-- Giải pháp: ước lượng giá vốn = 70% giá bán (discount_price hoặc price)
-- Admin có thể chỉnh lại chính xác sau khi tạo PO nhập hàng cho từng sản phẩm

-- 1) Backfill products.cost_price cho sản phẩm chưa có giá vốn
-- Ước tính giá vốn = 70% giá bán (discount_price ưu tiên, fallback sang price)
UPDATE products
SET cost_price = ROUND(COALESCE(discount_price, price, 0) * 0.70, 2)
WHERE cost_price IS NULL OR cost_price = 0;

-- 2) Đảm bảo cột products.cost_price không bao giờ NULL nữa
ALTER TABLE products ALTER COLUMN cost_price SET DEFAULT 0;
ALTER TABLE products ALTER COLUMN cost_price SET NOT NULL;

-- 3) Backfill order_items.cost_price cho đơn hàng cũ đang NULL
-- Ước tính giá vốn = 70% giá bán tại thời điểm đặt hàng (order_items.price)
UPDATE order_items
SET cost_price = ROUND(price * 0.70, 2)
WHERE cost_price IS NULL OR cost_price = 0;


