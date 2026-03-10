-- Create SePay transactions table for webhook data storage and payment tracking

CREATE TABLE sepay_transactions (
    id BIGSERIAL PRIMARY KEY,
    sepay_id INTEGER NOT NULL UNIQUE,
    gateway VARCHAR(100) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    account_number VARCHAR(100),
    sub_account VARCHAR(250),
    amount_in DECIMAL(20,2) NOT NULL DEFAULT 0.00,
    amount_out DECIMAL(20,2) NOT NULL DEFAULT 0.00,
    accumulated DECIMAL(20,2) NOT NULL DEFAULT 0.00,
    code VARCHAR(250),
    transaction_content TEXT,
    reference_code VARCHAR(255),
    description TEXT,
    transfer_type VARCHAR(10) NOT NULL,
    transfer_amount DECIMAL(20,2) NOT NULL,
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

-- Add comment for documentation
COMMENT ON TABLE sepay_transactions IS 'Store SePay webhook transaction data for payment processing';
COMMENT ON COLUMN sepay_transactions.sepay_id IS 'Unique transaction ID from SePay';
COMMENT ON COLUMN sepay_transactions.code IS 'Payment code to match with order number';
COMMENT ON COLUMN sepay_transactions.processed IS 'Whether this transaction has been processed for order payment';
COMMENT ON COLUMN sepay_transactions.order_id IS 'Linked order ID if payment code matches';