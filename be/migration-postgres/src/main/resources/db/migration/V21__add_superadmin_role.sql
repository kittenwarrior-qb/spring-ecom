-- V21: Add SUPERADMIN role with permission to manage ADMIN role assignment

-- 1. Add SUPERADMIN role
INSERT INTO roles (name) VALUES ('SUPERADMIN') ON CONFLICT (name) DO NOTHING;

-- 2. Add permission for managing ADMIN role assignment
INSERT INTO permissions (name) VALUES ('ROLE_ADMIN_MANAGE') ON CONFLICT (name) DO NOTHING;

-- 3. Assign ROLE_ADMIN_MANAGE permission to SUPERADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'SUPERADMIN' AND p.name = 'ROLE_ADMIN_MANAGE'
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 4. Assign all other permissions to SUPERADMIN (full access like ADMIN)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'SUPERADMIN' AND p.name != 'ROLE_ADMIN_MANAGE'
ON CONFLICT (role_id, permission_id) DO NOTHING;
