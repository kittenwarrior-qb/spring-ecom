-- V19: Add ROLE management permissions

-- 1. Add ROLE management permissions
INSERT INTO permissions (name) VALUES
    ('ROLE_VIEW'),
    ('ROLE_CREATE'),
    ('ROLE_UPDATE')
ON CONFLICT (name) DO NOTHING;

-- 2. Assign ROLE management permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ADMIN' AND p.name IN ('ROLE_VIEW', 'ROLE_CREATE', 'ROLE_UPDATE')
ON CONFLICT (role_id, permission_id) DO NOTHING;
