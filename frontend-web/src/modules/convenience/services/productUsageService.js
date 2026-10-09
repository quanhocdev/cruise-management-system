// src/modules/convenience/services/productUsageService.js
import api from "../../../api/axios";

const BASE_URL = "/convenience/product-usages";

const productUsageService = {
  // Lấy toàn bộ lịch sử sử dụng sản phẩm dành cho CONVENIENCE
  async getAll() {
    const response = await api.get(BASE_URL);
    return response.data;
  },

  // Lấy lịch sử sử dụng sản phẩm của một hành khách trong booking
  async getByBookingPassenger(bookingPassengerId) {
    const response = await api.get(
      `${BASE_URL}/booking-passenger/${bookingPassengerId}`
    );
    return response.data;
  },
};

export default productUsageService;
