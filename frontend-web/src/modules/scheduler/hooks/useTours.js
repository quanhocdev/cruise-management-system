import { useCallback, useEffect, useState } from "react";

import tourService from "../services/tourService";

export default function useTours() {
  const [tours, setTours] = useState([]);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  /**
   * =====================================================
   * LOAD TOURS
   * =====================================================
   *
   * statusTrip:
   *
   * null
   * -> lấy tất cả
   *
   * "DRAFT"
   * -> chỉ lấy DRAFT
   *
   * "APPROVAL_PENDING"
   * -> chỉ lấy APPROVAL_PENDING
   *
   * "APPROVED"
   * -> chỉ lấy APPROVED
   *
   * ...
   */

  const loadTours = useCallback(async (statusTrip = null) => {
    setLoading(true);
    setError("");

    try {
      const data = await tourService.getAllTours(statusTrip);

      setTours(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("LOAD TOURS ERROR:", err);

      setError(err.response?.data?.message || "Không thể tải danh sách Tour.");
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * =====================================================
   * CREATE
   * =====================================================
   */

  const createTour = useCallback(async (data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const created = await tourService.createTour(data);

      setTours((prev) => [...prev, created]);

      setSuccess("Tạo Tour thành công.");

      return created;
    } catch (err) {
      console.error("CREATE TOUR ERROR:", err);

      setError(err.response?.data?.message || "Không thể tạo Tour.");

      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  /**
   * =====================================================
   * UPDATE
   * =====================================================
   */

  const updateTour = useCallback(async (id, data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await tourService.updateTour(id, data);

      setTours((prev) => prev.map((tour) => (tour.id === id ? updated : tour)));

      setSuccess("Cập nhật Tour thành công.");

      return updated;
    } catch (err) {
      console.error("UPDATE TOUR ERROR:", err);

      setError(err.response?.data?.message || "Không thể cập nhật Tour.");

      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  /**
   * =====================================================
   * DELETE
   * =====================================================
   */

  const deleteTour = useCallback(async (id) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await tourService.deleteTour(id);

      setTours((prev) => prev.filter((tour) => tour.id !== id));

      setSuccess("Xóa Tour thành công.");

      return true;
    } catch (err) {
      console.error("DELETE TOUR ERROR:", err);

      setError(err.response?.data?.message || "Không thể xóa Tour.");

      return false;
    } finally {
      setSaving(false);
    }
  }, []);
  /**
   * =====================================================
   * SUBMIT FOR APPROVAL
   * =====================================================
   *
   * DRAFT
   *   ↓
   * APPROVAL_PENDING
   */

  const submitForApproval = useCallback(async (id) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await tourService.submitForApproval(id);

      setTours((prev) => prev.map((tour) => (tour.id === id ? updated : tour)));

      setSuccess("Gửi Tour cho Operation duyệt thành công.");

      return updated;
    } catch (err) {
      console.error("SUBMIT TOUR FOR APPROVAL ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể gửi Tour cho Operation duyệt.",
      );

      throw err;
    } finally {
      setSaving(false);
    }
  }, []);
  /**
   * =====================================================
   * INITIAL LOAD
   * =====================================================
   *
   * Lần đầu vào trang:
   * -> lấy tất cả tour
   */

  useEffect(() => {
    loadTours();
  }, [loadTours]);

  return {
    tours,

    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadTours,

    createTour,
    updateTour,
    deleteTour,
  };
}
