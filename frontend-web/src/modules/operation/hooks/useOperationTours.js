import { useCallback, useState } from "react";
import operationTourService from "../services/operationTourService";

export default function useOperationTours() {
  // =====================================================
  // TOURS STATES
  // =====================================================
  const [pendingTours, setPendingTours] = useState([]);
  const [approvedTours, setApprovedTours] = useState([]);

  // =====================================================
  // LOADING STATES
  // =====================================================
  const [loading, setLoading] = useState(false);
  const [approving, setApproving] = useState(false);

  // =====================================================
  // MESSAGES
  // =====================================================
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // TOURS API
  // =====================================================
  const loadPendingTours = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await operationTourService.getPendingTours();
      const tourList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setPendingTours(tourList);
    } catch (err) {
      console.error("LOAD OPERATION PENDING TOURS ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách Tour chờ duyệt.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const loadApprovedTours = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await operationTourService.getApprovedTours();
      const tourList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setApprovedTours(tourList);
    } catch (err) {
      console.error("LOAD OPERATION APPROVED TOURS ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách Tour đã được duyệt.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // APPROVE TOUR
  // =====================================================
  const approveTour = useCallback(async (tourId, payload = null) => {
    setApproving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await operationTourService.approveTour(tourId, payload);

      setPendingTours((prev) => prev.filter((tour) => tour.id !== tourId));
      if (updated) {
        setApprovedTours((prev) => [...prev, updated]);
      }

      setSuccess("Duyệt Tour thành công.");
      return updated;
    } catch (err) {
      console.error("APPROVE TOUR ERROR:", err);
      setError(err.response?.data?.message || "Không thể duyệt Tour.");
      throw err;
    } finally {
      setApproving(false);
    }
  }, []);

  // =====================================================
  // CLEAR HELPERS
  // =====================================================
  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  return {
    pendingTours,
    approvedTours,
    loading,
    approving,
    error,
    success,

    loadPendingTours,
    loadApprovedTours,
    approveTour,
    clearMessages,
  };
}
