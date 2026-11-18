// src/modules/passenger/hooks/trip-detail/usePassengerTripItinerary.js
import { useCallback } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerTripItinerary(tourId) {
  const fetcher = useCallback(
    () => passengerTripService.getItinerary(tourId),
    [tourId],
  );

  const resource = usePassengerResource(fetcher, []);

  return {
    itinerary: resource.data,
    loading: resource.loading,
    error: resource.error,
    loadItinerary: resource.load,
  };
}
