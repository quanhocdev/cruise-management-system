\set ON_ERROR_STOP on
BEGIN;
DELETE FROM booking.booking_passengers WHERE id BETWEEN 922001 AND 922048;
DELETE FROM booking.passengers WHERE id BETWEEN 921001 AND 921048;
DELETE FROM booking.bookings WHERE id BETWEEN 920001 AND 920024;
DELETE FROM booking.info_tour_packages WHERE id IN (SELECT md5('flow-package-' || i)::uuid FROM generate_series(1, 12) g(i));
COMMIT;
