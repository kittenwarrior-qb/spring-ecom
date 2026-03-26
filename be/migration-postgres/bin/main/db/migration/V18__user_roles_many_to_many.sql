-- V18: Refactor User-Role to Many-to-Many relationship

-- 1. Create user_roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- 2. Migrate existing single role to user_roles (skip if already exists)
INSERT INTO user_roles (user_id, role_id)
SELECT id, role_id FROM users WHERE role_id IS NOT NULL
ON CONFLICT (user_id, role_id) DO NOTHING;

-- 3. Add new permissions (skip if already exists)
INSERT INTO permissions (name) VALUES
    ('ADMIN_ACCESS'),
    ('CART_MANAGE'),
    ('ORDER_CREATE'),
    ('REVIEW_CREATE'),
    ('PROFILE_MANAGE'),
    ('ROLE_VIEW'),
    ('ROLE_CREATE'),
    ('ROLE_UPDATE')
ON CONFLICT (name) DO NOTHING;

-- 4. Assign ADMIN_ACCESS to ADMIN role (skip if already exists)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ADMIN' AND p.name = 'ADMIN_ACCESS'
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 5. Assign basic permissions to USER role (skip if already exists)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'USER' AND p.name IN ('CART_MANAGE', 'ORDER_CREATE', 'ORDER_VIEW', 'REVIEW_CREATE', 'PROFILE_MANAGE', 'PRODUCT_VIEW', 'CATEGORY_VIEW')
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 6. Assign permissions to SELLER role (skip if already exists)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SELLER' AND p.name IN ('ADMIN_ACCESS', 'CART_MANAGE', 'ORDER_CREATE', 'ORDER_VIEW', 'ORDER_UPDATE', 'PROFILE_MANAGE', 'PRODUCT_CREATE', 'PRODUCT_UPDATE', 'PRODUCT_DELETE', 'PRODUCT_VIEW', 'CATEGORY_VIEW')
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 7. Assign ROLE management permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ADMIN' AND p.name IN ('ROLE_VIEW', 'ROLE_CREATE', 'ROLE_UPDATE')
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Note: Keep role_id column in users for backward compatibility during transition
