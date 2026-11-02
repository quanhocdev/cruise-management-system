ALTER TABLE booking.registration_notifications ADD COLUMN IF NOT EXISTS priority VARCHAR(10) NOT NULL DEFAULT 'NORMAL';
ALTER TABLE booking.registration_notifications ADD COLUMN IF NOT EXISTS booking_id BIGINT;
ALTER TABLE booking.registration_notifications ADD COLUMN IF NOT EXISTS registration_id BIGINT;
