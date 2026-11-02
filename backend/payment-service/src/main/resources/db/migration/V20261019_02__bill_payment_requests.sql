CREATE TABLE IF NOT EXISTS payment.bill_payment_locks (bill_id BIGINT PRIMARY KEY);
CREATE TABLE IF NOT EXISTS payment.bill_payment_requests (
 client_request_id UUID PRIMARY KEY, payload_hash VARCHAR(64) NOT NULL,
 response_body TEXT, created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS payment.bill_payment_deliveries (
 payment_id BIGINT PRIMARY KEY REFERENCES payment.payments(id),
 delivered_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS payment.bill_query_schedule (
 payment_id BIGINT PRIMARY KEY REFERENCES payment.payments(id), next_attempt_at TIMESTAMPTZ NOT NULL
);
