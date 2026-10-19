CREATE SCHEMA IF NOT EXISTS notification;
CREATE TABLE IF NOT EXISTS notification.activity_inbox (
 id UUID PRIMARY KEY,
 recipient_id BIGINT NOT NULL,
 title VARCHAR(255) NOT NULL,
 message VARCHAR(2000) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_activity_inbox_recipient ON notification.activity_inbox(recipient_id,created_at DESC);
