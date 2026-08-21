import api from "../../../api/axios";

const OPERATION_TOUR_BASE_URL = "/operation/tours";

const operationTourService = {
  /**
   * GET /api/operation/tours/pending
   */
  getPendingTours: async () => {
    const response = await api.get(`${OPERATION_TOUR_BASE_URL}/pending`);
    return response.data;
  },

  /**
   * GET /api/operation/tours/approved
   */
  getApprovedTours: async () => {
    const response = await api.get(`${OPERATION_TOUR_BASE_URL}/approved`);
    return response.data;
  },

  /**
   * POST /api/operation/tours/{id}/approve
   * Duyệt Tour - Đổi trạng thái sang APPROVED sau khi đã phân công xong khu vực
   */
  approveTour: async (tourId, payload = null) => {
    const response = await api.post(
      `${OPERATION_TOUR_BASE_URL}/${tourId}/approve`,
      payload,
    );
    return response.data;
  },
};

export default operationTourService;
