// src/modules/operation/services/serviceTourAssignmentService.js

import api from "../../../api/axios";

const BASE_URL = "/operation/service-tour-assignment";
const CONFIGURED_BASE_URL = "/operation/service-tours";

const serviceTourAssignmentService = {
  /**
   * =========================================================
   * API PHÂN CÔNG CŨ
   * =========================================================
   */

  /**
   * GET /api/operation/service-tour-assignment/tour/{tourId}
   */
  getByTour: async (tourId) => {
    const response = await api.get(`${BASE_URL}/tour/${tourId}`);

    return response.data;
  },

  /**
   * POST /api/operation/service-tour-assignment
   */
  assign: async (payload) => {
    const response = await api.post(BASE_URL, payload, {
      headers: {
        "Content-Type": "application/json",
      },
    });

    return response.data;
  },

  /**
   * DELETE
   * /api/operation/service-tour-assignment/tour/{tourId}/area/{cruiseAreaId}
   */
  delete: async (tourId, cruiseAreaId) => {
    const response = await api.delete(
      `${BASE_URL}/tour/${tourId}/area/${cruiseAreaId}`,
    );

    return response.data;
  },

  /**
   * =========================================================
   * API LẤY SERVICE TOUR ĐÃ CẤU HÌNH
   * =========================================================
   */

  /**
   * GET /api/operation/service-tours
   *
   * Lấy tất cả ServiceTour đã được service-service
   * cấu hình và gửi sang tour-service.
   */
  getAllConfigured: async () => {
    const response = await api.get(CONFIGURED_BASE_URL);

    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/operation/service-tours/tour/{tourId}
   *
   * Lấy toàn bộ ServiceTour đã cấu hình của một Tour.
   */
  getConfiguredByTour: async (tourId) => {
    const response = await api.get(`${CONFIGURED_BASE_URL}/tour/${tourId}`);

    return response.data?.data ?? response.data;
  },
};

export default serviceTourAssignmentService;
