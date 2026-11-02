CREATE TABLE IF NOT EXISTS booking.usage_capacity_locks (
    target_type VARCHAR(20) NOT NULL CHECK (target_type IN ('SERVICE', 'ACTIVITY', 'SHORE')),
    target_id UUID NOT NULL,
    PRIMARY KEY (target_type, target_id)
);
