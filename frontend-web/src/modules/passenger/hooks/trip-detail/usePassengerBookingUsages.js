// src/modules/passenger/hooks/trip-detail/usePassengerBookingUsages.js
import { useCallback } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerBookingUsages(bookingId) {
  const productFetcher = useCallback(
    () => passengerTripService.getProductUsages(bookingId),
    [bookingId],
  );

  const serviceFetcher = useCallback(
    () => passengerTripService.getServiceUsages(bookingId),
    [bookingId],
  );

  const activityFetcher = useCallback(
    () => passengerTripService.getCruiseActivityUsages(bookingId),
    [bookingId],
  );

  const visitFetcher = useCallback(
    () => passengerTripService.getVisitUsages(bookingId),
    [bookingId],
  );

  const productsResource = usePassengerResource(productFetcher, []);
  const servicesResource = usePassengerResource(serviceFetcher, []);
  const activitiesResource = usePassengerResource(activityFetcher, []);
  const visitsResource = usePassengerResource(visitFetcher, []);

  const loadUsages = useCallback(async () => {
    if (!bookingId) return;

    await Promise.allSettled([
      productsResource.load(),
      servicesResource.load(),
      activitiesResource.load(),
      visitsResource.load(),
    ]);
  }, [
    bookingId,
    productsResource.load,
    servicesResource.load,
    activitiesResource.load,
    visitsResource.load,
  ]);

  return {
    productUsages: productsResource.data,
    serviceUsages: servicesResource.data,
    activityCruiseUsages: activitiesResource.data,
    visitUsages: visitsResource.data,

    productUsagesLoading: productsResource.loading,
    serviceUsagesLoading: servicesResource.loading,
    activityCruiseUsagesLoading: activitiesResource.loading,
    visitUsagesLoading: visitsResource.loading,

    productUsagesError: productsResource.error,
    serviceUsagesError: servicesResource.error,
    activityCruiseUsagesError: activitiesResource.error,
    visitUsagesError: visitsResource.error,

    loadUsages,
  };
}
