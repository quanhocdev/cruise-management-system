// src/modules/convenience/services/serviceUsageService.js
import api from "../../../api/axios";

const BASE_URL = "/convenience/service-usages";

const serviceUsageService = {
  // Lấy toàn bộ lịch sử sử dụng dịch vụ dành cho CONVENIENCE
  async getAll() {
    const response = await api.get(BASE_URL);
    return response.data;
  },

  // Lấy lịch sử sử dụng dịch vụ của một hành khách trong booking
  async getByBookingPassenger(bookingPassengerId) {
    const response = await api.get(
      `${BASE_URL}/booking-passenger/${bookingPassengerId}`,
    );
    return response.data;
  },
};

export default serviceUsageService;
