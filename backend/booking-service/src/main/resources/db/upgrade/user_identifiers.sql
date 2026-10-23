-- Import the authoritative auth ID mapping before upgrading a populated database.
-- Never derive a UUID from a legacy number: that would change account ownership.
CREATE SCHEMA IF NOT EXISTS booking;
CREATE TABLE IF NOT EXISTS booking.user_id_mapping (
    legacy_id BIGINT PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE
);
DO $$ BEGIN
    IF to_regclass('booking.request_dedup') IS NOT NULL THEN
        IF EXISTS (SELECT 1 FROM booking.request_dedup t WHERE t.actor_id ~ '^user:[0-9]+$'
            AND NOT EXISTS (SELECT 1 FROM booking.user_id_mapping m WHERE t.actor_id='user:'||m.legacy_id)) THEN
            RAISE EXCEPTION 'USER_ID_MAPPING_REQUIRED: request_dedup.actor_id';
        END IF;
    END IF;
END $$;
DO $$ BEGIN
    IF to_regclass('booking.ledger_entries') IS NOT NULL THEN
        IF EXISTS (SELECT 1 FROM booking.ledger_entries t WHERE t.actor_id ~ '^user:[0-9]+$'
            AND NOT EXISTS (SELECT 1 FROM booking.user_id_mapping m WHERE t.actor_id='user:'||m.legacy_id)) THEN
            RAISE EXCEPTION 'USER_ID_MAPPING_REQUIRED: ledger_entries.actor_id';
        END IF;
    END IF;
END $$;
DO $$ BEGIN
    IF to_regclass('booking.spending_limit_changes') IS NOT NULL THEN
        IF EXISTS (SELECT 1 FROM booking.spending_limit_changes t WHERE t.actor_id ~ '^user:[0-9]+$'
            AND NOT EXISTS (SELECT 1 FROM booking.user_id_mapping m WHERE t.actor_id='user:'||m.legacy_id)) THEN
            RAISE EXCEPTION 'USER_ID_MAPPING_REQUIRED: spending_limit_changes.actor_id';
        END IF;
    END IF;
END $$;
CREATE OR REPLACE FUNCTION booking.mapped_user_id(old_id BIGINT) RETURNS UUID
LANGUAGE SQL STABLE AS 'SELECT user_id FROM booking.user_id_mapping WHERE legacy_id = old_id';
DO $$
DECLARE
    col RECORD;
    kind TEXT;
    missing BOOLEAN;
BEGIN
    -- Validate every legacy column before changing any data in this transaction.
    FOR col IN SELECT * FROM (VALUES
            ('bookings','created_by_user_id'),
            ('registration_notifications','recipient_id'),
            ('passenger_departures','actor_user_id')
        ) AS columns_to_upgrade(table_name,column_name)
    LOOP
        SELECT data_type INTO kind FROM information_schema.columns
        WHERE table_schema='booking' AND table_name=col.table_name AND column_name=col.column_name;
        IF kind IN ('bigint','integer') THEN
            EXECUTE format('SELECT EXISTS(SELECT 1 FROM %I.%I t WHERE t.%I IS NOT NULL AND NOT EXISTS '
                || '(SELECT 1 FROM booking.user_id_mapping m WHERE m.legacy_id=t.%I))',
                'booking',col.table_name,col.column_name,col.column_name) INTO missing;
            IF missing THEN
                RAISE EXCEPTION 'USER_ID_MAPPING_REQUIRED: %.% requires an auth-approved legacy_id -> UUID mapping',
                    col.table_name,col.column_name;
            END IF;
        ELSIF kind IS NOT NULL AND kind <> 'uuid' THEN
            RAISE EXCEPTION 'UNSUPPORTED_USER_ID_TYPE: %.% (%)',col.table_name,col.column_name,kind;
        END IF;
    END LOOP;
    FOR col IN SELECT * FROM (VALUES
            ('bookings','created_by_user_id'),
            ('registration_notifications','recipient_id'),
            ('passenger_departures','actor_user_id')
        ) AS columns_to_upgrade(table_name,column_name)
    LOOP
        SELECT data_type INTO kind FROM information_schema.columns
        WHERE table_schema='booking' AND table_name=col.table_name AND column_name=col.column_name;
        IF kind IN ('bigint','integer') THEN
            EXECUTE format('ALTER TABLE %I.%I ADD COLUMN IF NOT EXISTS %I BIGINT',
                'booking',col.table_name,'legacy_'||col.column_name);
            EXECUTE format('UPDATE %I.%I SET %I=%I',
                'booking',col.table_name,'legacy_'||col.column_name,col.column_name);
            EXECUTE format('ALTER TABLE %I.%I ALTER COLUMN %I TYPE UUID USING booking.mapped_user_id(%I)',
                'booking',col.table_name,col.column_name,col.column_name);
        END IF;
    END LOOP;
END $$;
DROP FUNCTION booking.mapped_user_id(BIGINT);
DO $$ BEGIN
    IF to_regclass('booking.request_dedup') IS NOT NULL THEN
        UPDATE booking.request_dedup t SET actor_id='user:'||m.user_id
        FROM booking.user_id_mapping m WHERE t.actor_id='user:'||m.legacy_id;
    END IF;
END $$;
DO $$ BEGIN
    IF to_regclass('booking.ledger_entries') IS NOT NULL THEN
        UPDATE booking.ledger_entries t SET actor_id='user:'||m.user_id
        FROM booking.user_id_mapping m WHERE t.actor_id='user:'||m.legacy_id;
    END IF;
END $$;
DO $$ BEGIN
    IF to_regclass('booking.spending_limit_changes') IS NOT NULL THEN
        UPDATE booking.spending_limit_changes t SET actor_id='user:'||m.user_id
        FROM booking.user_id_mapping m WHERE t.actor_id='user:'||m.legacy_id;
    END IF;
END $$;
