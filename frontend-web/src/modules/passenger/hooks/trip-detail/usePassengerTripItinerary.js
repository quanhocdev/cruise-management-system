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
    // Chuẩn hóa theo cách PassengerTripDetail.jsx đang sử dụng
    data: resource.data,
    loading: resource.loading,
    error: resource.error,
    load: resource.load,

    // Giữ alias cũ để không ảnh hưởng usePassengerTripDetail.js
    itinerary: resource.data,
    loadItinerary: resource.load,
  };
}
