import { useCallback, useState } from "react";

import scheduleService from "../services/scheduleService";

export default function useSchedules(tourId) {
  const [schedules, setSchedules] = useState([]);

  const [loading, setLoading] = useState(false);

  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");

  const [success, setSuccess] = useState("");

  /**
   * =====================================================
   * LOAD ALL
   * =====================================================
   */

  const loadSchedules = useCallback(async () => {
    if (!tourId) {
      setSchedules([]);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const data = await scheduleService.getAllSchedules(tourId);

      setSchedules(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("LOAD SCHEDULES ERROR:", err);

      setError(
        err.response?.data?.message || "Không thể tải danh sách lịch trình.",
      );
    } finally {
      setLoading(false);
    }
  }, [tourId]);

  /**
   * =====================================================
   * LOAD ACTIVE
   * =====================================================
   */

  const loadActiveSchedules = useCallback(async () => {
    if (!tourId) {
      setSchedules([]);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const data = await scheduleService.getActiveSchedules(tourId);

      setSchedules(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("LOAD ACTIVE SCHEDULES ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải lịch trình đang hoạt động.",
      );
    } finally {
      setLoading(false);
    }
  }, [tourId]);

  /**
   * =====================================================
   * CREATE
   * =====================================================
   */

  const createSchedule = useCallback(
    async (data) => {
      setSaving(true);
      setError("");
      setSuccess("");

      try {
        const created = await scheduleService.createSchedule(tourId, data);

        setSchedules((prev) => [...prev, created]);

        setSuccess("Tạo lịch trình thành công.");

        return created;
      } catch (err) {
        console.error("CREATE SCHEDULE ERROR:", err);

        setError(err.response?.data?.message || "Không thể tạo lịch trình.");

        return null;
      } finally {
        setSaving(false);
      }
    },
    [tourId],
  );

  /**
   * =====================================================
   * UPDATE
   * =====================================================
   */

  const updateSchedule = useCallback(
    async (scheduleId, data) => {
      setSaving(true);
      setError("");
      setSuccess("");

      try {
        const updated = await scheduleService.updateSchedule(
          tourId,
          scheduleId,
          data,
        );

        setSchedules((prev) =>
          prev.map((schedule) =>
            schedule.id === scheduleId ? updated : schedule,
          ),
        );

        setSuccess("Cập nhật lịch trình thành công.");

        return updated;
      } catch (err) {
        console.error("UPDATE SCHEDULE ERROR:", err);

        setError(
          err.response?.data?.message || "Không thể cập nhật lịch trình.",
        );

        return null;
      } finally {
        setSaving(false);
      }
    },
    [tourId],
  );

  /**
   * =====================================================
   * DELETE
   * =====================================================
   */

  const deleteSchedule = useCallback(
    async (scheduleId) => {
      setSaving(true);
      setError("");
      setSuccess("");

      try {
        await scheduleService.deleteSchedule(tourId, scheduleId);

        setSchedules((prev) =>
          prev.filter((schedule) => schedule.id !== scheduleId),
        );

        setSuccess("Xóa lịch trình thành công.");

        return true;
      } catch (err) {
        console.error("DELETE SCHEDULE ERROR:", err);

        setError(err.response?.data?.message || "Không thể xóa lịch trình.");

        return false;
      } finally {
        setSaving(false);
      }
    },
    [tourId],
  );

  return {
    schedules,

    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadSchedules,
    loadActiveSchedules,

    createSchedule,
    updateSchedule,
    deleteSchedule,
  };
}
