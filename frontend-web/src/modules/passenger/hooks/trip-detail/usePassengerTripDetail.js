// src/modules/passenger/hooks/trip-detail/usePassengerTripDetail.js
import { useCallback } from "react";

import usePassengerTripRoute from "./usePassengerTripRoute";
import usePassengerTripItinerary from "./usePassengerTripItinerary";
import usePassengerTripCruise from "./usePassengerTripCruise";
import usePassengerTripFacilities from "./usePassengerTripFacilities";
import usePassengerTripPackages from "./usePassengerTripPackages";

import usePassengerBookingDetail from "./usePassengerBookingDetail";
import usePassengerBookingPassengers from "./usePassengerBookingPassengers";
import usePassengerBookingBenefits from "./usePassengerBookingBenefits";
import usePassengerBookingUsages from "./usePassengerBookingUsages";
import usePassengerBookingBills from "./usePassengerBookingBills";

import usePassengerVesselLocation from "./usePassengerVesselLocation";
import usePassengerTripWeather from "./usePassengerTripWeather";

export default function usePassengerTripDetail({
  tourId,
  bookingId,
  weatherLocation = {},
}) {
  // =========================
  // TOUR SERVICE
  // =========================

  const route = usePassengerTripRoute(tourId);
  const itinerary = usePassengerTripItinerary(tourId);
  const cruise = usePassengerTripCruise(tourId);
  const facilities = usePassengerTripFacilities(tourId);
  const packages = usePassengerTripPackages(tourId);

  // =========================
  // BOOKING SERVICE
  // =========================

  const booking = usePassengerBookingDetail(bookingId);
  const passengers = usePassengerBookingPassengers(bookingId);
  const benefits = usePassengerBookingBenefits(bookingId);
  const usages = usePassengerBookingUsages(bookingId);
  const bills = usePassengerBookingBills(bookingId);

  // =========================
  // SHARED APIs
  // =========================

  const vessel = usePassengerVesselLocation(tourId);

  const weather = usePassengerTripWeather({
    latitude: weatherLocation.latitude,
    longitude: weatherLocation.longitude,
    startDate: weatherLocation.startDate,
    endDate: weatherLocation.endDate,
  });

  // =========================
  // LOAD TOUR DATA
  // =========================

  const loadTourData = useCallback(async () => {
    if (!tourId) return;

    await Promise.allSettled([
      route.loadRoute(),
      itinerary.loadItinerary(),
      cruise.loadCruise(),
      facilities.loadFacilities(),
      packages.loadPackages(),
    ]);
  }, [
    tourId,
    route.loadRoute,
    itinerary.loadItinerary,
    cruise.loadCruise,
    facilities.loadFacilities,
    packages.loadPackages,
  ]);

  // =========================
  // LOAD BOOKING DATA
  // =========================

  const loadBookingData = useCallback(async () => {
    if (!bookingId) return;

    await Promise.allSettled([
      booking.loadBooking(),
      passengers.loadPassengers(),
      benefits.loadBenefits(),
      usages.loadUsages(),
      bills.loadBills(),
    ]);
  }, [
    bookingId,
    booking.loadBooking,
    passengers.loadPassengers,
    benefits.loadBenefits,
    usages.loadUsages,
    bills.loadBills,
  ]);

  // =========================
  // LOAD ALL
  // =========================

  const loadAll = useCallback(async () => {
    await Promise.allSettled([loadTourData(), loadBookingData()]);
  }, [loadTourData, loadBookingData]);

  return {
    tour: {
      route,
      itinerary,
      cruise,
      facilities,
      packages,
    },

    booking: {
      detail: booking,
      passengers,
      benefits,
      usages,
      bills,
    },

    vessel,
    weather,

    loadTourData,
    loadBookingData,
    loadAll,
  };
}
