// src/modules/passenger/hooks/trip-detail/usePassengerBookingPassengers.js
import { useCallback } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerBookingPassengers(bookingId) {
  const fetcher = useCallback(
    () => passengerTripService.getBookingPassengers(bookingId),
    [bookingId],
  );

  const resource = usePassengerResource(fetcher, []);

  return {
    passengers: resource.data,
    loading: resource.loading,
    error: resource.error,
    loadPassengers: resource.load,
  };
}
