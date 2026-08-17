import { useState, useEffect, useCallback } from "react";
import { activityCruiseService } from "../services/activityCruiseService";

export const useActivityCruise = () => {
  const [activities, setActivities] = useState([]);

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // FETCH ALL
  // =====================================================
  const fetchActivities = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await activityCruiseService.getAll();
      setActivities(data);
      return data;
    } catch (err) {
      console.error("🔥 FETCH ACTIVITIES ERROR:", err);
      const message =
        err.response?.data?.message || "Không thể tải danh sách hoạt động.";
      setError(message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchActivities();
  }, [fetchActivities]);

  // =====================================================
  // CREATE
  // =====================================================
  const createActivity = useCallback(async (payload) => {
    setSaving(true);
    setError("");
    setSuccess("");
    try {
      const newActivity = await activityCruiseService.create(payload);
      setActivities((prev) => [...prev, newActivity]);
      setSuccess("Tạo hoạt động mới thành công.");
      return newActivity;
    } catch (err) {
      console.error("🔥 CREATE ACTIVITY ERROR:", err);
      const message =
        err.response?.data?.message || "Lỗi khi tạo hoạt động mới.";
      setError(message);
      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // UPDATE
  // =====================================================
  const updateActivity = useCallback(async (id, payload) => {
    setSaving(true);
    setError("");
    setSuccess("");
    try {
      const updated = await activityCruiseService.update(id, payload);
      setActivities((prev) =>
        prev.map((item) => (item.id === id ? updated : item)),
      );
      setSuccess("Cập nhật hoạt động thành công.");
      return updated;
    } catch (err) {
      console.error("🔥 UPDATE ACTIVITY ERROR:", err);
      const message =
        err.response?.data?.message || "Lỗi khi cập nhật hoạt động.";
      setError(message);
      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // DELETE
  // =====================================================
  const deleteActivity = useCallback(async (id) => {
    setSaving(true);
    setError("");
    setSuccess("");
    try {
      await activityCruiseService.delete(id);
      setActivities((prev) => prev.filter((item) => item.id !== id));
      setSuccess("Xóa hoạt động thành công.");
      return true;
    } catch (err) {
      console.error("🔥 DELETE ACTIVITY ERROR:", err);
      const message = err.response?.data?.message || "Lỗi khi xóa hoạt động.";
      setError(message);
      return false;
    } finally {
      setSaving(false);
    }
  }, []);

  return {
    activities,
    loading,
    saving,
    error,
    success,
    setError,
    setSuccess,
    refresh: fetchActivities,
    createActivity,
    updateActivity,
    deleteActivity,
  };
};
