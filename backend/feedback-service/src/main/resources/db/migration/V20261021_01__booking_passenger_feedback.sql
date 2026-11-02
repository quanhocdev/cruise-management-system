ALTER TABLE feedback.feedbacks ADD COLUMN IF NOT EXISTS booking_passenger_id BIGINT;
ALTER TABLE feedback.feedbacks ALTER COLUMN passenger_voyage_id DROP NOT NULL;
CREATE TABLE IF NOT EXISTS feedback.create_requests (
    client_request_id UUID PRIMARY KEY,
    reviewer_user_id UUID NOT NULL,
    payload_hash VARCHAR(64) NOT NULL,
    response_body TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
