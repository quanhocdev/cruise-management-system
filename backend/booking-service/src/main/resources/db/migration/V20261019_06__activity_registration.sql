CREATE TABLE IF NOT EXISTS booking.activity_registrations (
 id BIGSERIAL PRIMARY KEY,
 booking_id BIGINT NOT NULL REFERENCES booking.bookings(id),
 booking_passenger_id BIGINT NOT NULL REFERENCES booking.booking_passengers(id),
 tour_id UUID NOT NULL,
 target_type VARCHAR(12) NOT NULL CHECK (target_type IN ('ACTIVITY','SHORE')),
 target_id UUID NOT NULL,
 name VARCHAR(255) NOT NULL,
 starts_at TIMESTAMP NOT NULL,
 ends_at TIMESTAMP NOT NULL,
 listed_price NUMERIC(19,2) NOT NULL,
 status VARCHAR(16) NOT NULL CHECK (status IN ('CONFIRMED','CANCELLED','ATTENDED')),
 usage_id BIGINT,
 attendance_actor VARCHAR(100),
 attendance_reason VARCHAR(1000),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 last_checked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_active_registration ON booking.activity_registrations(booking_passenger_id,target_type,target_id) WHERE status <> 'CANCELLED';
CREATE INDEX IF NOT EXISTS idx_registration_capacity ON booking.activity_registrations(target_type,target_id,status);
CREATE INDEX IF NOT EXISTS idx_registration_booking ON booking.activity_registrations(booking_id,id);
CREATE TABLE IF NOT EXISTS booking.registration_notifications (
 id UUID PRIMARY KEY,
 recipient_id BIGINT NOT NULL,
 title VARCHAR(255) NOT NULL,
 message VARCHAR(2000) NOT NULL,
 delivered BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
