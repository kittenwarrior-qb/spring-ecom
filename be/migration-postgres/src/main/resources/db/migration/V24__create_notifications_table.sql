-- Create notifications table for MQTT notification system
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    reference_id BIGINT,
    reference_type VARCHAR(50),
    image_url VARCHAR(500),
    action_url VARCHAR(500),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for efficient querying
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read) WHERE is_read = FALSE;

-- Add comment to table
COMMENT ON TABLE notifications IS 'Stores user notifications sent via MQTT';
COMMENT ON COLUMN notifications.type IS 'Type: ORDER_STATUS, ORDER_CONFIRMED, ORDER_SHIPPED, ORDER_DELIVERED, ORDER_CANCELLED, SYSTEM_ANNOUNCEMENT, PROMOTION, etc.';
COMMENT ON COLUMN notifications.reference_id IS 'ID of related entity (order, product, etc.)';
COMMENT ON COLUMN notifications.reference_type IS 'Type of referenced entity: ORDER, PRODUCT, etc.';
COMMENT ON COLUMN notifications.action_url IS 'URL to navigate when notification is clicked';
