-- Create addresses table
CREATE TABLE IF NOT EXISTS addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address_line VARCHAR(255) NOT NULL,
    ward VARCHAR(100),
    district VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON addresses(user_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_addresses_is_default ON addresses(is_default) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_addresses_deleted_at ON addresses(deleted_at);