// src/modules/scheduler/hooks/useVesselTours.js
import { useCallback, useEffect, useState } from "react";
import vesselTrackingService from "../services/vesselTrackingService";

export default function useVesselTours() {
  const [tours, setTours] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loadActiveTours = useCallback(
    async (statuses = ["READY", "IN_PROGRESS"]) => {
      setLoading(true);
      setError("");

      try {
        const data = await vesselTrackingService.getActiveTours(statuses);
        setTours(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error("LOAD VESSEL TOURS ERROR:", err);
        setError(
          err.response?.data?.message ||
            "Không thể tải danh sách tour giám sát.",
        );
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  useEffect(() => {
    loadActiveTours();
  }, [loadActiveTours]);

  return {
    tours,
    loading,
    error,
    loadActiveTours,
  };
}
