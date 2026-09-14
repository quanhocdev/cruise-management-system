// src/modules/passenger/services/bookingService.js
import api from "../../../api/axios";

const BASE_URL = "/passengers/bookings";

const bookingService = {
  getMine: async () => {
    const response = await api.get(BASE_URL);
    return response.data?.data ?? response.data;
  },

  getById: async (id, privileged = false) => {
    const response = await api.get(`${BASE_URL}/${id}`, {
      params: { privileged },
    });
    return response.data?.data ?? response.data;
  },

  /**
   * POST /api/passengers/bookings
   * Hỗ trợ nhận FormData hoặc JSON object chứa tourId, tourPackageId, numberOfRooms, primaryContactName, passengers...
   */
  create: async (data) => {
    const response = await api.post(BASE_URL, data);
    return response.data?.data ?? response.data;
  },

  cancel: async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/cancel`);
    return response.data?.data ?? response.data;
  },
};

export default bookingService;
