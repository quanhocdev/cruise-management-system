// src/modules/passenger/hooks/trip-detail/usePassengerTripDetail.js
import { useCallback, useRef } from "react";

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
  activeSection = "tracking",
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

  // GPS chỉ polling khi activeSection === "tracking".
  const vessel = usePassengerVesselLocation(
    tourId,
    activeSection === "tracking",
  );

  const weather = usePassengerTripWeather({
    latitude: weatherLocation.latitude,
    longitude: weatherLocation.longitude,
    startDate: weatherLocation.startDate,
    endDate: weatherLocation.endDate,
  });

  // =========================
  // CACHE LOADED SECTIONS
  // =========================

  const loadedSectionsRef = useRef(new Set());

  const loadSection = useCallback(
    async (section, { force = false } = {}) => {
      if (!section) return;

      if (loadedSectionsRef.current.has(section) && !force) {
        return;
      }

      if (section === "tracking") {
        const tasks = [route.loadRoute()];

        if (weather.hasValidCoordinates) {
          tasks.push(weather.loadWeather());
        }

        await Promise.all(tasks);
      } else if (section === "itinerary") {
        if (!tourId) return;
        await itinerary.loadItinerary();
      } else if (section === "cruise") {
        if (!tourId) return;
        await cruise.loadCruise();
      } else if (section === "facilities") {
        if (!tourId) return;

        await Promise.all([
          facilities.loadFacilities(),
          packages.loadPackages(),
        ]);
      } else if (section === "booking") {
        if (!bookingId) return;
        await booking.loadBooking();
      } else if (section === "passengers") {
        if (!bookingId) return;
        await passengers.loadPassengers();
      } else if (section === "benefits") {
        if (!bookingId) return;
        await benefits.loadBenefits();
      } else if (section === "usages") {
        if (!bookingId) return;
        await usages.loadUsages();
      } else if (section === "bills") {
        if (!bookingId) return;
        await bills.loadBills();
      } else {
        return;
      }

      // Chỉ đánh dấu đã tải sau khi loader hoàn tất thành công.
      loadedSectionsRef.current.add(section);
    },
    [
      tourId,
      bookingId,
      route.loadRoute,
      itinerary.loadItinerary,
      cruise.loadCruise,
      facilities.loadFacilities,
      packages.loadPackages,
      booking.loadBooking,
      passengers.loadPassengers,
      benefits.loadBenefits,
      usages.loadUsages,
      bills.loadBills,
      weather.hasValidCoordinates,
      weather.loadWeather,
    ],
  );

  const reloadSection = useCallback(
    async (section) => {
      return loadSection(section, { force: true });
    },
    [loadSection],
  );

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

    loadSection,
    reloadSection,
  };
}
