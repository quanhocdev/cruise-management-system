// src/modules/scheduler/hooks/useVesselDetail.js
import { useCallback, useEffect, useState } from "react";
import vesselTrackingService from "../services/vesselTrackingService";

export default function useVesselDetail(tourId) {
  const [tourDetails, setTourDetails] = useState(null);
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
      const data = await vesselTrackingService.getTourDetails(tourId);
      setTourDetails(data);
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

  // Polling riêng tọa độ GPS từ Redis định kỳ mỗi 10 giây để cập nhật icon tàu real-time
  useEffect(() => {
    if (!tourId) return;

    const fetchLocationOnly = async () => {
      try {
        const position = await vesselTrackingService.getVesselLocation(tourId);
        if (position) {
          setTourDetails((prev) =>
            prev ? { ...prev, currentPosition: position } : prev,
          );
        }
      } catch (err) {
        // Bỏ qua lỗi ngầm nếu Redis chưa có tọa độ đẩy lên
      }
    };

    const interval = setInterval(fetchLocationOnly, 10000);
    return () => clearInterval(interval);
  }, [tourId]);

  return {
    tourDetails,
    loading,
    error,
    reload: loadTourDetails,
  };
}
