-- V30: Track broadcast notification read status per user

CREATE TABLE IF NOT EXISTS notification_user_reads (
    id BIGSERIAL PRIMARY KEY,
    notification_id BIGINT NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    read_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_notification_user_reads UNIQUE (notification_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_notification_user_reads_user_id
    ON notification_user_reads(user_id);

CREATE INDEX IF NOT EXISTS idx_notification_user_reads_notification_id
    ON notification_user_reads(notification_id);

CREATE INDEX IF NOT EXISTS idx_notification_user_reads_read_at
    ON notification_user_reads(read_at DESC);

