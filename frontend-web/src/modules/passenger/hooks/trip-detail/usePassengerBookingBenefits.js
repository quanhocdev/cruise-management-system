// src/modules/passenger/hooks/trip-detail/usePassengerBookingBenefits.js
import { useCallback } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerBookingBenefits(bookingId) {
  const fetcher = useCallback(
    () => passengerTripService.getBookingBenefits(bookingId),
    [bookingId],
  );

  const resource = usePassengerResource(fetcher, null);

  return {
    benefits: resource.data,
    loading: resource.loading,
    error: resource.error,
    loadBenefits: resource.load,
  };
}
