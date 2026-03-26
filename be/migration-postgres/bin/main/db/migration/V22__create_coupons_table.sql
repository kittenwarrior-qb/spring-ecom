-- V22: Create coupons table for discount system

CREATE TABLE coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    
    -- Discount configuration
    discount_type VARCHAR(20) NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
    discount_value DECIMAL(10, 2) NOT NULL CHECK (discount_value > 0),
    
    -- Usage constraints
    min_order_value DECIMAL(10, 2) DEFAULT 0 CHECK (min_order_value >= 0),
    max_discount DECIMAL(10, 2) CHECK (max_discount IS NULL OR max_discount > 0),
    usage_limit INTEGER CHECK (usage_limit IS NULL OR usage_limit > 0),
    used_count INTEGER DEFAULT 0 NOT NULL CHECK (used_count >= 0),
    
    -- Validity period
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_discount_value CHECK (
        (discount_type = 'PERCENTAGE' AND discount_value <= 100) OR
        (discount_type = 'FIXED_AMOUNT')
    ),
    CONSTRAINT chk_dates CHECK (end_date > start_date)
);

-- Index for quick lookup by code
CREATE INDEX idx_coupons_code ON coupons(code) WHERE deleted_at IS NULL;

-- Index for active coupons query
CREATE INDEX idx_coupons_active ON coupons(is_active, start_date, end_date) WHERE deleted_at IS NULL;

-- Add coupon_id to orders table
ALTER TABLE orders ADD COLUMN coupon_id BIGINT REFERENCES coupons(id);
ALTER TABLE orders ADD COLUMN discount_amount DECIMAL(10, 2) DEFAULT 0 CHECK (discount_amount >= 0);

-- Update total calculation note: total = subtotal - discount_amount + shipping_fee
COMMENT ON COLUMN orders.discount_amount IS 'Discount amount applied from coupon';
COMMENT ON TABLE coupons IS 'Coupon codes for order discounts';
