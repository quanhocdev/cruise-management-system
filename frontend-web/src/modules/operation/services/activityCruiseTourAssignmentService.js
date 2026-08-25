// src/modules/operation/services/activityCruiseTourAssignmentService.js

import api from "../../../api/axios";

const BASE_URL = "/operation/activity-cruise-tour-assignment";
const CONFIGURED_BASE_URL = "/operation/activity-cruise-tours";

const activityCruiseTourAssignmentService = {
  /**
   * =========================================================
   * API PHÂN CÔNG CŨ
   * =========================================================
   */

  /**
   * GET /api/operation/activity-cruise-tour-assignment/tour/{tourId}
   *
   * Lấy danh sách các khu vực đã phân công hoạt động cho Tour.
   */
  getByTour: async (tourId) => {
    const response = await api.get(`${BASE_URL}/tour/${tourId}`);

    return response.data;
  },

  /**
   * POST /api/operation/activity-cruise-tour-assignment
   *
   * Phân công 1 khu vực Hoạt động cho Tour.
   * payload = { tourId, cruiseAreaId }
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
   * /api/operation/activity-cruise-tour-assignment/tour/{tourId}/area/{cruiseAreaId}
   *
   * Xóa phân công khu vực hoạt động.
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
   * GET /api/operation/activity-cruise-tours
   *
   * Lấy tất cả ActivityCruiseTour đã được cấu hình.
   */
  getAllConfigured: async () => {
    const response = await api.get(CONFIGURED_BASE_URL);

    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/operation/activity-cruise-tours/tour/{tourId}
   *
   * Lấy các ActivityCruiseTour đã cấu hình của một Tour.
   */
  getConfiguredByTour: async (tourId) => {
    const response = await api.get(`${CONFIGURED_BASE_URL}/tour/${tourId}`);

    return response.data?.data ?? response.data;
  },
};

export default activityCruiseTourAssignmentService;
