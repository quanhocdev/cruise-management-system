CREATE TABLE IF NOT EXISTS booking.passenger_departures (
    booking_passenger_id BIGINT PRIMARY KEY REFERENCES booking.booking_passengers(id),
    booking_id BIGINT NOT NULL REFERENCES booking.bookings(id),
    actor_user_id UUID NOT NULL,
    room_returned BOOLEAN NOT NULL CHECK (room_returned),
    wristband_returned BOOLEAN NOT NULL CHECK (wristband_returned),
    reason VARCHAR(500) NOT NULL CHECK (length(trim(reason)) > 0),
    departed_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS ix_passenger_departures_booking ON booking.passenger_departures(booking_id);
