// src/modules/operation/services/productTourAssignmentService.js
import api from "../../../api/axios";

const ASSIGNMENT_BASE_URL = "/operation/product-tour-assignment";
const CONFIGURED_BASE_URL = "/operation/product-tours";

const productTourAssignmentService = {
  /**
   * =========================================================
   * GET CONFIGURED PRODUCT TOURS
   * =========================================================
   *
   * GET /api/operation/product-tours
   * Lấy tất cả ProductTour mà tour-service đã nhận từ Kafka.
   */
  getAll: async () => {
    const response = await api.get(CONFIGURED_BASE_URL);
    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/operation/product-tours/tour/{tourId}
   * Lấy ProductTour đã cấu hình của một Tour.
   */
  getByTour: async (tourId) => {
    const response = await api.get(`${CONFIGURED_BASE_URL}/tour/${tourId}`);

    return response.data?.data ?? response.data;
  },

  /**
   * =========================================================
   * ASSIGNMENT
   * =========================================================
   *
   * POST /api/operation/product-tour-assignment
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
