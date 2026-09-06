// src/modules/passenger/services/passengerService.js
import api from "../../../api/axios";

const BASE_URL = "/passengers";

const passengerService = {
  /**
   * GET /api/passengers?userId=...
   */
  getAll: async (userId) => {
    const response = await api.get(BASE_URL, {
      params: { userId },
    });
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
    // Không ép Content-Type để Axios tự động xử lý boundary cho FormData (chứa ảnh CCCD)
    const response = await api.patch(`${BASE_URL}/${id}`, data);
    return response.data?.data ?? response.data;
  },
};

export default passengerService;