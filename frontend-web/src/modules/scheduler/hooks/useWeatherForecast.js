// src/modules/scheduler/hooks/useWeatherForecast.js
import { useCallback, useEffect, useState } from "react";
import vesselTrackingService from "../services/vesselTrackingService";

export default function useWeatherForecast(
  latitude,
  longitude,
  startDate,
  endDate,
) {
  const [weather, setWeather] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loadWeather = useCallback(async () => {
    if (!latitude || !longitude) return;

    setLoading(true);
    setError("");

    try {
      const data = await vesselTrackingService.getWeatherForecast(
        latitude,
        longitude,
        startDate,
        endDate,
      );
      setWeather(data);
    } catch (err) {
      console.error("LOAD WEATHER ERROR:", err);
      setError(
        err.response?.data?.message || "Không thể tải thông tin thời tiết.",
      );
    } finally {
      setLoading(false);
    }
  }, [latitude, longitude, startDate, endDate]);

  useEffect(() => {
    loadWeather();
  }, [loadWeather]);

  return {
    weather,
    loading,
    error,
    reloadWeather: loadWeather,
  };
}
