\set ON_ERROR_STOP on
BEGIN;
CREATE TEMP TABLE demo_payment AS
SELECT i, CASE WHEN (i-1)/10+1>6 THEN (i-1)/10+3 ELSE (i-1)/10+1 END AS n,((i-1)%10)%3+1 AS k,
CASE WHEN i>60 THEN 'SUCCESS' WHEN (i-1)%10=7 THEN 'PENDING' WHEN (i-1)%10=8 THEN 'FAILED' WHEN (i-1)%10=9 THEN 'REFUNDED' ELSE 'SUCCESS' END AS status
FROM generate_series(1,80)i;
INSERT INTO payment.payments(id,amount,payer_id,reference_id,reference_type,method,status,transaction_code,response_code,expires_at,paid_at,created_at,updated_at)
SELECT -953000-i,(ARRAY[4900000,7900000,10900000])[k]+n*100000,-940008,-950000-i,'BOOKING','VNPAY',status,
'SEA-TXN-'||lpad(i::text,3,'0'),CASE WHEN status IN('SUCCESS','REFUNDED') THEN '00' ELSE '99' END,
now()+interval '15 minutes',CASE WHEN status IN('SUCCESS','REFUNDED') THEN now()-interval '3 days' END,now()-interval '3 days',now() FROM demo_payment ON CONFLICT DO NOTHING;
COMMIT;
