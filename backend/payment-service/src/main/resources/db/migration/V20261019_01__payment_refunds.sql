CREATE SCHEMA IF NOT EXISTS payment;
CREATE TABLE IF NOT EXISTS payment.payment_refunds (
    id UUID PRIMARY KEY,
    payment_id BIGINT NOT NULL REFERENCES payment.payments(id),
    client_request_id UUID NOT NULL UNIQUE,
    request_hash VARCHAR(64) NOT NULL,
    actor_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    reason VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('QUEUED','SENDING','PROCESSING','UNKNOWN','SUCCEEDED','REJECTED')),
    transaction_type VARCHAR(2) NOT NULL,
    original_transaction_no VARCHAR(100) NOT NULL,
    original_created_at TIMESTAMPTZ NOT NULL,
    provider_transaction_no VARCHAR(100),
    provider_code VARCHAR(40),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    attempted_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS idx_refunds_payment ON payment.payment_refunds(payment_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_refunds_pending ON payment.payment_refunds(status, updated_at);
