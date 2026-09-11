// src/modules/operation/hooks/useOperationTours.js

import { useCallback, useState } from "react";
import operationTourService from "../services/operationTourService";

export default function useOperationTours() {
  const [pendingTours, setPendingTours] = useState([]);
  const [approvedTours, setApprovedTours] = useState([]);
  const [readyTours, setReadyTours] = useState([]);

  const [loading, setLoading] = useState(false);
  const [approving, setApproving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadPendingTours = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await operationTourService.getPendingTours();
      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setPendingTours(list);
    } catch (err) {
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
      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setApprovedTours(list);
    } catch (err) {
      setError(
        err.response?.data?.message || "Không thể tải danh sách Tour đã duyệt.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const loadReadyTours = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await operationTourService.getReadyTours();
      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setReadyTours(list);
    } catch (err) {
      setReadyTours([]);
    } finally {
      setLoading(false);
    }
  }, []);

  const approveTour = useCallback(async (tourId, payload = null) => {
    setApproving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await operationTourService.approveTour(tourId, payload);
      setSuccess("Duyệt Tour thành công.");
      return updated;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể duyệt Tour.");
      throw err;
    } finally {
      setApproving(false);
    }
  }, []);

  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  return {
    pendingTours,
    approvedTours,
    readyTours,
    loading,
    approving,
    error,
    success,

    loadPendingTours,
    loadApprovedTours,
    loadReadyTours,
    approveTour,
    clearMessages,
  };
}
