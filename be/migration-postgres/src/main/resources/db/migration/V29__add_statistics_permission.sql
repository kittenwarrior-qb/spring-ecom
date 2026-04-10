-- V29: Add STATISTICS_VIEW permission and assign to ADMIN role

INSERT INTO permissions (name)
VALUES ('STATISTICS_VIEW')
ON CONFLICT (name) DO NOTHING;

-- Assign STATISTICS_VIEW permission to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND p.name = 'STATISTICS_VIEW'
ON CONFLICT DO NOTHING;

-- Also assign to SUPERADMIN role if exists
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'SUPERADMIN' AND p.name = 'STATISTICS_VIEW'
ON CONFLICT DO NOTHING;

