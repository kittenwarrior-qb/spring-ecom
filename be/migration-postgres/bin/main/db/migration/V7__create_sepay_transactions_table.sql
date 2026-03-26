-- Create SePay transactions table with JSONB for webhook data storage

CREATE TABLE sepay_transactions (
    id BIGSERIAL PRIMARY KEY,
    sepay_id INTEGER NOT NULL UNIQUE,
    webhook_data JSONB NOT NULL,
    code VARCHAR(250),
    transfer_amount DECIMAL(20,2) NOT NULL,
    transfer_type VARCHAR(10) NOT NULL,
    processed BOOLEAN DEFAULT FALSE,
    order_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sepay_transactions_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    CONSTRAINT chk_transfer_type CHECK (transfer_type IN ('in', 'out')),
    CONSTRAINT chk_transfer_amount CHECK (transfer_amount >= 0)
);

-- Create indexes for better performance
CREATE INDEX idx_sepay_transactions_sepay_id ON sepay_transactions(sepay_id);
CREATE INDEX idx_sepay_transactions_code ON sepay_transactions(code);
CREATE INDEX idx_sepay_transactions_processed ON sepay_transactions(processed);
CREATE INDEX idx_sepay_transactions_created_at ON sepay_transactions(created_at);
CREATE INDEX idx_sepay_transactions_order_id ON sepay_transactions(order_id);
CREATE INDEX idx_sepay_transactions_transfer_type ON sepay_transactions(transfer_type);

-- JSONB indexes for common queries
CREATE INDEX idx_sepay_transactions_webhook_data_gin ON sepay_transactions USING GIN (webhook_data);

-- Add comments for documentation
COMMENT ON TABLE sepay_transactions IS 'Store SePay webhook transaction data with JSONB for flexibility';
COMMENT ON COLUMN sepay_transactions.sepay_id IS 'Unique transaction ID from SePay';
COMMENT ON COLUMN sepay_transactions.webhook_data IS 'Complete webhook data from SePay in JSONB format';
COMMENT ON COLUMN sepay_transactions.code IS 'Payment code to match with order number (extracted from webhook_data)';
COMMENT ON COLUMN sepay_transactions.processed IS 'Whether this transaction has been processed for order payment';
COMMENT ON COLUMN sepay_transactions.order_id IS 'Linked order ID if payment code matches';