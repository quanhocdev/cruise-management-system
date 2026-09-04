// src/modules/shore/services/visitTourService.js

import api from "../../../api/axios";

const BASE_URL = "/shore/visit-tours";

const visitTourService = {
  /**
   * GET /api/shore/visit-tours
   *
   * Lấy tất cả Visit Tour.
   */
  getAll: async () => {
    const response = await api.get(BASE_URL);
    return response.data;
  },

  /**
   * GET /api/shore/visit-tours/{id}
   *
   * Lấy chi tiết một Visit Tour.
   */
  getById: async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data;
  },

  /**
   * GET /api/shore/visit-tours/schedule-stop/{scheduleStopId}
   *
   * Lấy Visit Tour theo Schedule Stop.
   */
  getByScheduleStop: async (scheduleStopId) => {
    const response = await api.get(
      `${BASE_URL}/schedule-stop/${scheduleStopId}`,
    );

    return response.data;
  },

  /**
   * GET /api/shore/visit-tours/tour/{tourId}
   *
   * Lấy Visit Tour theo Tour.
   */
  getByTour: async (tourId) => {
    const response = await api.get(`${BASE_URL}/tour/${tourId}`);

    return response.data;
  },

  /**
   * POST /api/shore/visit-tours/schedule-stops/{scheduleStopId}/visit-tours
   *
   * Tạo Visit Tour mới cho một Schedule Stop.
   */
  create: async (scheduleStopId, payload) => {
    const response = await api.post(
      `${BASE_URL}/schedule-stops/${scheduleStopId}/visit-tours`,
      payload,
      {
        headers: {
          "Content-Type": "application/json",
        },
      },
    );

    return response.data;
  },

  /**
   * PATCH /api/shore/visit-tours/{id}
   *
   * Cập nhật Visit Tour.
   */
  update: async (id, payload) => {
    const response = await api.patch(`${BASE_URL}/${id}`, payload, {
      headers: {
        "Content-Type": "application/json",
      },
    });

    return response.data;
  },

  /**
   * DELETE /api/shore/visit-tours/{id}
   *
   * Xóa Visit Tour.
   */
  delete: async (id) => {
    const response = await api.delete(`${BASE_URL}/${id}`);

    return response.data;
  },

  /**
   * GET /api/shore/visit-tour-configurations/configuration-history
   *
   * Lấy lịch sử các Tour đã hoàn thành cấu hình Visit Tour.
   */
  getConfigurationHistory: async () => {
    const response = await api.get(
      "/shore/visit-tour-configurations/configuration-history",
    );

    return response.data;
  },

  /**
   * GET /api/shore/visit-tour-configurations/configuration-history/{tourId}
   *
   * Lấy chi tiết cấu hình của một Tour trong lịch sử.
   */
  getConfigurationHistoryDetail: async (tourId) => {
    const response = await api.get(
      `/shore/visit-tour-configurations/configuration-history/${tourId}`,
    );

    return response.data;
  },

  /**
   * POST /api/shore/visit-tour-configurations/{tourId}/complete
   *
   * Hoàn thành cấu hình Visit Tour của một Tour.
   */
  completeTourConfiguration: async (tourId) => {
    const response = await api.post(
      `/shore/visit-tour-configurations/${tourId}/complete`,
    );

    return response.data;
  },
};

export default visitTourService;
