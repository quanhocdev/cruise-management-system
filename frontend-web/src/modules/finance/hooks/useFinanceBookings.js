// src/modules/finance/hooks/useFinanceBookings.js
import { useCallback } from "react";
import financeService from "../services/financeService";
import useAsyncData from "./useAsyncData";

// Hook lấy danh sách booking theo Tour ID (chỉ gọi API khi đã chọn tourId)
export const useFinanceBookings = (tourId) => {
  const fetcher = useCallback(
    () => financeService.getBookingsByTour(tourId),
    [tourId],
  );

  const { data, ...rest } = useAsyncData(fetcher, {
    enabled: !!tourId,
  });

  return { bookings: data, ...rest };
};

// Hook lấy danh sách hành khách theo Booking ID (dùng cho trang chi tiết hành khách)
export const useBookingPassengers = (bookingId) => {
  const fetcher = useCallback(
    () => financeService.getPassengersByBooking(bookingId),
    [bookingId],
  );

  const { data, ...rest } = useAsyncData(fetcher, {
    enabled: !!bookingId,
    initial: [],
  });

  return { passengers: data, ...rest };
};
