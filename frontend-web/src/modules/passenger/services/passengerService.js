// src/modules/passenger/services/passengerService.js
import api from "../../../api/axios";

const BASE_URL = "/passengers";

const passengerService = {
  /**
   * GET /api/passengers
   */
  getAll: async () => {
    const response = await api.get(BASE_URL);
    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/passengers/{id}
   */
  getById: async (id) => {
    const response = await api.get(`${BASE_URL}/${id}`);
    return response.data?.data ?? response.data;
  },

  /**
   * PATCH /api/passengers/{id}
   */
  update: async (id, data) => {
    const response = await api.patch(`${BASE_URL}/${id}`, data);
    return response.data?.data ?? response.data;
  },
};

export default passengerService;
