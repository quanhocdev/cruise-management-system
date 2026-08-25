// src/modules/operation/hooks/useActivityVisitTourAssignments.js

import { useCallback, useState } from "react";
import activityVisitTourAssignmentService from "../services/activityVisitTourAssignmentService";

export default function useActivityVisitTourAssignments() {
  const [activityVisitAssignments, setActivityVisitAssignments] = useState([]);

  const [configuredActivityVisits, setConfiguredActivityVisits] = useState([]);

  const [loading, setLoading] = useState(false);
  const [configuredLoading, setConfiguredLoading] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadActivityVisitAssignments = useCallback(async (tourId) => {
    if (!tourId) {
      setActivityVisitAssignments([]);
      return [];
    }

    setLoading(true);
    setError("");

    try {
      const data = await activityVisitTourAssignmentService.getByTour(tourId);

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setActivityVisitAssignments(list);

      return list;
    } catch (err) {
      console.error("LOAD ACTIVITY VISIT ASSIGNMENTS ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách phân công hoạt động trên bờ.",
      );

      return [];
    } finally {
      setLoading(false);
    }
  }, []);

  // =========================================================
  // PHÂN CÔNG
  // =========================================================

  /**
   * Phân công Activity Visit cho Tour.
   *
   * Giữ nguyên nếu frontend hiện tại đang sử dụng API này.
   */
  const assignActivityVisit = useCallback(async (payload) => {
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const result = await activityVisitTourAssignmentService.assign(payload);

      setActivityVisitAssignments((prev) => {
        const exists = prev.some(
          (item) => String(item.id) === String(result.id),
        );

        return exists ? prev : [...prev, result];
      });

      setSuccess("Phân công hoạt động trên bờ thành công.");

      return result;
    } catch (err) {
      console.error("ASSIGN ACTIVITY VISIT ERROR:", err);

      setError(
        err.response?.data?.message || "Không thể phân công hoạt động trên bờ.",
      );

      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // =========================================================
  // XÓA PHÂN CÔNG
  // =========================================================

  const deleteActivityVisitAssignment = useCallback(
    async (tourId, scheduleStopId) => {
      setLoading(true);
      setError("");
      setSuccess("");

      try {
        await activityVisitTourAssignmentService.delete(tourId, scheduleStopId);

        setActivityVisitAssignments((prev) =>
          prev.filter((item) => {
            const targetId =
              item.scheduleStopId || item.stopId || item.scheduleStop?.id;

            return String(targetId) !== String(scheduleStopId);
          }),
        );

        setSuccess("Đã hủy phân công hoạt động trên bờ.");
      } catch (err) {
        console.error("DELETE ACTIVITY VISIT ASSIGNMENT ERROR:", err);

        setError(
          err.response?.data?.message ||
            "Không thể xóa phân công hoạt động trên bờ.",
        );

        throw err;
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  const loadConfiguredActivityVisits = useCallback(async () => {
    setConfiguredLoading(true);
    setError("");

    try {
      const data = await activityVisitTourAssignmentService.getAllConfigured();

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setConfiguredActivityVisits(list);

      return list;
    } catch (err) {
      console.error("LOAD CONFIGURED ACTIVITY VISITS ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải cấu hình hoạt động trên bờ.",
      );

      return [];
    } finally {
      setConfiguredLoading(false);
    }
  }, []);

  const loadConfiguredActivityVisitsByTour = useCallback(async (tourId) => {
    if (!tourId) {
      setConfiguredActivityVisits([]);
      return [];
    }

    setConfiguredLoading(true);
    setError("");

    try {
      const data =
        await activityVisitTourAssignmentService.getConfiguredByTour(tourId);

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setConfiguredActivityVisits(list);

      return list;
    } catch (err) {
      console.error("LOAD CONFIGURED ACTIVITY VISITS BY TOUR ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải cấu hình hoạt động trên bờ của Tour.",
      );

      return [];
    } finally {
      setConfiguredLoading(false);
    }
  }, []);

  const clearActivityVisitAssignments = useCallback(() => {
    setActivityVisitAssignments([]);
  }, []);

  const clearConfiguredActivityVisits = useCallback(() => {
    setConfiguredActivityVisits([]);
  }, []);

  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  return {
    activityVisitAssignments,

    activityVisitLoading: loading,
    activityVisitError: error,
    activityVisitSuccess: success,

    loadActivityVisitAssignments,
    assignActivityVisit,
    deleteActivityVisitAssignment,

    configuredActivityVisits,
    configuredActivityVisitLoading: configuredLoading,

    loadConfiguredActivityVisits,
    loadConfiguredActivityVisitsByTour,

    clearActivityVisitAssignments,
    clearConfiguredActivityVisits,
    clearMessages,
  };
}
