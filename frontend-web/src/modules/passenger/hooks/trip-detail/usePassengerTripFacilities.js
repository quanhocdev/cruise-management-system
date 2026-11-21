// src/modules/passenger/hooks/trip-detail/usePassengerTripFacilities.js
import { useCallback } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerTripFacilities(tourId) {
  const productsFetcher = useCallback(
    () => passengerTripService.getProducts(tourId),
    [tourId],
  );

  const servicesFetcher = useCallback(
    () => passengerTripService.getServices(tourId),
    [tourId],
  );

  const activitiesFetcher = useCallback(
    () => passengerTripService.getCruiseActivities(tourId),
    [tourId],
  );

  const visitsFetcher = useCallback(
    () => passengerTripService.getVisits(tourId),
    [tourId],
  );

  const productsResource = usePassengerResource(productsFetcher, []);
  const servicesResource = usePassengerResource(servicesFetcher, []);
  const activitiesResource = usePassengerResource(activitiesFetcher, []);
  const visitsResource = usePassengerResource(visitsFetcher, []);

  const loadFacilities = useCallback(async () => {
    if (!tourId) return;

    await Promise.allSettled([
      productsResource.load(),
      servicesResource.load(),
      activitiesResource.load(),
      visitsResource.load(),
    ]);
  }, [
    tourId,
    productsResource.load,
    servicesResource.load,
    activitiesResource.load,
    visitsResource.load,
  ]);

  return {
    products: productsResource.data,
    services: servicesResource.data,
    cruiseActivities: activitiesResource.data,
    visits: visitsResource.data,

    productsLoading: productsResource.loading,
    servicesLoading: servicesResource.loading,
    activitiesLoading: activitiesResource.loading,
    visitsLoading: visitsResource.loading,

    productsError: productsResource.error,
    servicesError: servicesResource.error,
    activitiesError: activitiesResource.error,
    visitsError: visitsResource.error,

    loadFacilities,
  };
}
