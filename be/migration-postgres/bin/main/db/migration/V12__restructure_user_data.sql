-- Migration to restructure user data
-- Step 1: Remove any existing address fields from users table (cleanup)
ALTER TABLE users 
DROP COLUMN IF EXISTS address,
DROP COLUMN IF EXISTS city,
DROP COLUMN IF EXISTS district,
DROP COLUMN IF EXISTS ward;

-- Step 2: Remove user info fields from users table (keep core auth fields only)
ALTER TABLE users 
DROP COLUMN IF EXISTS phone_number,
DROP COLUMN IF EXISTS date_of_birth,
DROP COLUMN IF EXISTS avatar_url,
DROP COLUMN IF EXISTS first_name,
DROP COLUMN IF EXISTS last_name;

-- Step 3: Create user_info table to store all user profile information
CREATE TABLE IF NOT EXISTS user_info (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone_number VARCHAR(20),
    date_of_birth DATE,
    avatar_url VARCHAR(500),
    address VARCHAR(255),
    ward VARCHAR(100),
    district VARCHAR(100),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_user_info_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Step 4: Create indexes for user_info table
CREATE INDEX IF NOT EXISTS idx_user_info_user_id ON user_info(user_id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_user_info_phone ON user_info(phone_number) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_user_info_deleted_at ON user_info(deleted_at);

-- Step 5: Drop addresses table (will be replaced by user_info)
DROP TABLE IF EXISTS addresses CASCADE;