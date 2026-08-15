import { useCallback, useState } from "react";

import scheduleStopService from "../services/scheduleStopService";

export default function useScheduleStops(scheduleId) {
  const [scheduleStops, setScheduleStops] = useState([]);

  const [loading, setLoading] = useState(false);

  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");

  const [success, setSuccess] = useState("");

  /**
   * =====================================================
   * LOAD
   * =====================================================
   */

  const loadScheduleStops = useCallback(async () => {
    if (!scheduleId) {
      setScheduleStops([]);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const data = await scheduleStopService.getAllScheduleStops(scheduleId);

      setScheduleStops(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("LOAD SCHEDULE STOPS ERROR:", err);

      setError(err.response?.data?.message || "Không thể tải các điểm dừng.");
    } finally {
      setLoading(false);
    }
  }, [scheduleId]);

  /**
   * =====================================================
   * CREATE
   * =====================================================
   */

  const createScheduleStop = useCallback(
    async (data) => {
      setSaving(true);
      setError("");
      setSuccess("");

      try {
        const created = await scheduleStopService.createScheduleStop(
          scheduleId,
          data,
        );

        setScheduleStops((prev) =>
          [...prev, created].sort(
            (a, b) => (a.stopOrder ?? 0) - (b.stopOrder ?? 0),
          ),
        );

        setSuccess("Thêm điểm dừng thành công.");

        return created;
      } catch (err) {
        console.error("CREATE SCHEDULE STOP ERROR:", err);

        setError(err.response?.data?.message || "Không thể thêm điểm dừng.");

        return null;
      } finally {
        setSaving(false);
      }
    },
    [scheduleId],
  );

  /**
   * =====================================================
   * UPDATE
   * =====================================================
   */

  const updateScheduleStop = useCallback(
    async (stopId, data) => {
      setSaving(true);
      setError("");
      setSuccess("");

      try {
        const updated = await scheduleStopService.updateScheduleStop(
          scheduleId,
          stopId,
          data,
        );

        setScheduleStops((prev) =>
          prev
            .map((stop) => (stop.id === stopId ? updated : stop))
            .sort((a, b) => (a.stopOrder ?? 0) - (b.stopOrder ?? 0)),
        );

        setSuccess("Cập nhật điểm dừng thành công.");

        return updated;
      } catch (err) {
        console.error("UPDATE SCHEDULE STOP ERROR:", err);

        setError(
          err.response?.data?.message || "Không thể cập nhật điểm dừng.",
        );

        return null;
      } finally {
        setSaving(false);
      }
    },
    [scheduleId],
  );

  /**
   * =====================================================
   * DELETE
   * =====================================================
   */

  const deleteScheduleStop = useCallback(
    async (stopId) => {
      setSaving(true);
      setError("");
      setSuccess("");

      try {
        await scheduleStopService.deleteScheduleStop(scheduleId, stopId);

        setScheduleStops((prev) => prev.filter((stop) => stop.id !== stopId));

        setSuccess("Xóa điểm dừng thành công.");

        return true;
      } catch (err) {
        console.error("DELETE SCHEDULE STOP ERROR:", err);

        setError(err.response?.data?.message || "Không thể xóa điểm dừng.");

        return false;
      } finally {
        setSaving(false);
      }
    },
    [scheduleId],
  );

  return {
    scheduleStops,

    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadScheduleStops,

    createScheduleStop,
    updateScheduleStop,
    deleteScheduleStop,
  };
}
