// src/modules/passenger/hooks/trip-detail/usePassengerTripRoute.js
import { useCallback } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerTripRoute(tourId) {
  const fetchOverview = useCallback(
    () => passengerTripService.getRouteOverview(tourId),
    [tourId],
  );

  const fetchLegs = useCallback(
    () => passengerTripService.getRouteLegs(tourId),
    [tourId],
  );

  const overviewResource = usePassengerResource(fetchOverview, null);
  const legsResource = usePassengerResource(fetchLegs, []);

  const loadRoute = useCallback(async () => {
    if (!tourId) return;

    await Promise.allSettled([overviewResource.load(), legsResource.load()]);
  }, [tourId, overviewResource.load, legsResource.load]);

  return {
    overview: overviewResource.data,
    legs: legsResource.data,
    overviewLoading: overviewResource.loading,
    legsLoading: legsResource.loading,
    overviewError: overviewResource.error,
    legsError: legsResource.error,
    loadRoute,
  };
}
