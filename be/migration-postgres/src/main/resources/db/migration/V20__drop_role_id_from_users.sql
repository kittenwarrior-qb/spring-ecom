-- Remove role_id column from users table (now using user_roles many-to-many)
ALTER TABLE users DROP COLUMN IF EXISTS role_id;
