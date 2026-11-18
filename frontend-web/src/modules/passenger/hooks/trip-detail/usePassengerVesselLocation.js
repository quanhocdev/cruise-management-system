// src/modules/passenger/hooks/trip-detail/usePassengerVesselLocation.js
import { useCallback, useEffect } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

const GPS_POLLING_INTERVAL = 10000;

export default function usePassengerVesselLocation(tourId) {
  const fetcher = useCallback(() => {
    if (!tourId) return Promise.resolve(null);
    return passengerTripService.getVesselLocation(tourId);
  }, [tourId]);

  const resource = usePassengerResource(fetcher, null);

  useEffect(() => {
    if (!tourId) return undefined;

    resource.load().catch(() => {});

    const intervalId = setInterval(() => {
      resource.load().catch(() => {});
    }, GPS_POLLING_INTERVAL);

    return () => clearInterval(intervalId);
  }, [tourId, resource.load]);

  return {
    vesselLocation: resource.data,
    loading: resource.loading,
    error: resource.error,
    reloadLocation: resource.load,
  };
}
