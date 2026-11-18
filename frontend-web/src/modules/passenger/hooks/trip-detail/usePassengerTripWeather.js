// src/modules/passenger/hooks/trip-detail/usePassengerTripWeather.js
import { useCallback } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerTripWeather({
  latitude,
  longitude,
  startDate,
  endDate,
}) {
  const hasValidCoordinates =
    latitude != null &&
    longitude != null &&
    Number.isFinite(Number(latitude)) &&
    Number.isFinite(Number(longitude));

  const fetcher = useCallback(() => {
    if (!hasValidCoordinates) {
      return Promise.resolve(null);
    }

    return passengerTripService.getWeather(
      latitude,
      longitude,
      startDate,
      endDate,
    );
  }, [hasValidCoordinates, latitude, longitude, startDate, endDate]);

  const resource = usePassengerResource(fetcher, null);

  return {
    weather: resource.data,
    loading: resource.loading,
    error: resource.error,
    hasValidCoordinates,
    loadWeather: resource.load,
  };
}
