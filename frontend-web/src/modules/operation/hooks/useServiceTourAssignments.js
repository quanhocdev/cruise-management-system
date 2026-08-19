// src/modules/operation/hooks/useServiceTourAssignments.js
import { useCallback, useState } from "react";
import serviceTourAssignmentService from "../services/serviceTourAssignmentService";

export default function useServiceTourAssignments() {
  const [serviceAssignments, setServiceAssignments] = useState([]);

  const [loading, setLoading] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  /**
   * Tải danh sách phân công Service theo tourId
   */
  const loadServiceAssignments = useCallback(async (tourId) => {
    if (!tourId) {
      setServiceAssignments([]);
      return [];
    }

    setLoading(true);
    setError("");

    try {
      const data = await serviceTourAssignmentService.getByTour(tourId);

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setServiceAssignments(list);

      return list;
    } catch (err) {
      console.error("LOAD SERVICE ASSIGNMENTS ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách phân công dịch vụ.",
      );

      return [];
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Operation phân công khu vực cho Service
   */
  const assignServiceArea = useCallback(
    async (tourIdOrPayload, cruiseAreaId) => {
      setLoading(true);
      setError("");
      setSuccess("");

      try {
        let payload = {};

        /*
         * Hỗ trợ cả:
         *
         * assignServiceArea({
         *   tourId,
         *   cruiseAreaId
         * })
         *
         * và:
         *
         * assignServiceArea(tourId, cruiseAreaId)
         */
        if (typeof tourIdOrPayload === "object" && tourIdOrPayload !== null) {
          payload = tourIdOrPayload;
        } else {
          payload = {
            tourId: tourIdOrPayload,
            cruiseAreaId,
          };
        }

        const result = await serviceTourAssignmentService.assign(payload);

        setServiceAssignments((prev) => {
          const exists = prev.some(
            (item) => String(item.id) === String(result.id),
          );

          return exists ? prev : [...prev, result];
        });

        setSuccess("Lưu phân công khu vực dịch vụ thành công.");

        return result;
      } catch (err) {
        console.error("ASSIGN SERVICE AREA ERROR:", err);

        setError(
          err.response?.data?.message || "Không thể phân công khu vực dịch vụ.",
        );

        throw err;
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  /**
   * Xóa phân công Service
   */
  const deleteServiceAssignment = useCallback(async (tourId, cruiseAreaId) => {
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      await serviceTourAssignmentService.delete(tourId, cruiseAreaId);

      setServiceAssignments((prev) =>
        prev.filter((item) => {
          const targetId =
            item.cruiseAreaId ||
            item.areaId ||
            item.cruiseArea?.id ||
            item.area?.id;

          return String(targetId) !== String(cruiseAreaId);
        }),
      );

      setSuccess("Đã hủy phân công dịch vụ.");
    } catch (err) {
      console.error("DELETE SERVICE ASSIGNMENT ERROR:", err);

      setError(
        err.response?.data?.message || "Không thể xóa phân công dịch vụ.",
      );

      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Xóa toàn bộ state phân công
   */
  const clearServiceAssignments = useCallback(() => {
    setServiceAssignments([]);
  }, []);

  /**
   * Xóa message
   */
  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  return {
    serviceAssignments,

    serviceLoading: loading,
    serviceError: error,
    serviceSuccess: success,

    loadServiceAssignments,
    assignServiceArea,
    deleteServiceAssignment,

    clearServiceAssignments,
    clearMessages,
  };
}
