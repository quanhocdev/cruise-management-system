// src/modules/passenger/services/bookingService.js
import api from "../../../api/axios";

const BASE_URL = "/passengers/bookings";

const bookingService = {
  /**
   * GET /api/passengers/bookings
   */
  getMine: async () => {
    const response = await api.get(BASE_URL);
    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/passengers/bookings/{id}
   */
  getById: async (id, privileged = false) => {
    const response = await api.get(`${BASE_URL}/${id}`, {
      params: { privileged },
    });
    return response.data?.data ?? response.data;
  },

  /**
   * POST /api/passengers/bookings
   */
  create: async (data) => {
    // Không còn truyền userId qua params nữa, backend tự bóc từ token
    const response = await api.post(BASE_URL, data);
    return response.data?.data ?? response.data;
  },

  /**
   * PATCH /api/passengers/bookings/{id}/cancel
   */
  cancel: async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/cancel`);
    return response.data?.data ?? response.data;
  },
};

export default bookingService;
