ALTER TABLE booking.ledger_entries ADD COLUMN IF NOT EXISTS origin varchar(20) NOT NULL DEFAULT 'LIVE';
ALTER TABLE booking.ledger_entries ADD COLUMN IF NOT EXISTS source_used_at timestamp;
-- Old usage has no per-entry quota snapshot; NULL preserves that unknown value.
ALTER TABLE booking.ledger_entries ALTER COLUMN benefit_quantity DROP NOT NULL;
