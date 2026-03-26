-- Add reserved_quantity column to products table for reservation pattern
ALTER TABLE products ADD COLUMN IF NOT EXISTS reserved_quantity INTEGER NOT NULL DEFAULT 0;

-- Create stock_reservations table
CREATE TABLE IF NOT EXISTS stock_reservations (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expire_at TIMESTAMP WITH TIME ZONE NOT NULL,
    released_at TIMESTAMP WITH TIME ZONE
);

-- Create indexes for efficient queries
CREATE INDEX IF NOT EXISTS idx_stock_reservations_order_id ON stock_reservations(order_id);
CREATE INDEX IF NOT EXISTS idx_stock_reservations_product_id ON stock_reservations(product_id);
CREATE INDEX IF NOT EXISTS idx_stock_reservations_expire_at ON stock_reservations(expire_at);
CREATE INDEX IF NOT EXISTS idx_stock_reservations_status ON stock_reservations(status);

-- Add check constraint for status
ALTER TABLE stock_reservations ADD CONSTRAINT chk_reservation_status 
    CHECK (status IN ('ACTIVE', 'CONFIRMED', 'RELEASED', 'CANCELLED'));

-- Add comment
COMMENT ON TABLE stock_reservations IS 'Tracks stock reservations per order for TTL-based stock release';
COMMENT ON COLUMN stock_reservations.status IS 'ACTIVE: holding stock, CONFIRMED: payment done, RELEASED: timeout/cancel, CANCELLED: user cancelled';
