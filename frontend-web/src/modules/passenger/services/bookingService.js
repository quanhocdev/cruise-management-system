// src/modules/passenger/services/bookingService.js
import api from "../../../api/axios";

const BASE_URL = "/passengers/bookings";

const bookingService = {
  /**
   * GET /api/passengers/bookings?userId=...
   */
  getMine: async (userId) => {
    const response = await api.get(BASE_URL, {
      params: { userId },
    });
    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/passengers/bookings/{id}?userId=...&privileged=...
   */
  getById: async (id, userId, privileged = false) => {
    const response = await api.get(`${BASE_URL}/${id}`, {
      params: { userId, privileged },
    });
    return response.data?.data ?? response.data;
  },

  /**
   * POST /api/passengers/bookings?userId=...
   */
  create: async (data, userId) => {
    const response = await api.post(BASE_URL, data, {
      params: { userId },
      headers: { "Content-Type": "application/json" },
    });
    return response.data?.data ?? response.data;
  },

  /**
   * PATCH /api/passengers/bookings/{id}/cancel?userId=...
   */
  cancel: async (id, userId) => {
    const response = await api.patch(`${BASE_URL}/${id}/cancel`, null, {
      params: { userId },
    });
    return response.data?.data ?? response.data;
  },
};

export default bookingService;
