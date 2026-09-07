-- Local UI fixture only: writes synchronized activity snapshots, not Kafka producer data.
-- No booking/payment/user records are modified. Run with psql ON_ERROR_STOP=1.
BEGIN;
DO $$
BEGIN
 IF NOT EXISTS (SELECT 1 FROM tour.tours WHERE id='55555555-5555-5555-5555-555555555555' AND name LIKE '%Test%') THEN
   RAISE EXCEPTION 'Expected demo tour not found';
 END IF;
 IF EXISTS (SELECT 1 FROM tour.schedules WHERE tour_id='55555555-5555-5555-5555-555555555555' AND day_number BETWEEN 1 AND 3 AND name NOT LIKE '[TEST]%') THEN
   RAISE EXCEPTION 'Existing user schedule found; refusing to overwrite';
 END IF;
END $$;
INSERT INTO tour.schedules(id,tour_id,day_number,real_day,name,description,status)
SELECT ('a7000000-0000-0000-0000-00000000000'||d)::uuid,t.id,d,t.start_date+(d-1),
 CASE d WHEN 1 THEN '[TEST] Don khach va len tau' WHEN 2 THEN '[TEST] Tham quan va trai nghiem' ELSE '[TEST] Tra phong va ket thuc' END,
 'Du lieu mau kiem thu Passenger; khong phai lich trinh kinh doanh.', 'ACTIVE'
FROM tour.tours t CROSS JOIN generate_series(1,3) d
WHERE t.id='55555555-5555-5555-5555-555555555555'
ON CONFLICT DO NOTHING;
INSERT INTO tour.ports(id,name,city,country,status,created_at,updated_at)
VALUES ('a7000000-0000-0000-0000-000000000010','[TEST] Cang Ha Long','Ha Long','Viet Nam','ACTIVE',now(),now()) ON CONFLICT DO NOTHING;
INSERT INTO tour.cruise_areas(id,cruise_deck_id,name,description,status)
SELECT 'a7000000-0000-0000-0000-000000000020',r.cruise_deck_id,'[TEST] Khu tap Yoga','Khu vuc mau cho UI test','ACTIVE'
FROM tour.rooms r WHERE r.id='44444444-4444-4444-4444-444444444444' ON CONFLICT DO NOTHING;
INSERT INTO tour.schedule_stops(id,schedule_id,port_id,stop_order,arrive_at,leave_at)
SELECT 'a7000000-0000-0000-0000-000000000030',s.id,'a7000000-0000-0000-0000-000000000010',1,s.real_day+time '08:00',s.real_day+time '17:00'
FROM tour.schedules s WHERE s.id='a7000000-0000-0000-0000-000000000002' ON CONFLICT DO NOTHING;
INSERT INTO tour.assignment_activity_cruise(id,tour_id,cruise_area_id,activity_cruise_tour_id,activity_name,activity_description,start_time,end_time,max_passengers,price,status,created_at,updated_at)
SELECT 'a7000000-0000-0000-0000-000000000040',t.id,'a7000000-0000-0000-0000-000000000020','a7000000-0000-0000-0000-000000000041',
 '[TEST] Yoga tren tau','Hoat dong mau mien phi. Chi dung kiem thu giao dien.',t.start_date+time '16:00',t.start_date+time '17:00',20,0,'NOT_STARTED',now(),now()
FROM tour.tours t WHERE t.id='55555555-5555-5555-5555-555555555555' ON CONFLICT DO NOTHING;
INSERT INTO tour.assignment_activity_visit(id,tour_id,schedule_stop_id,visit_tour_id,visit_name,visit_description,start_time,end_time,max_passengers,price,status,created_at,updated_at)
SELECT 'a7000000-0000-0000-0000-000000000050',t.id,'a7000000-0000-0000-0000-000000000030','a7000000-0000-0000-0000-000000000051',
 '[TEST] Tham quan Ha Long','Chuyen tham quan mau; khong tao giao dich hay dang ky.',t.start_date+1+time '09:00',t.start_date+1+time '11:30',12,250000,'NOT_STARTED',now(),now()
FROM tour.tours t WHERE t.id='55555555-5555-5555-5555-555555555555' ON CONFLICT DO NOTHING;
COMMIT;
