// src/modules/operation/hooks/useProductTourAssignments.js

import { useCallback, useState } from "react";
import productTourAssignmentService from "../services/productTourAssignmentService";

export default function useProductTourAssignments() {
  // =========================================================
  // PHÂN CÔNG
  // =========================================================

  const [productAssignments, setProductAssignments] = useState([]);

  // =========================================================
  // CẤU HÌNH ĐÃ HOÀN THÀNH
  // =========================================================

  const [configuredProducts, setConfiguredProducts] = useState([]);

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
   * GET /api/operation/product-tour-assignment/tour/{tourId}
   *
   * Lấy danh sách khu vực đã phân công Product cho Tour.
   */
  const loadProductAssignments = useCallback(async (tourId) => {
    if (!tourId) {
      setProductAssignments([]);
      return [];
    }

    setLoading(true);
    setError("");

    try {
      const data = await productTourAssignmentService.getByTour(tourId);

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setProductAssignments(list);

      return list;
    } catch (err) {
      console.error("LOAD PRODUCT ASSIGNMENTS ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách phân công tiện ích.",
      );

      return [];
    } finally {
      setLoading(false);
    }
  }, []);

  // =========================================================
  // PHÂN CÔNG PRODUCT
  // =========================================================

  const assignProduct = useCallback(async (payload) => {
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const result = await productTourAssignmentService.assign(payload);

      setProductAssignments((prev) => {
        const exists = prev.some(
          (item) => String(item.id) === String(result.id),
        );

        return exists ? prev : [...prev, result];
      });

      setSuccess("Phân công tiện ích thành công.");

      return result;
    } catch (err) {
      console.error("ASSIGN PRODUCT ERROR:", err);

      setError(err.response?.data?.message || "Không thể phân công tiện ích.");

      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // =========================================================
  // XÓA PHÂN CÔNG
  // =========================================================

  /**
   * Xóa phân công tiện ích theo tourId + cruiseAreaId.
   */
  const deleteProductAssignment = useCallback(async (tourId, cruiseAreaId) => {
    setLoading(true);
    setError("");
    setSuccess("");

    try {
      await productTourAssignmentService.delete(tourId, cruiseAreaId);

      setProductAssignments((prev) =>
        prev.filter((item) => {
          const targetAreaId =
            item.cruiseAreaId ||
            item.areaId ||
            item.cruiseArea?.id ||
            item.area?.id;

          return String(targetAreaId) !== String(cruiseAreaId);
        }),
      );

      setSuccess("Đã xóa phân công tiện ích.");
    } catch (err) {
      console.error("DELETE PRODUCT ASSIGNMENT ERROR:", err);

      setError(
        err.response?.data?.message || "Không thể xóa phân công tiện ích.",
      );

      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // =========================================================
  // LOAD CẤU HÌNH ĐÃ HOÀN THÀNH
  // =========================================================

  /**
   * GET /api/operation/product-tours
   *
   * Lấy TẤT CẢ ProductTour đã được tour-service nhận
   * từ product-service qua Kafka.
   */
  const loadConfiguredProducts = useCallback(async () => {
    setConfiguredLoading(true);
    setError("");

    try {
      const data = await productTourAssignmentService.getAllConfigured();

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setConfiguredProducts(list);

      return list;
    } catch (err) {
      console.error("LOAD CONFIGURED PRODUCTS ERROR:", err);

      setError(
        err.response?.data?.message || "Không thể tải cấu hình tiện ích.",
      );

      return [];
    } finally {
      setConfiguredLoading(false);
    }
  }, []);

  /**
   * GET /api/operation/product-tours/tour/{tourId}
   *
   * Lấy các ProductTour đã cấu hình của một Tour.
   */
  const loadConfiguredProductsByTour = useCallback(async (tourId) => {
    if (!tourId) {
      return [];
    }

    setConfiguredLoading(true);
    setError("");

    try {
      const data =
        await productTourAssignmentService.getConfiguredByTour(tourId);

      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      setConfiguredProducts(list);

      return list;
    } catch (err) {
      console.error("LOAD CONFIGURED PRODUCTS BY TOUR ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải cấu hình tiện ích của Tour.",
      );

      return [];
    } finally {
      setConfiguredLoading(false);
    }
  }, []);

  // =========================================================
  // CLEAR
  // =========================================================

  const clearProductAssignments = useCallback(() => {
    setProductAssignments([]);
  }, []);

  const clearConfiguredProducts = useCallback(() => {
    setConfiguredProducts([]);
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

    productAssignments,
    productLoading: loading,
    productError: error,
    productSuccess: success,

    loadProductAssignments,
    assignProduct,
    deleteProductAssignment,

    // -------------------------------------------------------
    // Cấu hình đã hoàn thành
    // -------------------------------------------------------

    configuredProducts,
    configuredLoading,

    loadConfiguredProducts,
    loadConfiguredProductsByTour,

    // -------------------------------------------------------
    // Clear
    // -------------------------------------------------------

    clearProductAssignments,
    clearConfiguredProducts,
    clearMessages,
  };
}
