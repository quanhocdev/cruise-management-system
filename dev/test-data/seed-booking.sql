\set ON_ERROR_STOP on
BEGIN;

INSERT INTO booking.info_tour_packages (id, tour_id, name, price, max_passengers, status)
SELECT md5('flow-package-' || i)::uuid,
       md5('flow-tour-' || (((i - 1) / 2) + 1))::uuid,
       'FLOW - ' || CASE WHEN i % 2 = 1 THEN 'Tieu chuan ' ELSE 'Cao cap ' END || (((i - 1) / 2) + 1),
       CASE WHEN i % 2 = 1 THEN 4500000 ELSE 6900000 END, 2, 'ACTIVE'
FROM generate_series(1, 12) AS g(i)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, price = EXCLUDED.price,
    max_passengers = EXCLUDED.max_passengers, status = EXCLUDED.status;

INSERT INTO booking.bookings (id, booking_code, tour_id, tour_package_id, created_by_user_id,
                              primary_contact_name, primary_contact_email, primary_contact_phone,
                              number_of_rooms, number_passengers, total_amount, status, created_at, updated_at)
SELECT 920000 + i,
       'FLOW-BOOK-' || lpad(i::text, 3, '0'),
       md5('flow-tour-' || (((i - 1) % 6) + 1))::uuid,
       md5('flow-package-' || ((((i - 1) % 6) * 2) + 1))::uuid,
       2,
       'Khach kiem thu ' || lpad(i::text, 2, '0'),
       'flow.customer' || lpad(i::text, 2, '0') || '@example.invalid',
       '0908' || lpad(i::text, 6, '0'),
       1, 2, 9000000,
       CASE WHEN i <= 14 THEN 'CONFIRMED' WHEN i <= 19 THEN 'PENDING_PAYMENT' ELSE 'CANCELLED' END,
       CURRENT_TIMESTAMP - (i || ' hours')::interval, CURRENT_TIMESTAMP
FROM generate_series(1, 24) AS g(i)
ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.status, total_amount = EXCLUDED.total_amount,
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO booking.passengers (id, full_name, date_of_birth, gender, id_card_type,
                                identification_number, phone_number, email, document_note,
                                created_at, updated_at)
SELECT 921000 + i,
       'FLOW - Hanh khach ' || lpad(i::text, 3, '0'),
       DATE '1985-01-01' + (i * 120),
       CASE WHEN i % 2 = 0 THEN 'FEMALE' ELSE 'MALE' END,
       CASE WHEN i % 5 = 0 THEN 'PASSPORT' ELSE 'CCCD' END,
       'FLOW-ID-' || lpad(i::text, 6, '0'),
       '0912' || lpad(i::text, 6, '0'),
       'flow.passenger' || lpad(i::text, 3, '0') || '@example.invalid',
       CASE WHEN i % 7 = 0 THEN 'Can kiem tra giay to tai quay' ELSE NULL END,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 48) AS g(i)
ON CONFLICT (id) DO UPDATE SET full_name = EXCLUDED.full_name, document_note = EXCLUDED.document_note,
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO booking.booking_passengers (id, booking_id, passenger_id, room_id, nfc_card_uid,
                                        status, checked_in_at, checked_out_at, created_at, updated_at)
SELECT 922000 + i,
       920000 + (((i - 1) / 2) + 1),
       921000 + i,
       CASE WHEN (((i - 1) / 2) + 1) BETWEEN 9 AND 13
            THEN md5('flow-room-' || CASE ((((i - 1) / 2) % 3) + 1) WHEN 1 THEN 1 WHEN 2 THEN 13 ELSE 22 END)::uuid
            ELSE NULL END,
       CASE WHEN (((i - 1) / 2) + 1) BETWEEN 9 AND 13
            THEN 'FLOW-NFC-' || lpad((36 + i - 17)::text, 3, '0') ELSE NULL END,
       CASE
         WHEN (((i - 1) / 2) + 1) BETWEEN 9 AND 10 THEN 'CHECKED_IN'
         WHEN (((i - 1) / 2) + 1) = 11 THEN 'CHECKED_OUT'
         WHEN (((i - 1) / 2) + 1) = 12 THEN 'CHECKED_IN'
         WHEN (((i - 1) / 2) + 1) = 13 AND i % 2 = 1 THEN 'CHECKED_IN'
         ELSE 'PENDING'
       END,
       CASE WHEN (((i - 1) / 2) + 1) BETWEEN 9 AND 13 THEN CURRENT_TIMESTAMP - INTERVAL '2 hours' ELSE NULL END,
       CASE WHEN (((i - 1) / 2) + 1) = 11 THEN CURRENT_TIMESTAMP - INTERVAL '1 hour' ELSE NULL END,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 48) AS g(i)
ON CONFLICT (id) DO UPDATE SET room_id = EXCLUDED.room_id, nfc_card_uid = EXCLUDED.nfc_card_uid,
    status = EXCLUDED.status, checked_in_at = EXCLUDED.checked_in_at,
    checked_out_at = EXCLUDED.checked_out_at, updated_at = CURRENT_TIMESTAMP;

COMMIT;
