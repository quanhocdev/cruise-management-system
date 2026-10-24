ALTER TABLE notification.activity_inbox ADD COLUMN IF NOT EXISTS priority VARCHAR(10) NOT NULL DEFAULT 'NORMAL';
ALTER TABLE notification.activity_inbox ADD COLUMN IF NOT EXISTS booking_id BIGINT;
ALTER TABLE notification.activity_inbox ADD COLUMN IF NOT EXISTS registration_id BIGINT;
ALTER TABLE notification.activity_inbox ADD COLUMN IF NOT EXISTS read_at TIMESTAMPTZ;
CREATE INDEX IF NOT EXISTS idx_activity_inbox_unread ON notification.activity_inbox(recipient_id) WHERE read_at IS NULL;

CREATE TABLE IF NOT EXISTS notification.activity_read_requests (
 operation_type VARCHAR(40) NOT NULL,
 client_request_id UUID NOT NULL,
 recipient_id UUID NOT NULL,
 payload_hash CHAR(64) NOT NULL,
 notification_id UUID NOT NULL REFERENCES notification.activity_inbox(id),
 read_at TIMESTAMPTZ NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY(operation_type, client_request_id)
);
