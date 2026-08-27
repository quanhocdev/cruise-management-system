// src/modules/operation/services/productTourAssignmentService.js

import api from "../../../api/axios";

const ASSIGNMENT_BASE_URL = "/operation/product-tour-assignment";
const CONFIGURED_BASE_URL = "/operation/product-tours";

const productTourAssignmentService = {
  // =========================================================
  // CONFIGURED PRODUCT TOURS
  // =========================================================

  /**
   * GET /api/operation/product-tours
   *
   * Lấy tất cả ProductTour đã được tour-service nhận
   * từ product-service qua Kafka.
   */
  getAllConfigured: async () => {
    const response = await api.get(CONFIGURED_BASE_URL);

    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/operation/product-tours/tour/{tourId}
   *
   * Lấy các ProductTour đã cấu hình của một Tour.
   */
  getConfiguredByTour: async (tourId) => {
    const response = await api.get(`${CONFIGURED_BASE_URL}/tour/${tourId}`);

    return response.data?.data ?? response.data;
  },

  // =========================================================
  // PRODUCT ASSIGNMENT
  // =========================================================

  /**
   * GET /api/operation/product-tour-assignment/tour/{tourId}
   *
   * Lấy danh sách khu vực đã phân công Product cho Tour.
   */
  getByTour: async (tourId) => {
    const response = await api.get(`${ASSIGNMENT_BASE_URL}/tour/${tourId}`);

    return response.data?.data ?? response.data;
  },

  /**
   * POST /api/operation/product-tour-assignment
   *
   * Phân công Product cho Tour.
   */
  assign: async (payload) => {
    const response = await api.post(ASSIGNMENT_BASE_URL, payload, {
      headers: {
        "Content-Type": "application/json",
      },
    });

    return response.data;
  },

  /**
   * DELETE /api/operation/product-tour-assignment/tour/{tourId}/area/{cruiseAreaId}
   *
   * Hủy phân công Product.
   */
  delete: async (tourId, cruiseAreaId) => {
    const response = await api.delete(
      `${ASSIGNMENT_BASE_URL}/tour/${tourId}/area/${cruiseAreaId}`,
    );

    return response.data;
  },
};

export default productTourAssignmentService;
