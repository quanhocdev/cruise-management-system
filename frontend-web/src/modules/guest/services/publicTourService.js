// src/modules/guest/services/publicTourService.js
import api from "../../../api/axios";

// Trỏ đúng tiền tố endpoint public đã khai báo ở Gateway và Backend
const API_URL = "/public/tours";

export const publicTourService = {
  // Lấy danh sách tóm tắt tất cả các tour mở công khai
  getAll: async () => {
    const response = await api.get(API_URL);
    return response.data;
  },

  // Lấy thông tin chi tiết đầy đủ của 1 tour theo ID
  getById: async (id) => {
    const response = await api.get(`${API_URL}/${id}`);
    return response.data;
  },
};
