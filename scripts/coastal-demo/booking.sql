\set ON_ERROR_STOP on
BEGIN;
SET LOCAL TIME ZONE 'Asia/Ho_Chi_Minh';
CREATE FUNCTION pg_temp.did(kind text, n int) RETURNS uuid LANGUAGE sql IMMUTABLE AS $$ SELECT md5('coastal-v1-' || kind || '-' || n)::uuid $$;
INSERT INTO booking.info_tour_packages(id,tour_id,name,price,max_passengers,status)
SELECT pg_temp.did('package',n*10+k),pg_temp.did('tour',n),(ARRAY['Nghỉ dưỡng Deluxe','Trải nghiệm Suite','Gia đình trọn gói'])[k],
(ARRAY[4900000,7900000,10900000])[k]+n*100000,k+1,'ACTIVE' FROM generate_series(1,10)n CROSS JOIN generate_series(1,3)k ON CONFLICT DO NOTHING;
CREATE TEMP TABLE demo_book AS
SELECT i, CASE WHEN (i-1)/10+1>6 THEN (i-1)/10+3 ELSE (i-1)/10+1 END AS n,
(i-1)%10+1 AS local_no,((i-1)%10)%3+1 AS k,
CASE WHEN i>60 THEN 'CONFIRMED' WHEN (i-1)%10 IN(7,8) THEN 'PENDING_PAYMENT' WHEN (i-1)%10=9 THEN 'CANCELLED' ELSE 'CONFIRMED' END AS status
FROM generate_series(1,80)i;
INSERT INTO booking.bookings(id,booking_code,tour_id,tour_package_id,created_by_user_id,primary_contact_name,primary_contact_email,primary_contact_phone,number_of_rooms,number_passengers,total_amount,status,created_at,updated_at)
SELECT -950000-i,'SEA-BOOK-'||lpad(i::text,3,'0'),pg_temp.did('tour',n),pg_temp.did('package',n*10+k),-940008,
(ARRAY['Nguyễn Minh Anh','Trần Quốc Bảo','Lê Thu Hà','Phạm Hoàng Nam','Võ Ngọc Lan','Đặng Gia Huy','Bùi Thanh Mai','Đỗ Minh Khang','Hồ Bảo Ngọc','Dương Đức Hải'])[local_no]||' (mẫu '||i||')',
'sea.customer'||i||'@example.invalid','090'||lpad(i::text,7,'0'),1,2,(ARRAY[4900000,7900000,10900000])[k]+n*100000,status,now()-interval '3 days',now() FROM demo_book ON CONFLICT DO NOTHING;
INSERT INTO booking.passengers(id,full_name,date_of_birth,gender,id_card_type,identification_number,phone_number,email,document_note,created_at,updated_at)
SELECT -951000-j,(ARRAY['Nguyễn Minh Anh','Trần Quốc Bảo','Lê Thu Hà','Phạm Hoàng Nam','Võ Ngọc Lan','Đặng Gia Huy','Bùi Thanh Mai','Đỗ Minh Khang'])[((j-1)%8)+1]||' (mẫu '||j||')',
date '1980-01-01'+j*60,CASE WHEN j%2=0 THEN 'FEMALE' ELSE 'MALE' END,CASE WHEN j%4=0 THEN 'PASSPORT' ELSE 'CCCD' END,
'SEA-ID-'||lpad(j::text,6,'0'),'091'||lpad(j::text,7,'0'),'sea.passenger'||j||'@example.invalid','Nhân vật giả lập để test; không phải giấy tờ thật.',now(),now() FROM generate_series(1,160)j ON CONFLICT DO NOTHING;
INSERT INTO booking.booking_passengers(id,booking_id,passenger_id,room_id,nfc_card_uid,status,checked_in_at,checked_out_at,created_at,updated_at)
SELECT -952000-j,-950000-i,-951000-j,
CASE WHEN n IN(9,10) THEN pg_temp.did('room',n*100+(k-1)*8+(local_no-1)/3+1) END,
CASE WHEN n=9 THEN 'SEA-NFC-'||lpad(j::text,3,'0') END,
CASE WHEN n=9 THEN 'CHECKED_IN' WHEN n=10 THEN 'CHECKED_OUT' ELSE 'PENDING' END,
CASE WHEN n=9 THEN now()-interval '1 day' WHEN n=10 THEN now()-interval '10 days' END,
CASE WHEN n=10 THEN now()-interval '8 days' END,now(),now()
FROM demo_book CROSS JOIN LATERAL generate_series(i*2-1,i*2)j ON CONFLICT DO NOTHING;
COMMIT;
