-- Add missing audit columns, status to order_items and update order status constraint

-- Update order_items table
ALTER TABLE order_items 
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN deleted_at TIMESTAMP,
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
ADD COLUMN cancelled_at TIMESTAMP;

-- Add constraint for order item status
ALTER TABLE order_items 
ADD CONSTRAINT chk_order_item_status CHECK (status IN ('ACTIVE', 'CANCELLED', 'REFUNDED'));

-- Create indexes for order_items
CREATE INDEX idx_order_items_deleted_at ON order_items(deleted_at);
CREATE INDEX idx_order_items_status ON order_items(status);

-- Update order status constraint - simplified statuses
ALTER TABLE orders 
DROP CONSTRAINT IF EXISTS chk_status;

ALTER TABLE orders 
ADD CONSTRAINT chk_status CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'PARTIALLY_CANCELLED'));