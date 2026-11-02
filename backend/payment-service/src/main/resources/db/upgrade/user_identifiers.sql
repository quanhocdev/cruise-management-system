-- Import the authoritative auth ID mapping before upgrading a populated database.
-- Never derive a UUID from a legacy number: that would change account ownership.
CREATE SCHEMA IF NOT EXISTS payment;
CREATE TABLE IF NOT EXISTS payment.user_id_mapping (
    legacy_id BIGINT PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE
);
CREATE OR REPLACE FUNCTION payment.mapped_user_id(old_id BIGINT) RETURNS UUID
LANGUAGE SQL STABLE AS 'SELECT user_id FROM payment.user_id_mapping WHERE legacy_id = old_id';
DO $$
DECLARE
    col RECORD;
    kind TEXT;
    missing BOOLEAN;
BEGIN
    -- Validate every legacy column before changing any data in this transaction.
    FOR col IN SELECT * FROM (VALUES
            ('payments','payer_id'),
            ('payment_refunds','actor_id')
        ) AS columns_to_upgrade(table_name,column_name)
    LOOP
        SELECT data_type INTO kind FROM information_schema.columns
        WHERE table_schema='payment' AND table_name=col.table_name AND column_name=col.column_name;
        IF kind IN ('bigint','integer') THEN
            EXECUTE format('SELECT EXISTS(SELECT 1 FROM %I.%I t WHERE t.%I IS NOT NULL AND NOT EXISTS '
                || '(SELECT 1 FROM payment.user_id_mapping m WHERE m.legacy_id=t.%I))',
                'payment',col.table_name,col.column_name,col.column_name) INTO missing;
            IF missing THEN
                RAISE EXCEPTION 'USER_ID_MAPPING_REQUIRED: %.% requires an auth-approved legacy_id -> UUID mapping',
                    col.table_name,col.column_name;
            END IF;
        ELSIF kind IS NOT NULL AND kind <> 'uuid' THEN
            RAISE EXCEPTION 'UNSUPPORTED_USER_ID_TYPE: %.% (%)',col.table_name,col.column_name,kind;
        END IF;
    END LOOP;
    FOR col IN SELECT * FROM (VALUES
            ('payments','payer_id'),
            ('payment_refunds','actor_id')
        ) AS columns_to_upgrade(table_name,column_name)
    LOOP
        SELECT data_type INTO kind FROM information_schema.columns
        WHERE table_schema='payment' AND table_name=col.table_name AND column_name=col.column_name;
        IF kind IN ('bigint','integer') THEN
            EXECUTE format('ALTER TABLE %I.%I ADD COLUMN IF NOT EXISTS %I BIGINT',
                'payment',col.table_name,'legacy_'||col.column_name);
            EXECUTE format('UPDATE %I.%I SET %I=%I',
                'payment',col.table_name,'legacy_'||col.column_name,col.column_name);
            EXECUTE format('ALTER TABLE %I.%I ALTER COLUMN %I TYPE UUID USING payment.mapped_user_id(%I)',
                'payment',col.table_name,col.column_name,col.column_name);
        END IF;
    END LOOP;
END $$;
DROP FUNCTION payment.mapped_user_id(BIGINT);

DO $$ BEGIN
    IF to_regclass('payment.bill_payment_requests') IS NOT NULL THEN
        IF EXISTS (SELECT 1 FROM payment.bill_payment_requests t
            WHERE jsonb_typeof(t.response_body::jsonb->'payerId')='number'
            AND NOT EXISTS (SELECT 1 FROM payment.user_id_mapping m
                WHERE m.legacy_id=(t.response_body::jsonb->>'payerId')::bigint)) THEN
            RAISE EXCEPTION 'USER_ID_MAPPING_REQUIRED: cached payment responses';
        END IF;
        ALTER TABLE payment.bill_payment_requests ADD COLUMN IF NOT EXISTS legacy_response_body TEXT;
        UPDATE payment.bill_payment_requests t
        SET legacy_response_body=t.response_body,
            response_body=jsonb_set(t.response_body::jsonb,'{payerId}',to_jsonb(m.user_id))::text
        FROM payment.user_id_mapping m
        WHERE jsonb_typeof(t.response_body::jsonb->'payerId')='number'
            AND m.legacy_id=(t.response_body::jsonb->>'payerId')::bigint;
    END IF;
END $$;
