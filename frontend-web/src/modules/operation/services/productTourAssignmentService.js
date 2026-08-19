import api from "../../../api/axios";

const BASE_URL = "/operation/product-tour-assignment";

const productTourAssignmentService = {
  /**
   * GET /api/operation/product-tour-assignment/tour/{tourId}
   * Lấy danh sách phân công tiện ích/sản phẩm của Tour
   */
  getByTour: async (tourId) => {
    const response = await api.get(`${BASE_URL}/tour/${tourId}`);
    return response.data;
  },

  /**
   * POST /api/operation/product-tour-assignment
   * Phân công tiện ích/sản phẩm cho Tour
   */
  assign: async (payload) => {
    const response = await api.post(BASE_URL, payload, {
      headers: { "Content-Type": "application/json" },
    });
    return response.data;
  },

  /**
   * DELETE /api/operation/product-tour-assignment/tour/{tourId}/area/{cruiseAreaId}
   * Xóa phân công tiện ích theo tourId và cruiseAreaId (Chuẩn hóa với Activity)
   */
  delete: async (tourId, cruiseAreaId) => {
    const response = await api.delete(
      `${BASE_URL}/tour/${tourId}/area/${cruiseAreaId}`,
    );
    return response.data;
  },
};

export default productTourAssignmentService;
