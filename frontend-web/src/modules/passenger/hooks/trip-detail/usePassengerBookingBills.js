// src/modules/passenger/hooks/trip-detail/usePassengerBookingBills.js
import { useCallback } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerBookingBills(bookingId) {
  const fetcher = useCallback(
    () => passengerTripService.getBookingBills(bookingId),
    [bookingId],
  );

  const resource = usePassengerResource(fetcher, []);

  return {
    bills: resource.data,
    loading: resource.loading,
    error: resource.error,
    loadBills: resource.load,
  };
}
