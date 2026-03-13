-- Add cancelled_quantity column to order_items table
ALTER TABLE order_items 
ADD COLUMN cancelled_quantity INTEGER NOT NULL DEFAULT 0;

-- Add comment for documentation
COMMENT ON COLUMN order_items.cancelled_quantity IS 'Number of items cancelled from the original quantity';