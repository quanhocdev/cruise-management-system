\set ON_ERROR_STOP on
BEGIN;
SET LOCAL TIME ZONE 'Asia/Ho_Chi_Minh';
CREATE FUNCTION pg_temp.did(kind text, n int) RETURNS uuid LANGUAGE sql IMMUTABLE AS $$ SELECT md5('coastal-v1-' || kind || '-' || n)::uuid $$;
CREATE TEMP TABLE destinations(n int, place text, port text, excursion text, city text, lat numeric, lon numeric);
INSERT INTO destinations VALUES
(1,'Hạ Long','Cảng Tuần Châu','Khám phá hang Sửng Sốt và đảo Ti Tốp','Quảng Ninh',20.922,106.986),
(2,'Lan Hạ','Bến Bèo','Chèo kayak và khám phá làng Việt Hải','Hải Phòng',20.720,107.058),
(3,'Bái Tử Long','Cảng Hòn Gai','Tham quan làng chài Vung Viêng','Quảng Ninh',20.950,107.080),
(4,'Đà Nẵng','Cảng Tiên Sa','Tham quan bán đảo Sơn Trà','Đà Nẵng',16.120,108.214),
(5,'Nha Trang','Cảng Cầu Đá','Khám phá vịnh Nha Trang','Khánh Hòa',12.203,109.214),
(6,'Phú Quốc','Cảng An Thới','Tham quan quần đảo An Thới','Kiên Giang',10.021,104.008),
(7,'Hạ Long','Cảng Tuần Châu','Tham quan đảo Ti Tốp','Quảng Ninh',20.922,106.986),
(8,'Lan Hạ','Bến Bèo','Khám phá làng Việt Hải','Hải Phòng',20.720,107.058),
(9,'Hạ Long','Cảng Tuần Châu','Khám phá hang Sửng Sốt','Quảng Ninh',20.922,106.986),
(10,'Phú Quốc','Cảng An Thới','Tham quan đảo và làng chài','Kiên Giang',10.021,104.008);

INSERT INTO tour.cruises(id,code,name,description,max_passengers,status,created_at,updated_at)
SELECT pg_temp.did('ship',n),'SEA-SHIP-'||lpad(n::text,2,'0'),'Du thuyền Ngọc Biển '||n,
'Tàu mô phỏng phục vụ kiểm thử: 24 phòng, nhà hàng, spa, sân ngắm cảnh và quầy tiện ích.',72,'ACTIVE',now(),now() FROM destinations ON CONFLICT DO NOTHING;
INSERT INTO tour.cruise_decks(id,cruise_id,deck_number,status)
SELECT pg_temp.did('deck',n*10+d),pg_temp.did('ship',n),d,'ACTIVE' FROM destinations CROSS JOIN generate_series(1,3) d ON CONFLICT DO NOTHING;
INSERT INTO tour.cruise_areas(id,cruise_deck_id,name,description,status)
SELECT pg_temp.did('area',n*10+a),pg_temp.did('deck',n*10+((a-1)/2+1)),
(ARRAY['Nhà hàng Hải Phong','Quầy lưu niệm','Spa Sóng Biển','Phòng sinh hoạt','Sân ngắm cảnh','Sân khấu hoàng hôn'])[a],
'Khu vực dịch vụ trên tàu mô phỏng','ACTIVE' FROM destinations CROSS JOIN generate_series(1,6) a ON CONFLICT DO NOTHING;
INSERT INTO tour.room_types(id,name,capacity,price,description)
SELECT pg_temp.did('type',k),(ARRAY['SEA • Deluxe hướng biển','SEA • Suite ban công','SEA • Family kết nối'])[k],k+1,
(ARRAY[1800000,2800000,3900000])[k],'Hạng phòng mô phỏng có sức chứa và giá riêng' FROM generate_series(1,3) k ON CONFLICT DO NOTHING;
INSERT INTO tour.rooms(id,cruise_deck_id,room_type_id,code,status)
SELECT pg_temp.did('room',n*100+r),pg_temp.did('deck',n*10+((r-1)/8+1)),pg_temp.did('type',(r-1)/8+1),
'SEA-'||n||'-'||lpad(r::text,2,'0'),'ACTIVE' FROM destinations CROSS JOIN generate_series(1,24) r ON CONFLICT DO NOTHING;
INSERT INTO tour.ports(id,name,city,country,address,description,latitude,longitude,status,created_at,updated_at)
SELECT pg_temp.did('port',n),'SEA • '||port||' ('||n||')',city,'Việt Nam',port,
'Điểm đón trả khách mô phỏng; tọa độ tham khảo.',lat,lon,'ACTIVE',now(),now() FROM destinations ON CONFLICT DO NOTHING;
INSERT INTO tour.tours(id,cruise_id,code,name,description,start_date,end_date,booking_start,booking_end,status_trip,status_booking,created_at,updated_at)
SELECT pg_temp.did('tour',n),pg_temp.did('ship',n),'SEA-'||lpad(n::text,2,'0'),
place||' • Du thuyền 3 ngày 2 đêm'||CASE WHEN n=7 THEN ' — Chờ duyệt' WHEN n=8 THEN ' — Chờ vận hành' WHEN n=9 THEN ' — Đang khởi hành' WHEN n=10 THEN ' — Chuyến đã kết thúc' ELSE '' END,
'Tour mô phỏng: nghỉ dưỡng hướng biển, ẩm thực, spa, hoạt động trên tàu và tham quan địa phương. Lịch trình, giá và nhà cung cấp đều là dữ liệu test.',
CASE WHEN n=9 THEN current_date-1 WHEN n=10 THEN current_date-10 ELSE current_date+14+n END,
CASE WHEN n=9 THEN current_date+1 WHEN n=10 THEN current_date-8 ELSE current_date+16+n END,
CASE WHEN n IN(7,8) THEN current_timestamp+interval '5 days' ELSE current_timestamp-interval '30 days' END,
CASE WHEN n IN(9,10) THEN current_timestamp-interval '2 days' ELSE current_timestamp+interval '12 days' END,
CASE WHEN n=7 THEN 'APPROVAL_PENDING' WHEN n=8 THEN 'APPROVED' WHEN n=9 THEN 'IN_PROGRESS' WHEN n=10 THEN 'COMPLETED' ELSE 'READY' END,
CASE WHEN n IN(7,8) THEN 'WAITING' WHEN n IN(9,10) THEN 'CLOSED' ELSE 'OPEN' END,now(),now() FROM destinations ON CONFLICT DO NOTHING;
INSERT INTO tour.schedules(id,tour_id,day_number,name,description,real_day,status)
SELECT pg_temp.did('day',n*10+d),t.id,d,(ARRAY['Đón khách và khám phá vịnh','Trải nghiệm địa phương và nghỉ dưỡng','Bình minh trên biển và trả khách'])[d],
'Ăn sáng 07:00; hoạt động theo lịch; ăn trưa 12:00; ăn tối 18:30.',t.start_date+d-1,'ACTIVE'
FROM destinations JOIN tour.tours t ON t.id=pg_temp.did('tour',n) CROSS JOIN generate_series(1,3) d ON CONFLICT DO NOTHING;
INSERT INTO tour.schedule_stops(id,schedule_id,port_id,stop_order,arrive_at,leave_at)
SELECT pg_temp.did('stop',n*10+d),pg_temp.did('day',n*10+d),pg_temp.did('port',n),1,
t.start_date+d-1+time '08:00',t.start_date+d-1+time '17:00'
FROM destinations JOIN tour.tours t ON t.id=pg_temp.did('tour',n) CROSS JOIN generate_series(1,3) d ON CONFLICT DO NOTHING;
INSERT INTO tour.tour_packages(id,tour_id,room_type_id,name,description,price,status,created_at,updated_at)
SELECT pg_temp.did('package',n*10+k),pg_temp.did('tour',n),pg_temp.did('type',k),
(ARRAY['Nghỉ dưỡng Deluxe','Trải nghiệm Suite','Gia đình trọn gói'])[k],
'Giá cho một phòng / chuyến. Bao gồm suất ăn, nước uống, hoạt động và quyền lợi mô phỏng trong danh sách.',
(ARRAY[4900000,7900000,10900000])[k]+n*100000,'ACTIVE',now(),now() FROM destinations CROSS JOIN generate_series(1,3) k ON CONFLICT DO NOTHING;
INSERT INTO tour.products(id,name,description,price,stock_quantity,status,created_at,updated_at)
SELECT pg_temp.did('product',k),(ARRAY['SEA • Nước suối','SEA • Nước ép cam','SEA • Cà phê sữa','SEA • Trà sen','SEA • Bánh quy','SEA • Trái cây','SEA • Áo thun lưu niệm','SEA • Nón đi biển','SEA • Túi chống nước','SEA • Kem chống nắng','SEA • Khăn tắm','SEA • Mô hình du thuyền'])[k],
'Sản phẩm mẫu tại quầy tiện ích. Giá tính theo đơn vị.',(ARRAY[15000,45000,35000,30000,50000,80000,180000,120000,90000,220000,150000,350000])[k],200,'ACTIVE',now(),now() FROM generate_series(1,12) k ON CONFLICT DO NOTHING;
INSERT INTO tour.services(id,name,description,price,duration_minutes,max_passengers,status,created_at,updated_at)
SELECT pg_temp.did('service',k),(ARRAY['SEA • Buffet hải sản','SEA • Massage thư giãn','SEA • Xông hơi','SEA • Giặt ủi','SEA • Chụp ảnh kỷ niệm','SEA • Bữa tối riêng','SEA • Trang trí sinh nhật','SEA • Đồ uống hoàng hôn'])[k],
'Dịch vụ mô phỏng, cần nhân viên xác nhận lịch phục vụ.',(ARRAY[450000,500000,200000,120000,350000,900000,650000,180000])[k],60,12,'ACTIVE',now(),now() FROM generate_series(1,8) k ON CONFLICT DO NOTHING;
INSERT INTO tour.activity_cruise(id,name,description,status,created_at,updated_at)
SELECT pg_temp.did('activity',k),(ARRAY['SEA • Lớp nấu ăn','SEA • Yoga bình minh','SEA • Nhạc sống hoàng hôn'])[k],
'Hoạt động tập thể trên du thuyền mô phỏng','ACTIVE',now(),now() FROM generate_series(1,3) k ON CONFLICT DO NOTHING;
INSERT INTO tour.product_tour(id,tour_id,cruise_area_id,product_id,quantity,status,created_at,updated_at)
SELECT pg_temp.did('pt',n*100+k),pg_temp.did('tour',n),pg_temp.did('area',n*10+2),pg_temp.did('product',k),30,
CASE WHEN n=8 THEN 'WAITING_CONFIG' WHEN n=9 THEN 'IN_PROGRESS' WHEN n=10 THEN 'COMPLETED' ELSE 'CONFIGURED' END,now(),now() FROM destinations CROSS JOIN generate_series(1,12) k ON CONFLICT DO NOTHING;
INSERT INTO tour.service_tours(id,tour_id,cruise_area_id,service_id,duration_minutes,max_passengers,status,created_at,updated_at)
SELECT pg_temp.did('st',n*100+k),pg_temp.did('tour',n),pg_temp.did('area',n*10+CASE WHEN k IN(2,3) THEN 3 ELSE 1 END),pg_temp.did('service',k),60,12,
CASE WHEN n=8 THEN 'WAITING_CONFIG' WHEN n=9 THEN 'IN_PROGRESS' WHEN n=10 THEN 'COMPLETED' ELSE 'CONFIGURED' END,now(),now() FROM destinations CROSS JOIN generate_series(1,8) k ON CONFLICT DO NOTHING;
INSERT INTO tour.activity_cruise_tour(id,tour_id,cruise_area_id,activity_cruise_id,activity_name,activity_description,price,max_passengers,start_time,end_time,status,created_at,updated_at)
SELECT pg_temp.did('at',n*10+k),t.id,pg_temp.did('area',n*10+k+3),a.id,a.name,a.description,150000,20,
t.start_date+time '18:00'+(k-1)*interval '1 day',t.start_date+time '19:00'+(k-1)*interval '1 day',
CASE WHEN n=8 THEN 'WAITING_CONFIG' WHEN n=9 THEN 'IN_PROGRESS' WHEN n=10 THEN 'COMPLETED' ELSE 'CONFIGURED' END,now(),now()
FROM destinations JOIN tour.tours t ON t.id=pg_temp.did('tour',n) CROSS JOIN generate_series(1,3) k JOIN tour.activity_cruise a ON a.id=pg_temp.did('activity',k) ON CONFLICT DO NOTHING;
INSERT INTO tour.visit_tour(id,tour_id,schedule_stop_id,name,description,price,max_passengers,start_time,end_time,status,created_at,updated_at)
SELECT pg_temp.did('visit',n*10+d),t.id,pg_temp.did('stop',n*10+d),excursion||' — ngày '||d,
'Hướng dẫn viên đón khách tại bến lúc 09:00; tập trung trở lại lúc 11:30. Chương trình mô phỏng.',350000,24,
t.start_date+d-1+time '09:00',t.start_date+d-1+time '11:30',
CASE WHEN n=8 THEN 'WAITING_CONFIG' WHEN n=9 THEN 'IN_PROGRESS' WHEN n=10 THEN 'COMPLETED' ELSE 'CONFIGURED' END,now(),now()
FROM destinations JOIN tour.tours t ON t.id=pg_temp.did('tour',n) CROSS JOIN generate_series(1,3) d ON CONFLICT DO NOTHING;
INSERT INTO tour.package_benefits(id,tour_package_id,type,reference_id,quantity,discount_percent,created_at,updated_at)
SELECT pg_temp.did('benefit',n*100+k*10+b),pg_temp.did('package',n*10+k),
(ARRAY['PRODUCT','SERVICE','ACTIVITY_CRUISE','ACTIVITY_VISIT'])[b],
CASE b WHEN 1 THEN pg_temp.did('pt',n*100+1) WHEN 2 THEN pg_temp.did('st',n*100+1) WHEN 3 THEN pg_temp.did('at',n*10+1) ELSE pg_temp.did('visit',n*10+2) END,
CASE WHEN b=1 THEN 6 ELSE 1 END,100,now(),now()
FROM destinations CROSS JOIN generate_series(1,3) k CROSS JOIN generate_series(1,4) b ON CONFLICT DO NOTHING;
INSERT INTO tour.nfc_cards(id,card_uid,status,created_at,updated_at)
SELECT pg_temp.did('nfc',i),'SEA-NFC-'||lpad(i::text,3,'0'),CASE WHEN i BETWEEN 121 AND 140 THEN 'ASSIGNED' WHEN i>190 THEN 'INACTIVE' ELSE 'AVAILABLE' END,now(),now() FROM generate_series(1,200)i ON CONFLICT DO NOTHING;
COMMIT;
