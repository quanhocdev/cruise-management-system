// src/modules/operation/hooks/useActivityCruiseTourAssignments.js

import { useCallback, useState } from "react";
import activityCruiseTourAssignmentService from "../services/activityCruiseTourAssignmentService";

export default function useActivityCruiseTourAssignments() {
  const [activityAssignments, setActivityAssignments] = useState([]);
  const [configuredActivities, setConfiguredActivities] = useState([]);

  const [loading, setLoading] = useState(false);
  const [configuredLoading, setConfiguredLoading] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =========================================================
  // PHÂN CÔNG
  // =========================================================

  /**
   * GET /api/operation/activity-cruise-tour-assignment/tour/{tourId}
   *
   * Lấy các khu vực Activity Cruise đã được phân công.
   */
  const loadActivityAssignments = useCallback(async (tourId) => {
    if (!tourId) {
      setActivityAssignments([]);
      return [];
    }

    setLoading(true);
    setError("");

    try {
      const data = await activityCruiseTourAssignmentService.getByTour(tourId);

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setActivityAssignments(list);

      return list;
    } catch (err) {
      console.error("LOAD ACTIVITY CRUISE ASSIGNMENTS ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách phân công hoạt động trên tàu.",
      );

      return [];
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * POST /api/operation/activity-cruise-tour-assignment
   *
   * Phân công khu vực Activity Cruise.
   */
  const assignActivityArea = useCallback(
    async (tourIdOrPayload, cruiseAreaId) => {
      setLoading(true);
      setError("");
      setSuccess("");

      try {
        let payload = {};

        if (typeof tourIdOrPayload === "object" && tourIdOrPayload !== null) {
          payload = tourIdOrPayload;
        } else {
          payload = {
            tourId: tourIdOrPayload,
            cruiseAreaId,
          };
        }

        const result =
          await activityCruiseTourAssignmentService.assign(payload);

        setActivityAssignments((prev) => {
          const exists = prev.some(
            (item) => String(item.id) === String(result.id),
          );

          return exists ? prev : [...prev, result];
        });

        setSuccess("Lưu phân công khu vực hoạt động trên tàu thành công.");

        return result;
      } catch (err) {
        console.error("ASSIGN ACTIVITY CRUISE AREA ERROR:", err);

        setError(
          err.response?.data?.message ||
            "Không thể phân công khu vực hoạt động trên tàu.",
        );

        throw err;
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  /**
   * DELETE /api/operation/activity-cruise-tour-assignment/...
   *
   * Xóa phân công Activity Cruise.
   */
  const deleteActivityAssignment = useCallback(async (tourId, cruiseAreaId) => {
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      await activityCruiseTourAssignmentService.delete(tourId, cruiseAreaId);

      setActivityAssignments((prev) =>
        prev.filter((item) => {
          const targetId =
            item.cruiseAreaId ||
            item.areaId ||
            item.cruiseArea?.id ||
            item.area?.id;

          return String(targetId) !== String(cruiseAreaId);
        }),
      );

      setSuccess("Đã hủy phân công khu vực hoạt động trên tàu.");
    } catch (err) {
      console.error("DELETE ACTIVITY CRUISE ASSIGNMENT ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể xóa phân công hoạt động trên tàu.",
      );

      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // =========================================================
  // CẤU HÌNH ĐÃ HOÀN THÀNH
  // =========================================================

  /**
   * GET /api/operation/activity-cruise-tours
   *
   * Lấy TẤT CẢ ActivityCruiseTour đã nhận cấu hình từ
   * activity-cruise-service qua Kafka.
   */
  const loadConfiguredActivities = useCallback(async () => {
    setConfiguredLoading(true);
    setError("");

    try {
      const data = await activityCruiseTourAssignmentService.getAllConfigured();

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setConfiguredActivities(list);

      return list;
    } catch (err) {
      console.error("LOAD CONFIGURED ACTIVITY CRUISE ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải cấu hình hoạt động trên tàu.",
      );

      return [];
    } finally {
      setConfiguredLoading(false);
    }
  }, []);

  /**
   * GET /api/operation/activity-cruise-tours/tour/{tourId}
   *
   * Lấy cấu hình Activity Cruise của một Tour.
   */
  const loadConfiguredActivitiesByTour = useCallback(async (tourId) => {
    if (!tourId) {
      return [];
    }

    setConfiguredLoading(true);
    setError("");

    try {
      const data =
        await activityCruiseTourAssignmentService.getConfiguredByTour(tourId);

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setConfiguredActivities(list);

      return list;
    } catch (err) {
      console.error("LOAD CONFIGURED ACTIVITY CRUISE BY TOUR ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải cấu hình hoạt động trên tàu của Tour.",
      );

      return [];
    } finally {
      setConfiguredLoading(false);
    }
  }, []);

  // =========================================================
  // CLEAR
  // =========================================================

  const clearActivityAssignments = useCallback(() => {
    setActivityAssignments([]);
  }, []);

  const clearConfiguredActivities = useCallback(() => {
    setConfiguredActivities([]);
  }, []);

  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  // =========================================================
  // RETURN
  // =========================================================

  return {
    // Phân công
    activityAssignments,
    activityLoading: loading,
    activityError: error,
    activitySuccess: success,

    loadActivityAssignments,
    assignActivityArea,
    deleteActivityAssignment,

    // Cấu hình đã hoàn thành
    configuredActivities,
    configuredLoading,

    loadConfiguredActivities,
    loadConfiguredActivitiesByTour,

    // Clear
    clearActivityAssignments,
    clearConfiguredActivities,
    clearMessages,
  };
}
