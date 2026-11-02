-- Import the authoritative auth ID mapping before upgrading a populated database.
-- Never derive a UUID from a legacy number: that would change account ownership.
CREATE SCHEMA IF NOT EXISTS notification;
CREATE TABLE IF NOT EXISTS notification.user_id_mapping (
    legacy_id BIGINT PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE
);
CREATE OR REPLACE FUNCTION notification.mapped_user_id(old_id BIGINT) RETURNS UUID
LANGUAGE SQL STABLE AS 'SELECT user_id FROM notification.user_id_mapping WHERE legacy_id = old_id';
DO $$
DECLARE
    col RECORD;
    kind TEXT;
    missing BOOLEAN;
BEGIN
    -- Validate every legacy column before changing any data in this transaction.
    FOR col IN SELECT * FROM (VALUES
            ('notifications','recipient_user_id'),
            ('activity_inbox','recipient_id'),
            ('activity_read_requests','recipient_id')
        ) AS columns_to_upgrade(table_name,column_name)
    LOOP
        SELECT data_type INTO kind FROM information_schema.columns
        WHERE table_schema='notification' AND table_name=col.table_name AND column_name=col.column_name;
        IF kind IN ('bigint','integer') THEN
            EXECUTE format('SELECT EXISTS(SELECT 1 FROM %I.%I t WHERE t.%I IS NOT NULL AND NOT EXISTS '
                || '(SELECT 1 FROM notification.user_id_mapping m WHERE m.legacy_id=t.%I))',
                'notification',col.table_name,col.column_name,col.column_name) INTO missing;
            IF missing THEN
                RAISE EXCEPTION 'USER_ID_MAPPING_REQUIRED: %.% requires an auth-approved legacy_id -> UUID mapping',
                    col.table_name,col.column_name;
            END IF;
        ELSIF kind IS NOT NULL AND kind <> 'uuid' THEN
            RAISE EXCEPTION 'UNSUPPORTED_USER_ID_TYPE: %.% (%)',col.table_name,col.column_name,kind;
        END IF;
    END LOOP;
    FOR col IN SELECT * FROM (VALUES
            ('notifications','recipient_user_id'),
            ('activity_inbox','recipient_id'),
            ('activity_read_requests','recipient_id')
        ) AS columns_to_upgrade(table_name,column_name)
    LOOP
        SELECT data_type INTO kind FROM information_schema.columns
        WHERE table_schema='notification' AND table_name=col.table_name AND column_name=col.column_name;
        IF kind IN ('bigint','integer') THEN
            EXECUTE format('ALTER TABLE %I.%I ADD COLUMN IF NOT EXISTS %I BIGINT',
                'notification',col.table_name,'legacy_'||col.column_name);
            EXECUTE format('UPDATE %I.%I SET %I=%I',
                'notification',col.table_name,'legacy_'||col.column_name,col.column_name);
            EXECUTE format('ALTER TABLE %I.%I ALTER COLUMN %I TYPE UUID USING notification.mapped_user_id(%I)',
                'notification',col.table_name,col.column_name,col.column_name);
        END IF;
    END LOOP;
END $$;
DROP FUNCTION notification.mapped_user_id(BIGINT);
