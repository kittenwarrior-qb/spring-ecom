-- V17: Add new order statuses for stock reservation pattern

-- Drop old constraint
ALTER TABLE orders DROP CONSTRAINT IF EXISTS chk_status;

-- Add new constraint with all statuses including reservation pattern
ALTER TABLE orders ADD CONSTRAINT chk_status CHECK (status IN (
    'PENDING', 
    'PENDING_STOCK', 
    'STOCK_RESERVED', 
    'STOCK_FAILED', 
    'CONFIRMED', 
    'PROCESSING', 
    'SHIPPED', 
    'DELIVERED', 
    'CANCELLED', 
    'PARTIALLY_CANCELLED'
));

-- Add comment
COMMENT ON COLUMN orders.status IS 'PENDING_STOCK: waiting for stock reservation, STOCK_RESERVED: stock reserved, STOCK_FAILED: insufficient stock';
