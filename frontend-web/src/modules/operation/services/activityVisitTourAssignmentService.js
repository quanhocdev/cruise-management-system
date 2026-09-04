// src/modules/operation/services/activityVisitTourAssignmentService.js

import api from "../../../api/axios";

const BASE_URL = "/operation/activity-visit-tour-assignment";
const CONFIGURED_BASE_URL = "/operation/activity-visit-tours";

const activityVisitTourAssignmentService = {
  /**
   * =========================================================
   * API PHÂN CÔNG
   * =========================================================
   */

  /**
   * GET /api/operation/activity-visit-tour-assignment/tour/{tourId}
   *
   * Lấy danh sách các khu vực đã phân công Activity Visit
   * cho một Tour.
   */
  getByTour: async (tourId) => {
    const response = await api.get(`${BASE_URL}/tour/${tourId}`);

    return response.data;
  },

  /**
   * POST /api/operation/activity-visit-tour-assignment
   *
   * Phân công khu vực tham quan cho Tour.
   *
   * payload = {
   *   tourId,
   *   cruiseAreaId
   * }
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
   * /api/operation/activity-visit-tour-assignment/tour/{tourId}/area/{cruiseAreaId}
   *
   * Xóa phân công khu vực Activity Visit.
   */
  delete: async (tourId, cruiseAreaId) => {
    const response = await api.delete(
      `${BASE_URL}/tour/${tourId}/area/${cruiseAreaId}`,
    );

    return response.data;
  },

  /**
   * =========================================================
   * API NHẬN CẤU HÌNH ĐÃ HOÀN THÀNH
   * =========================================================
   */

  /**
   * GET /api/operation/activity-visit-tours
   *
   * Lấy tất cả ActivityVisitTour đã được cấu hình.
   */
  getAllConfigured: async () => {
    const response = await api.get(CONFIGURED_BASE_URL);

    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/operation/activity-visit-tours/tour/{tourId}
   *
   * Lấy các ActivityVisitTour đã cấu hình của một Tour.
   */
  getConfiguredByTour: async (tourId) => {
    const response = await api.get(`${CONFIGURED_BASE_URL}/tour/${tourId}`);

    return response.data?.data ?? response.data;
  },
};

export default activityVisitTourAssignmentService;
