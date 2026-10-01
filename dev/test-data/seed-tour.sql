\set ON_ERROR_STOP on
BEGIN;

INSERT INTO tour.cruises (id, code, name, description, max_passengers, status, created_at, updated_at)
SELECT md5('flow-cruise-' || i)::uuid,
       'FLOW-SHIP-' || lpad(i::text, 2, '0'),
       'FLOW - Du thuyen ' || i,
       'Du lieu kiem thu luong POS, co the xoa an toan.',
       500 + i * 100, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 3) AS g(i)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, max_passengers = EXCLUDED.max_passengers,
    status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.cruise_decks (id, cruise_id, deck_number, status)
SELECT md5('flow-deck-' || i)::uuid,
       md5('flow-cruise-' || ((i + 1) / 2))::uuid,
       CASE WHEN i % 2 = 1 THEN 1 ELSE 2 END, 'ACTIVE'
FROM generate_series(1, 6) AS g(i)
ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.status;

INSERT INTO tour.cruise_areas (id, cruise_deck_id, name, description, status)
SELECT md5('flow-area-' || i)::uuid,
       md5('flow-deck-' || ((i + 1) / 2))::uuid,
       'FLOW - Khu vuc ' || lpad(i::text, 2, '0'),
       CASE WHEN i % 2 = 1 THEN 'Khu dich vu va tien ich' ELSE 'Khu sinh hoat va giai tri' END,
       'ACTIVE'
FROM generate_series(1, 12) AS g(i)
ON CONFLICT (id) DO UPDATE SET description = EXCLUDED.description, status = EXCLUDED.status;

INSERT INTO tour.room_types (id, name, description, capacity, price)
VALUES
  (md5('flow-room-type-1')::uuid, 'FLOW - Standard', 'Phong tieu chuan 2 nguoi', 2, 1800000),
  (md5('flow-room-type-2')::uuid, 'FLOW - Deluxe', 'Phong cao cap 3 nguoi', 3, 2800000),
  (md5('flow-room-type-3')::uuid, 'FLOW - Family', 'Phong gia dinh 4 nguoi', 4, 3900000)
ON CONFLICT (id) DO UPDATE SET description = EXCLUDED.description, capacity = EXCLUDED.capacity, price = EXCLUDED.price;

INSERT INTO tour.rooms (id, cruise_deck_id, room_type_id, code, status)
SELECT md5('flow-room-' || i)::uuid,
       md5('flow-deck-' || (((i - 1) / 5) + 1))::uuid,
       md5('flow-room-type-' || (((i - 1) % 3) + 1))::uuid,
       'FLOW-' || lpad((((i - 1) / 5) + 1)::text, 2, '0') || '-' || lpad((((i - 1) % 5) + 1)::text, 2, '0'),
       'ACTIVE'
FROM generate_series(1, 30) AS g(i)
ON CONFLICT (id) DO UPDATE SET room_type_id = EXCLUDED.room_type_id, status = EXCLUDED.status;

INSERT INTO tour.ports (id, name, city, country, address, description, latitude, longitude, status, created_at, updated_at)
VALUES
  (md5('flow-port-1')::uuid, 'FLOW - Cang Sai Gon', 'Ho Chi Minh', 'Viet Nam', 'Quan 4', 'Diem khoi hanh', 10.756, 106.706, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (md5('flow-port-2')::uuid, 'FLOW - Cang Vung Tau', 'Vung Tau', 'Viet Nam', 'Bai Truoc', 'Diem tham quan', 10.346, 107.084, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (md5('flow-port-3')::uuid, 'FLOW - Cang Nha Trang', 'Nha Trang', 'Viet Nam', 'Cau Da', 'Diem tham quan', 12.203, 109.214, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (md5('flow-port-4')::uuid, 'FLOW - Cang Da Nang', 'Da Nang', 'Viet Nam', 'Tien Sa', 'Diem tham quan', 16.120, 108.214, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (md5('flow-port-5')::uuid, 'FLOW - Cang Ha Long', 'Quang Ninh', 'Viet Nam', 'Bai Chay', 'Diem tham quan', 20.950, 107.067, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (md5('flow-port-6')::uuid, 'FLOW - Cang Phu Quoc', 'Kien Giang', 'Viet Nam', 'An Thoi', 'Diem tham quan', 10.021, 104.008, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET description = EXCLUDED.description, status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.tours (id, cruise_id, code, name, description, start_date, end_date,
                        booking_start, booking_end, status_trip, status_booking, created_at, updated_at)
SELECT md5('flow-tour-' || i)::uuid,
       md5('flow-cruise-' || (((i - 1) % 3) + 1))::uuid,
       'FLOW-TOUR-' || lpad(i::text, 2, '0'),
       'FLOW - Hanh trinh kiem thu ' || i,
       'Chuyen di danh rieng cho kiem thu toan bo luong.',
       CURRENT_DATE + (i * 7), CURRENT_DATE + (i * 7) + 3,
       CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP + INTERVAL '5 days',
       (ARRAY['READY','IN_PROGRESS','APPROVED','COMPLETED','CANCELLED','DRAFT'])[i],
       (ARRAY['OPEN','CLOSED','OPEN','CLOSED','CLOSED','WAITING'])[i],
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 6) AS g(i)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date, status_trip = EXCLUDED.status_trip,
    status_booking = EXCLUDED.status_booking, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.schedules (id, tour_id, day_number, name, description, real_day, status)
SELECT md5('flow-schedule-' || i)::uuid,
       md5('flow-tour-' || (((i - 1) / 3) + 1))::uuid,
       ((i - 1) % 3) + 1,
       'FLOW - Ngay ' || (((i - 1) % 3) + 1),
       'Lich trinh kiem thu ngay ' || (((i - 1) % 3) + 1),
       CURRENT_DATE + ((((i - 1) / 3) + 1) * 7) + ((i - 1) % 3), 'ACTIVE'
FROM generate_series(1, 18) AS g(i)
ON CONFLICT (id) DO UPDATE SET real_day = EXCLUDED.real_day, status = EXCLUDED.status;

INSERT INTO tour.schedule_stops (id, schedule_id, port_id, stop_order, arrive_at, leave_at)
SELECT md5('flow-stop-' || i)::uuid,
       md5('flow-schedule-' || (((i - 1) / 2) + 1))::uuid,
       md5('flow-port-' || (((i - 1) % 6) + 1))::uuid,
       ((i - 1) % 2) + 1,
       CURRENT_TIMESTAMP + (i || ' days')::interval,
       CURRENT_TIMESTAMP + (i || ' days')::interval + INTERVAL '6 hours'
FROM generate_series(1, 36) AS g(i)
ON CONFLICT (id) DO UPDATE SET arrive_at = EXCLUDED.arrive_at, leave_at = EXCLUDED.leave_at;

INSERT INTO tour.tour_packages (id, tour_id, room_type_id, name, description, price, status, created_at, updated_at)
SELECT md5('flow-package-' || i)::uuid,
       md5('flow-tour-' || (((i - 1) / 2) + 1))::uuid,
       md5('flow-room-type-' || CASE WHEN i % 2 = 1 THEN 1 ELSE 2 END)::uuid,
       'FLOW - ' || CASE WHEN i % 2 = 1 THEN 'Tieu chuan ' ELSE 'Cao cap ' END || (((i - 1) / 2) + 1),
       'Goi kiem thu co phong va quyen loi dich vu.',
       CASE WHEN i % 2 = 1 THEN 4500000 ELSE 6900000 END,
       'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 12) AS g(i)
ON CONFLICT (id) DO UPDATE SET price = EXCLUDED.price, status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.products (id, name, description, price, stock_quantity, status, created_at, updated_at)
SELECT md5('flow-product-' || i)::uuid, 'FLOW - San pham ' || lpad(i::text, 2, '0'),
       'San pham kiem thu tai cua hang tren tau.', 30000 + i * 15000, 20 + i * 5,
       'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 8) AS g(i)
ON CONFLICT (id) DO UPDATE SET price = EXCLUDED.price, stock_quantity = EXCLUDED.stock_quantity,
    status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.services (id, name, description, price, duration_minutes, max_passengers, status, created_at, updated_at)
SELECT md5('flow-service-' || i)::uuid, 'FLOW - Dich vu ' || lpad(i::text, 2, '0'),
       'Dich vu kiem thu tren du thuyen.', 120000 + i * 50000, 30 + i * 10, 5 + i * 2,
       'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 6) AS g(i)
ON CONFLICT (id) DO UPDATE SET price = EXCLUDED.price, duration_minutes = EXCLUDED.duration_minutes,
    max_passengers = EXCLUDED.max_passengers, status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.activity_cruise (id, name, description, status, created_at, updated_at)
SELECT md5('flow-activity-' || i)::uuid, 'FLOW - Hoat dong ' || lpad(i::text, 2, '0'),
       'Hoat dong giai tri kiem thu tren tau.', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 6) AS g(i)
ON CONFLICT (id) DO UPDATE SET description = EXCLUDED.description, status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.product_tour (id, tour_id, cruise_area_id, product_id, quantity, status, created_at, updated_at)
SELECT md5('flow-product-tour-' || i)::uuid, md5('flow-tour-' || i)::uuid,
       md5('flow-area-' || ((((i - 1) % 3) * 4) + 1))::uuid,
       md5('flow-product-' || i)::uuid, 25 + i * 5, 'CONFIGURED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 6) AS g(i)
ON CONFLICT (id) DO UPDATE SET quantity = EXCLUDED.quantity, status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.service_tours (id, tour_id, cruise_area_id, service_id, max_passengers, duration_minutes, status, created_at, updated_at)
SELECT md5('flow-service-tour-' || i)::uuid, md5('flow-tour-' || i)::uuid,
       md5('flow-area-' || ((((i - 1) % 3) * 4) + 2))::uuid,
       md5('flow-service-' || i)::uuid, 10 + i, 45 + i * 5, 'CONFIGURED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 6) AS g(i)
ON CONFLICT (id) DO UPDATE SET max_passengers = EXCLUDED.max_passengers,
    duration_minutes = EXCLUDED.duration_minutes, status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.activity_cruise_tour (id, tour_id, cruise_area_id, activity_cruise_id, activity_name,
                                       activity_description, price, max_passengers, start_time, end_time,
                                       status, created_at, updated_at)
SELECT md5('flow-activity-tour-' || i)::uuid, md5('flow-tour-' || i)::uuid,
       md5('flow-area-' || ((((i - 1) % 3) * 4) + 3))::uuid,
       md5('flow-activity-' || i)::uuid, 'FLOW - Hoat dong ' || lpad(i::text, 2, '0'),
       'Phien hoat dong kiem thu.', 180000 + i * 20000, 20 + i,
       CURRENT_TIMESTAMP + (i || ' days')::interval,
       CURRENT_TIMESTAMP + (i || ' days')::interval + INTERVAL '2 hours',
       'CONFIGURED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 6) AS g(i)
ON CONFLICT (id) DO UPDATE SET price = EXCLUDED.price, status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.visit_tour (id, tour_id, schedule_stop_id, name, description, price, max_passengers,
                             start_time, end_time, status, created_at, updated_at)
SELECT md5('flow-visit-' || i)::uuid,
       md5('flow-tour-' || (((i - 1) / 2) + 1))::uuid,
       md5('flow-stop-' || ((((i - 1) / 2) * 6) + ((i - 1) % 2) + 1))::uuid,
       'FLOW - Tham quan bo ' || lpad(i::text, 2, '0'), 'Chuong trinh SHORE kiem thu.',
       350000 + i * 25000, 30 + i,
       CURRENT_TIMESTAMP + (i || ' days')::interval,
       CURRENT_TIMESTAMP + (i || ' days')::interval + INTERVAL '4 hours',
       CASE WHEN i <= 8 THEN 'CONFIGURED' WHEN i <= 10 THEN 'COMPLETED' ELSE 'CANCELLED' END,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 12) AS g(i)
ON CONFLICT (id) DO UPDATE SET price = EXCLUDED.price, status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.nfc_cards (id, card_uid, status, created_at, updated_at)
SELECT md5('flow-nfc-' || i)::uuid, 'FLOW-NFC-' || lpad(i::text, 3, '0'),
       CASE WHEN i <= 35 THEN 'AVAILABLE' WHEN i <= 47 THEN 'ASSIGNED' ELSE 'INACTIVE' END,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM generate_series(1, 50) AS g(i)
ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.policies (id, title, content, type, status, created_at, updated_at)
VALUES
  (md5('flow-policy-booking')::uuid, 'FLOW - Chinh sach dat cho', 'Du lieu mau cho kiem thu chinh sach dat tour.', 'BOOKING', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (md5('flow-policy-cancel')::uuid, 'FLOW - Chinh sach huy', 'Du lieu mau cho kiem thu hoan huy tour.', 'CANCEL', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET content = EXCLUDED.content, status = EXCLUDED.status, updated_at = CURRENT_TIMESTAMP;

INSERT INTO tour.booking_policies (id, policy_id, days_before_departure, discount_percent, status)
VALUES
  (md5('flow-booking-policy-1')::uuid, md5('flow-policy-booking')::uuid, 30, 10, 'ACTIVE'),
  (md5('flow-booking-policy-2')::uuid, md5('flow-policy-booking')::uuid, 14, 5, 'ACTIVE')
ON CONFLICT (id) DO UPDATE SET discount_percent = EXCLUDED.discount_percent, status = EXCLUDED.status;

INSERT INTO tour.cancel_policies (id, policy_id, days_before, refund_percent, status)
VALUES
  (md5('flow-cancel-policy-1')::uuid, md5('flow-policy-cancel')::uuid, 30, 100, 'ACTIVE'),
  (md5('flow-cancel-policy-2')::uuid, md5('flow-policy-cancel')::uuid, 14, 70, 'ACTIVE'),
  (md5('flow-cancel-policy-3')::uuid, md5('flow-policy-cancel')::uuid, 3, 20, 'ACTIVE')
ON CONFLICT (id) DO UPDATE SET refund_percent = EXCLUDED.refund_percent, status = EXCLUDED.status;

COMMIT;
