-- Update payment_method constraint to only allow COD and BANK_TRANSFER
ALTER TABLE orders DROP CONSTRAINT IF EXISTS chk_payment_method;
ALTER TABLE orders ADD CONSTRAINT chk_payment_method 
    CHECK (payment_method IN ('COD', 'BANK_TRANSFER'));