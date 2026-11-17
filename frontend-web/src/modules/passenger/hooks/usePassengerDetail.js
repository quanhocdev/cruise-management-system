// src/modules/passenger/hooks/usePassengerDetail.js
import { useCallback, useEffect, useState } from "react";
import passengerTrackingService from "../services/passengerTrackingService";

export default function usePassengerDetail(tourId) {
  const [tourDetails, setTourDetails] = useState(null);
  const [routeOverview, setRouteOverview] = useState(null);
  const [routeLegs, setRouteLegs] = useState([]);
  const [vesselPosition, setVesselPosition] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loadTourDetails = useCallback(async () => {
    if (!tourId) {
      setTourDetails(null);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const [detailsData, overviewData, legsData, posData] = await Promise.all([
        passengerTrackingService.getTourDetails(tourId),
        passengerTrackingService.getTourRouteOverview(tourId).catch(() => null),
        passengerTrackingService.getTourRouteLegs(tourId).catch(() => []),
        passengerTrackingService.getVesselLocation(tourId).catch(() => null),
      ]);

      setTourDetails(detailsData);
      setRouteOverview(overviewData);
      setRouteLegs(legsData);
      if (posData) setVesselPosition(posData);
    } catch (err) {
      console.error("LOAD PASSENGER TOUR DETAILS ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải chi tiết hành trình tour.",
      );
    } finally {
      setLoading(false);
    }
  }, [tourId]);

  useEffect(() => {
    loadTourDetails();
  }, [loadTourDetails]);

  // Polling tọa độ GPS thực tế từ Redis mỗi 10 giây
  useEffect(() => {
    if (!tourId) return;

    const fetchLocationOnly = async () => {
      try {
        const position =
          await passengerTrackingService.getVesselLocation(tourId);
        if (position) {
          setVesselPosition(position);
        }
      } catch (err) {
        // Bỏ qua nếu Redis chưa có
      }
    };

    const interval = setInterval(fetchLocationOnly, 10000);
    return () => clearInterval(interval);
  }, [tourId]);

  return {
    tourDetails,
    routeOverview,
    routeLegs,
    vesselPosition,
    loading,
    error,
    reload: loadTourDetails,
  };
}
