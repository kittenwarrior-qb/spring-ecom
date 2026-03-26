-- Add refunded_amount column to orders table for tracking refunds when cancelling paid orders
ALTER TABLE orders ADD COLUMN refunded_amount DECIMAL(10,2) NOT NULL DEFAULT 0;

-- Add comment to describe the column
COMMENT ON COLUMN orders.refunded_amount IS 'Amount refunded when a paid order is cancelled';
