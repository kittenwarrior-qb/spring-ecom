-- =============================================
-- V27: Enhance Inventory Movements for Audit Trail
-- Thêm stock_before, stock_after, cost_price để theo dõi lịch sử kho chính xác
-- Mở rộng movement_type hỗ trợ SALE_OUT, RESERVATION, RESERVATION_RELEASE
-- =============================================

-- 1) Thêm cột audit vào inventory_movements
ALTER TABLE inventory_movements ADD COLUMN IF NOT EXISTS stock_before INT;
ALTER TABLE inventory_movements ADD COLUMN IF NOT EXISTS stock_after INT;
ALTER TABLE inventory_movements ADD COLUMN IF NOT EXISTS cost_price DECIMAL(10,2);

-- 2) Mở rộng constraint movement_type
ALTER TABLE inventory_movements DROP CONSTRAINT IF EXISTS chk_movement_type;
ALTER TABLE inventory_movements ADD CONSTRAINT chk_movement_type
    CHECK (movement_type IN ('IMPORT', 'EXPORT', 'ADJUSTMENT', 'RETURN', 'SALE_OUT', 'RESERVATION', 'RESERVATION_RELEASE'));

