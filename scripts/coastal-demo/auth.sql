\set ON_ERROR_STOP on
BEGIN;
INSERT INTO users(id,username,email,password,provider,status,enabled,role_id,created_at,updated_at)
SELECT -940000-v.n,'demo.'||v.username,'demo.'||v.username||'@example.invalid',u.password,'LOCAL','ACTIVE',true,u.role_id,now(),now()
FROM (VALUES (1,'admin'),(2,'scheduler'),(3,'operation'),(4,'finance'),(5,'convenience'),(6,'onboard'),(7,'shore'),(8,'passenger')) v(n,username)
JOIN users u ON u.username=v.username
ON CONFLICT DO NOTHING;
COMMIT;
