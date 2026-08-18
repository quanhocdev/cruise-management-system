import { useCallback, useState } from "react";
import operationTourService from "../services/operationTourService";

export default function useOperationTours() {
  const [pendingTours, setPendingTours] = useState([]);
  const [approvedTours, setApprovedTours] = useState([]);
  const [availableCruises, setAvailableCruises] = useState([]);

  const [loading, setLoading] = useState(false);
  const [cruiseLoading, setCruiseLoading] = useState(false);
  const [approving, setApproving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  /**
   * LOAD PENDING TOURS
   */
  const loadPendingTours = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await operationTourService.getPendingTours();
      // Xử lý nếu backend bọc kết quả trong data.result hoặc data.content
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

  /**
   * LOAD Approved TOURS
   */
  const loadApprovedTours = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await operationTourService.getApprovedTours();
      // Xử lý nếu backend bọc kết quả trong data.result hoặc data.content
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

  /**
   * LOAD AVAILABLE CRUISES
   */
  const loadAvailableCruises = useCallback(async (tourId) => {
    setCruiseLoading(true);
    setError("");
    setAvailableCruises([]);

    try {
      const data = await operationTourService.getAvailableCruises(tourId);
      const cruiseList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setAvailableCruises(cruiseList);
      return cruiseList;
    } catch (err) {
      console.error("LOAD AVAILABLE CRUISES ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách du thuyền khả dụng.",
      );
      return [];
    } finally {
      setCruiseLoading(false);
    }
  }, []);

  /**
   * APPROVE TOUR
   */
  const approveTour = useCallback(async (tourId, cruiseId) => {
    setApproving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await operationTourService.approveTour(tourId, cruiseId);
      setPendingTours((prev) => prev.filter((tour) => tour.id !== tourId));
      setSuccess("Duyệt Tour và gán du thuyền thành công.");
      return updated;
    } catch (err) {
      console.error("APPROVE TOUR ERROR:", err);
      setError(err.response?.data?.message || "Không thể duyệt Tour.");
      throw err;
    } finally {
      setApproving(false);
    }
  }, []);

  const clearAvailableCruises = useCallback(() => {
    setAvailableCruises([]);
  }, []);

  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  return {
    pendingTours,
    approvedTours,

    availableCruises,

    loading,
    cruiseLoading,
    approving,

    error,
    success,

    loadPendingTours,
    loadApprovedTours,
    loadAvailableCruises,
    approveTour,

    clearAvailableCruises,
    clearMessages,
  };
}
