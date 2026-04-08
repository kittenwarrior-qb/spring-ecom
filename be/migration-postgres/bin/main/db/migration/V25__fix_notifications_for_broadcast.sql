-- Drop foreign key constraint first
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS fk_notifications_user_id;

-- Remove NOT NULL constraint from user_id
ALTER TABLE notifications ALTER COLUMN user_id DROP NOT NULL;

-- Add foreign key back without ON DELETE CASCADE for null values
ALTER TABLE notifications ADD CONSTRAINT fk_notifications_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;

-- Add index for broadcast notifications (userId IS NULL)
CREATE INDEX idx_notifications_broadcast ON notifications(user_id) WHERE user_id IS NULL;

-- Update existing indexes to include null values
DROP INDEX IF EXISTS idx_notifications_user_unread;
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read) WHERE is_read = FALSE;

-- Add comment for broadcast notifications
COMMENT ON COLUMN notifications.user_id IS 'User ID (null for broadcast notifications to all users)';
