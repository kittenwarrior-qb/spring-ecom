-- Add deleted_at column to tables that extend BaseAuditEntity but missing this column

-- Add deleted_at to orders table
ALTER TABLE orders ADD COLUMN deleted_at TIMESTAMP;

-- Add deleted_at to carts table
ALTER TABLE carts ADD COLUMN deleted_at TIMESTAMP;

-- Add deleted_at to cart_items table  
ALTER TABLE cart_items ADD COLUMN deleted_at TIMESTAMP;

-- Add indexes for better query performance
CREATE INDEX idx_orders_deleted_at ON orders(deleted_at);
CREATE INDEX idx_carts_deleted_at ON carts(deleted_at);
CREATE INDEX idx_cart_items_deleted_at ON cart_items(deleted_at);