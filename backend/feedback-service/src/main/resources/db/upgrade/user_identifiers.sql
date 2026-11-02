-- Import the authoritative auth ID mapping before upgrading a populated database.
-- Never derive a UUID from a legacy number: that would change account ownership.
CREATE SCHEMA IF NOT EXISTS feedback;
CREATE TABLE IF NOT EXISTS feedback.user_id_mapping (
    legacy_id BIGINT PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE
);
CREATE OR REPLACE FUNCTION feedback.mapped_user_id(old_id BIGINT) RETURNS UUID
LANGUAGE SQL STABLE AS 'SELECT user_id FROM feedback.user_id_mapping WHERE legacy_id = old_id';
DO $$
DECLARE
    col RECORD;
    kind TEXT;
    missing BOOLEAN;
BEGIN
    -- Validate every legacy column before changing any data in this transaction.
    FOR col IN SELECT * FROM (VALUES
            ('feedbacks','reviewer_user_id'),
            ('feedbacks','moderated_by_user_id'),
            ('create_requests','reviewer_user_id')
        ) AS columns_to_upgrade(table_name,column_name)
    LOOP
        SELECT data_type INTO kind FROM information_schema.columns
        WHERE table_schema='feedback' AND table_name=col.table_name AND column_name=col.column_name;
        IF kind IN ('bigint','integer') THEN
            EXECUTE format('SELECT EXISTS(SELECT 1 FROM %I.%I t WHERE t.%I IS NOT NULL AND NOT EXISTS '
                || '(SELECT 1 FROM feedback.user_id_mapping m WHERE m.legacy_id=t.%I))',
                'feedback',col.table_name,col.column_name,col.column_name) INTO missing;
            IF missing THEN
                RAISE EXCEPTION 'USER_ID_MAPPING_REQUIRED: %.% requires an auth-approved legacy_id -> UUID mapping',
                    col.table_name,col.column_name;
            END IF;
        ELSIF kind IS NOT NULL AND kind <> 'uuid' THEN
            RAISE EXCEPTION 'UNSUPPORTED_USER_ID_TYPE: %.% (%)',col.table_name,col.column_name,kind;
        END IF;
    END LOOP;
    FOR col IN SELECT * FROM (VALUES
            ('feedbacks','reviewer_user_id'),
            ('feedbacks','moderated_by_user_id'),
            ('create_requests','reviewer_user_id')
        ) AS columns_to_upgrade(table_name,column_name)
    LOOP
        SELECT data_type INTO kind FROM information_schema.columns
        WHERE table_schema='feedback' AND table_name=col.table_name AND column_name=col.column_name;
        IF kind IN ('bigint','integer') THEN
            EXECUTE format('ALTER TABLE %I.%I ADD COLUMN IF NOT EXISTS %I BIGINT',
                'feedback',col.table_name,'legacy_'||col.column_name);
            EXECUTE format('UPDATE %I.%I SET %I=%I',
                'feedback',col.table_name,'legacy_'||col.column_name,col.column_name);
            EXECUTE format('ALTER TABLE %I.%I ALTER COLUMN %I TYPE UUID USING feedback.mapped_user_id(%I)',
                'feedback',col.table_name,col.column_name,col.column_name);
        END IF;
    END LOOP;
END $$;
DROP FUNCTION feedback.mapped_user_id(BIGINT);

DO $$ BEGIN
    IF to_regclass('feedback.create_requests') IS NOT NULL THEN
        IF EXISTS (SELECT 1 FROM feedback.create_requests t
            WHERE jsonb_typeof(t.response_body::jsonb->'reviewerUserId')='number'
            AND NOT EXISTS (SELECT 1 FROM feedback.user_id_mapping m
                WHERE m.legacy_id=(t.response_body::jsonb->>'reviewerUserId')::bigint)) THEN
            RAISE EXCEPTION 'USER_ID_MAPPING_REQUIRED: cached feedback responses';
        END IF;
        ALTER TABLE feedback.create_requests ADD COLUMN IF NOT EXISTS legacy_response_body TEXT;
        UPDATE feedback.create_requests t
        SET legacy_response_body=t.response_body,
            response_body=jsonb_set(t.response_body::jsonb,'{reviewerUserId}',to_jsonb(m.user_id))::text
        FROM feedback.user_id_mapping m
        WHERE jsonb_typeof(t.response_body::jsonb->'reviewerUserId')='number'
            AND m.legacy_id=(t.response_body::jsonb->>'reviewerUserId')::bigint;
    END IF;
END $$;
