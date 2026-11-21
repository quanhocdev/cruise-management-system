import { useCallback, useEffect, useState } from "react";
import vesselTrackingService from "../services/vesselTrackingService";

export default function useVesselDetail(tourId) {
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
      // Gọi đồng thời thông tin chi tiết tour, tổng quan route và danh sách chặng
      const [detailsData, overviewData, legsData, posData] = await Promise.all([
        vesselTrackingService.getTourDetails(tourId),
        vesselTrackingService.getTourRouteOverview(tourId).catch(() => null),
        vesselTrackingService.getTourRouteLegs(tourId).catch(() => []),
        vesselTrackingService.getVesselLocation(tourId).catch(() => null),
      ]);

      setTourDetails(detailsData);
      setRouteOverview(overviewData);
      setRouteLegs(legsData);
      if (posData) setVesselPosition(posData);
    } catch (err) {
      console.error("LOAD VESSEL TOUR DETAILS ERROR:", err);
      setError(err.response?.data?.message || "Không thể tải chi tiết tour.");
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
        const position = await vesselTrackingService.getVesselLocation(tourId);
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
