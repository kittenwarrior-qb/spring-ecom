-- =============================================
-- V28: Product Cost Batches for FIFO/Weighted Average Cost Tracking
-- Tạo bảng product_cost_batches để theo dõi giá vốn theo lô nhập
-- Thêm cột cost_price vào order_items để lưu giá vốn tại thời điểm bán
-- =============================================

-- 1) Bảng theo dõi giá vốn theo lô nhập
CREATE TABLE IF NOT EXISTS product_cost_batches (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    purchase_order_item_id BIGINT REFERENCES purchase_order_items(id),
    quantity_remaining INT NOT NULL CHECK (quantity_remaining >= 0),
    cost_price DECIMAL(10,2) NOT NULL CHECK (cost_price >= 0),
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2) Thêm cột cost_price vào order_items (giá vốn lúc bán - COGS per item)
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS cost_price DECIMAL(10,2);

-- 3) Indexes
CREATE INDEX IF NOT EXISTS idx_cost_batches_product ON product_cost_batches(product_id);
CREATE INDEX IF NOT EXISTS idx_cost_batches_remaining ON product_cost_batches(product_id, quantity_remaining) WHERE quantity_remaining > 0;
CREATE INDEX IF NOT EXISTS idx_cost_batches_po_item ON product_cost_batches(purchase_order_item_id);


