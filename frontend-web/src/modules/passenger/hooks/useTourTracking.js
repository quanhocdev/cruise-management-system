// src/modules/passenger/hooks/useTourTracking.js
// Chi tiết hành trình + vị trí tàu. Tự làm mới định kỳ khi tour đang IN_PROGRESS.
import { useCallback, useEffect, useState } from "react";
import bookingService from "../services/bookingService";

export default function useTourTracking(tourId, pollMs = 60000) {
  const [tracking, setTracking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [lastFetchedAt, setLastFetchedAt] = useState(null);

  // silent = true: làm mới ngầm, không bật loading và không xóa dữ liệu khi lỗi
  const fetchTracking = useCallback(
    async (silent = false) => {
      if (!tourId) return;
      if (!silent) {
        setLoading(true);
        setError("");
      }
      try {
        const data = await bookingService.getMyBookedTourDetails(tourId);
        setTracking(data);
        setLastFetchedAt(new Date());
      } catch (err) {
        if (!silent) {
          setTracking(null);
          setError(
            err.response?.data?.message ||
              "Không thể tải chi tiết hành trình của tour này.",
          );
        }
      } finally {
        if (!silent) setLoading(false);
      }
    },
    [tourId],
  );

  useEffect(() => {
    fetchTracking(false);
  }, [fetchTracking]);

  const polling = tracking?.statusTrip === "IN_PROGRESS" && pollMs > 0;

  useEffect(() => {
    if (!polling) return undefined;
    const id = setInterval(() => {
      if (!document.hidden) fetchTracking(true);
    }, pollMs);
    return () => clearInterval(id);
  }, [polling, pollMs, fetchTracking]);

  return {
    tracking,
    loading,
    error,
    polling,
    lastFetchedAt,
    reload: () => fetchTracking(false),
  };
}
