// src/modules/operation/hooks/useActivityTourAssignments.js
import { useCallback, useState } from "react";
import activityTourAssignmentService from "../services/activityTourAssignmentService";

export default function useActivityTourAssignments() {
  const [activityAssignments, setActivityAssignments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  /**
   * Tải danh sách phân công Hoạt động theo tourId
   */
  const loadActivityAssignments = useCallback(async (tourId) => {
    setLoading(true);
    setError("");

    try {
      const data = await activityTourAssignmentService.getByTour(tourId);
      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setActivityAssignments(list);
      return list;
    } catch (err) {
      console.error("LOAD ACTIVITY ASSIGNMENTS ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách phân công hoạt động.",
      );
      return [];
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Phân công khu vực cho Hoạt động
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
            cruiseAreaId: cruiseAreaId,
          };
        }

        const result = await activityTourAssignmentService.assign(payload);

        setActivityAssignments((prev) => {
          const exists = prev.some((item) => item.id === result.id);
          return exists ? prev : [...prev, result];
        });

        setSuccess("Lưu phân công khu vực hoạt động thành công.");
        return result;
      } catch (err) {
        console.error("ASSIGN ACTIVITY AREA ERROR:", err);
        setError(
          err.response?.data?.message ||
            "Không thể phân công khu vực hoạt động.",
        );
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  /**
   * Xóa phân công khu vực hoạt động theo tourId và cruiseAreaId
   */
  const deleteActivityAssignment = useCallback(async (tourId, cruiseAreaId) => {
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      await activityTourAssignmentService.delete(tourId, cruiseAreaId);

      // Cập nhật State linh hoạt: hỗ trợ lọc cả trường hợp cruiseAreaId, areaId hoặc nested object
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

      setSuccess("Đã hủy phân công khu vực hoạt động.");
    } catch (err) {
      console.error("DELETE ACTIVITY ASSIGNMENT ERROR:", err);
      setError(
        err.response?.data?.message || "Không thể xóa phân công hoạt động.",
      );
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const clearActivityAssignments = useCallback(() => {
    setActivityAssignments([]);
  }, []);

  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  return {
    activityAssignments,
    activityLoading: loading,
    activityError: error,
    activitySuccess: success,
    loadActivityAssignments,
    assignActivityArea,
    deleteActivityAssignment,
    clearActivityAssignments,
    clearMessages,
  };
}
