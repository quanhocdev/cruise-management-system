// src/modules/passenger/hooks/trip-detail/usePassengerTripPackages.js
import { useCallback } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerTripPackages(tourId) {
  const fetcher = useCallback(
    () => passengerTripService.getPackages(tourId),
    [tourId],
  );

  const resource = usePassengerResource(fetcher, []);

  return {
    packages: resource.data,
    loading: resource.loading,
    error: resource.error,
    loadPackages: resource.load,
  };
}
