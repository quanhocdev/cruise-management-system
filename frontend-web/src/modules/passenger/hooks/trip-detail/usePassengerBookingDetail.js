// src/modules/passenger/hooks/trip-detail/usePassengerBookingDetail.js
import { useCallback } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerBookingDetail(bookingId) {
  const fetcher = useCallback(
    () => passengerTripService.getBookingTripDetail(bookingId),
    [bookingId],
  );

  const resource = usePassengerResource(fetcher, null);

  return {
    booking: resource.data,
    loading: resource.loading,
    error: resource.error,
    loadBooking: resource.load,
  };
}
