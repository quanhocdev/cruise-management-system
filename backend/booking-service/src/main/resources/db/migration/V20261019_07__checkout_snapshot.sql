ALTER TABLE booking.bill_items ADD COLUMN IF NOT EXISTS passenger_name VARCHAR(255);
ALTER TABLE booking.bills ADD COLUMN IF NOT EXISTS preview_version VARCHAR(64);
CREATE TABLE IF NOT EXISTS booking.bill_paid_deliveries (
 bill_id BIGINT PRIMARY KEY REFERENCES booking.bills(id), payload TEXT NOT NULL,
 delivered_at TIMESTAMPTZ
);
