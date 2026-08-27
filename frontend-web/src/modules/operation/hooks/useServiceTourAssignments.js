// src/modules/operation/hooks/useServiceTourAssignments.js

import { useCallback, useState } from "react";
import serviceTourAssignmentService from "../services/serviceTourAssignmentService";

export default function useServiceTourAssignments() {
  // =========================================================
  // PHÂN CÔNG
  // =========================================================

  const [serviceAssignments, setServiceAssignments] = useState([]);

  // =========================================================
  // CẤU HÌNH ĐÃ HOÀN THÀNH
  // =========================================================

  const [configuredServices, setConfiguredServices] = useState([]);

  // =========================================================
  // STATE
  // =========================================================

  const [loading, setLoading] = useState(false);
  const [configuredLoading, setConfiguredLoading] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =========================================================
  // LOAD PHÂN CÔNG
  // =========================================================

  /**
   * GET /api/operation/service-tour-assignment/tour/{tourId}
   *
   * Lấy danh sách khu vực đã phân công Service cho Tour.
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

  // =========================================================
  // PHÂN CÔNG SERVICE
  // =========================================================

  const assignServiceArea = useCallback(
    async (tourIdOrPayload, cruiseAreaId) => {
      setLoading(true);
      setError("");
      setSuccess("");

      try {
        let payload = {};

        /*
         * Hỗ trợ:
         *
         * assignServiceArea({
         *   tourId,
         *   cruiseAreaId
         * })
         *
         * hoặc:
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

  // =========================================================
  // XÓA PHÂN CÔNG SERVICE
  // =========================================================

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

  // =========================================================
  // LOAD SERVICE ĐÃ CẤU HÌNH
  // =========================================================

  /**
   * GET /api/operation/service-tours
   *
   * Lấy TẤT CẢ ServiceTour mà tour-service
   * đã nhận được từ convenience-service qua Kafka.
   */
  const loadConfiguredServices = useCallback(async () => {
    setConfiguredLoading(true);
    setError("");

    try {
      const data = await serviceTourAssignmentService.getAllConfigured();

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setConfiguredServices(list);

      return list;
    } catch (err) {
      console.error("LOAD CONFIGURED SERVICES ERROR:", err);

      setError(
        err.response?.data?.message || "Không thể tải cấu hình dịch vụ.",
      );

      return [];
    } finally {
      setConfiguredLoading(false);
    }
  }, []);

  /**
   * GET /api/operation/service-tours/tour/{tourId}
   *
   * Lấy ServiceTour đã cấu hình của một Tour.
   */
  const loadConfiguredServicesByTour = useCallback(async (tourId) => {
    if (!tourId) {
      return [];
    }

    setConfiguredLoading(true);
    setError("");

    try {
      const data =
        await serviceTourAssignmentService.getConfiguredByTour(tourId);

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setConfiguredServices(list);

      return list;
    } catch (err) {
      console.error("LOAD CONFIGURED SERVICES BY TOUR ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải cấu hình dịch vụ của Tour.",
      );

      return [];
    } finally {
      setConfiguredLoading(false);
    }
  }, []);

  // =========================================================
  // CLEAR
  // =========================================================

  const clearServiceAssignments = useCallback(() => {
    setServiceAssignments([]);
  }, []);

  const clearConfiguredServices = useCallback(() => {
    setConfiguredServices([]);
  }, []);

  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  // =========================================================
  // RETURN
  // =========================================================

  return {
    // -------------------------------------------------------
    // Phân công
    // -------------------------------------------------------

    serviceAssignments,

    serviceLoading: loading,
    serviceError: error,
    serviceSuccess: success,

    loadServiceAssignments,
    assignServiceArea,
    deleteServiceAssignment,

    // -------------------------------------------------------
    // Cấu hình đã hoàn thành
    // -------------------------------------------------------

    configuredServices,
    configuredLoading,

    loadConfiguredServices,
    loadConfiguredServicesByTour,

    // -------------------------------------------------------
    // Clear
    // -------------------------------------------------------

    clearServiceAssignments,
    clearConfiguredServices,
    clearMessages,
  };
}
