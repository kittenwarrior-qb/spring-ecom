-- V15: Refactor role from enum column to separate roles/permissions tables

-- 1. Create roles table
CREATE TABLE roles (
    id   BIGSERIAL   PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Seed default roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('SELLER'), ('USER');

-- 3. Create permissions table
CREATE TABLE permissions (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 4. Seed default permissions
INSERT INTO permissions (name) VALUES
    ('PRODUCT_CREATE'),
    ('PRODUCT_UPDATE'),
    ('PRODUCT_DELETE'),
    ('PRODUCT_VIEW'),
    ('ORDER_VIEW'),
    ('ORDER_UPDATE'),
    ('ORDER_DELETE'),
    ('USER_VIEW'),
    ('USER_UPDATE'),
    ('USER_DELETE'),
    ('CATEGORY_CREATE'),
    ('CATEGORY_UPDATE'),
    ('CATEGORY_DELETE'),
    ('CATEGORY_VIEW');

-- 5. Create role_permissions junction table (only FK columns, no ORM relations)
CREATE TABLE role_permissions (
    role_id       BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- 6. Seed mappings
-- ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ADMIN';

-- SELLER gets product + order + category view permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SELLER'
  AND p.name IN ('PRODUCT_CREATE', 'PRODUCT_UPDATE', 'PRODUCT_DELETE', 'PRODUCT_VIEW',
                 'ORDER_VIEW', 'ORDER_UPDATE',
                 'CATEGORY_VIEW');

-- USER gets basic view permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'USER'
  AND p.name IN ('PRODUCT_VIEW', 'ORDER_VIEW', 'CATEGORY_VIEW');

-- 7. Add role_id FK column to users table
ALTER TABLE users ADD COLUMN role_id BIGINT REFERENCES roles(id);

-- 8. Migrate existing enum role values to the new role_id
UPDATE users SET role_id = (SELECT id FROM roles WHERE roles.name = users.role);

-- 9. Make role_id NOT NULL after data migration
ALTER TABLE users ALTER COLUMN role_id SET NOT NULL;

-- 10. Drop the old role enum column and its constraint
ALTER TABLE users DROP CONSTRAINT chk_role;
ALTER TABLE users DROP COLUMN role;
