\set ON_ERROR_STOP on
BEGIN;

INSERT INTO payment.payments (id, amount, payer_id, reference_id, reference_type, method, status,
                              transaction_code, response_code, payment_url, expires_at, paid_at,
                              created_at, updated_at)
SELECT 930000 + i, 9000000, 2, 920000 + i, 'BOOKING',
       CASE WHEN i % 2 = 0 THEN 'MOMO' ELSE 'VNPAY' END,
       CASE WHEN i <= 14 THEN 'SUCCESS' WHEN i <= 17 THEN 'PENDING'
            WHEN i <= 19 THEN 'FAILED' WHEN i <= 22 THEN 'REFUNDED' ELSE 'EXPIRED' END,
       CASE WHEN i <= 14 OR i BETWEEN 20 AND 22 THEN 'FLOW-TXN-' || lpad(i::text, 3, '0') ELSE NULL END,
       CASE WHEN i <= 14 THEN '00' WHEN i BETWEEN 18 AND 19 THEN '99' ELSE NULL END,
       CASE WHEN i BETWEEN 15 AND 17 THEN 'https://sandbox.example.invalid/pay/FLOW-' || i ELSE NULL END,
       CURRENT_TIMESTAMP + CASE WHEN i BETWEEN 15 AND 17 THEN INTERVAL '15 minutes' ELSE INTERVAL '0 minutes' END,
       CASE WHEN i <= 14 OR i BETWEEN 20 AND 22 THEN CURRENT_TIMESTAMP - (i || ' hours')::interval ELSE NULL END,
       CURRENT_TIMESTAMP - (i || ' hours')::interval, CURRENT_TIMESTAMP
FROM generate_series(1, 24) AS g(i)
ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.status, response_code = EXCLUDED.response_code,
    paid_at = EXCLUDED.paid_at, updated_at = CURRENT_TIMESTAMP;

COMMIT;
