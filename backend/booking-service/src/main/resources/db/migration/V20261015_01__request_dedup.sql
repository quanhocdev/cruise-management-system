CREATE SCHEMA IF NOT EXISTS booking;

CREATE TABLE IF NOT EXISTS booking.request_dedup (
    operation_type varchar(80) NOT NULL,
    client_request_id uuid NOT NULL,
    actor_id varchar(160) NOT NULL,
    payload_hash char(64) NOT NULL,
    response_status integer,
    response_body text,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    retain_until timestamptz,
    PRIMARY KEY (operation_type, client_request_id),
    CHECK ((response_status IS NULL) = (response_body IS NULL)),
    CHECK (response_status IS NULL OR response_status IN (200, 201))
);

-- Unknown voyage end dates retain the result indefinitely; never expire by request age.
CREATE INDEX IF NOT EXISTS idx_request_dedup_retention
    ON booking.request_dedup(retain_until) WHERE retain_until IS NOT NULL;
