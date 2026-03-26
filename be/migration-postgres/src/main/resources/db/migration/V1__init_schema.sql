-- Initial schema creation for E-commerce system

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone_number VARCHAR(20),
    date_of_birth DATE,
    avatar_url VARCHAR(500),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    email_verification_token_expiry TIMESTAMP,
    password_reset_token VARCHAR(255),
    password_reset_token_expiry TIMESTAMP,
    last_login_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'USER'))
);

CREATE INDEX idx_users_email ON users(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_username ON users(username) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_deleted_at ON users(deleted_at);

-- Refresh tokens table for JWT authentication
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP,
    replaced_by_token VARCHAR(500),
    device_info VARCHAR(255),
    ip_address VARCHAR(45),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token) WHERE revoked_at IS NULL;
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

-- Categories table for book classification
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    parent_id BIGINT,
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE INDEX idx_categories_slug ON categories(slug) WHERE deleted_at IS NULL;
CREATE INDEX idx_categories_parent_id ON categories(parent_id);
CREATE INDEX idx_categories_deleted_at ON categories(deleted_at);

-- Products table (Books)
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    publisher VARCHAR(255),
    publication_year INT,
    language VARCHAR(50) NOT NULL DEFAULT 'Vietnamese',
    pages INT,
    format VARCHAR(50) NOT NULL DEFAULT 'Paperback',
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    discount_price DECIMAL(10, 2),
    stock_quantity INT NOT NULL DEFAULT 0,
    weight DECIMAL(8, 2),
    dimensions VARCHAR(50),
    cover_image_url VARCHAR(500),
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    is_bestseller BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    view_count INT NOT NULL DEFAULT 0,
    sold_count INT NOT NULL DEFAULT 0,
    rating_average DECIMAL(3, 2) DEFAULT 0.00,
    rating_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_price CHECK (price >= 0),
    CONSTRAINT chk_discount_price CHECK (discount_price IS NULL OR discount_price >= 0),
    CONSTRAINT chk_stock_quantity CHECK (stock_quantity >= 0),
    CONSTRAINT chk_rating_average CHECK (rating_average >= 0 AND rating_average <= 5),
    CONSTRAINT chk_format CHECK (format IN ('Paperback', 'Hardcover', 'Ebook', 'Audiobook'))
);

CREATE INDEX idx_products_slug ON products(slug) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_isbn ON products(isbn) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_author ON products(author);
CREATE INDEX idx_products_publisher ON products(publisher);
CREATE INDEX idx_products_is_featured ON products(is_featured) WHERE is_active = TRUE AND deleted_at IS NULL;
CREATE INDEX idx_products_is_bestseller ON products(is_bestseller) WHERE is_active = TRUE AND deleted_at IS NULL;
CREATE INDEX idx_products_deleted_at ON products(deleted_at);
CREATE INDEX idx_products_price ON products(price);

-- Product images table
CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_images_product_id ON product_images(product_id);

-- Product categories junction table (many-to-many)
CREATE TABLE product_categories (
    product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_product_categories_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_categories_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_categories_category_id ON product_categories(category_id);

-- Product reviews table
CREATE TABLE product_reviews (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    title VARCHAR(255),
    comment TEXT,
    is_verified_purchase BOOLEAN NOT NULL DEFAULT FALSE,
    helpful_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_product_reviews_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_rating CHECK (rating >= 1 AND rating <= 5),
    CONSTRAINT uq_user_product_review UNIQUE (product_id, user_id)
);

CREATE INDEX idx_product_reviews_product_id ON product_reviews(product_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_product_reviews_user_id ON product_reviews(user_id);
CREATE INDEX idx_product_reviews_deleted_at ON product_reviews(deleted_at);