-- Add payment_status column to orders table
ALTER TABLE orders ADD COLUMN payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID';

-- Update existing orders based on payment method
-- COD orders that are delivered should be marked as PAID
UPDATE orders 
SET payment_status = 'PAID' 
WHERE payment_method = 'COD' AND status = 'DELIVERED';

-- Other COD orders remain UNPAID until delivery
UPDATE orders 
SET payment_status = 'UNPAID' 
WHERE payment_method = 'COD' AND status != 'DELIVERED';

-- Bank transfer orders should be checked manually, default to PENDING
UPDATE orders 
SET payment_status = 'PENDING' 
WHERE payment_method = 'BANK_TRANSFER';