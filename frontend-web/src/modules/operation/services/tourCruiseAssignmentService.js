import api from "../../../api/axios";

const BASE_URL = "/operation/tours";

const tourCruiseAssignmentService = {
  /**
   * GET /api/operation/tours/{id}/available-cruises
   * Lấy danh sách du thuyền khả dụng cho Tour
   */
  getAvailableCruises: async (tourId) => {
    const response = await api.get(`${BASE_URL}/${tourId}/available-cruises`);
    return response.data;
  },

  /**
   * GET /api/operation/tours/{id}/cruise-layout
   * Lấy toàn bộ Deck + Area của Cruise đang được gán cho Tour
   */
  getCruiseLayout: async (tourId) => {
    const response = await api.get(`${BASE_URL}/${tourId}/cruise-layout`);
    return response.data;
  },

  /**
   * POST /api/operation/tours/{id}/assign-cruise?cruiseId=...
   * Gán du thuyền cho Tour
   */
  assignCruise: async (tourId, cruiseId) => {
    const response = await api.post(
      `${BASE_URL}/${tourId}/assign-cruise`,
      null,
      {
        params: { cruiseId },
      },
    );
    return response.data;
  },
};

export default tourCruiseAssignmentService;
