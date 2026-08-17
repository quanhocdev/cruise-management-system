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
   * GET /api/operation/tours/{id}/available-cruises
   */
  getAvailableCruises: async (tourId) => {
    const response = await api.get(
      `${OPERATION_TOUR_BASE_URL}/${tourId}/available-cruises`,
    );
    return response.data;
  },

  /**
   * POST /api/operation/tours/{id}/approve?cruiseId=...
   */
  approveTour: async (tourId, cruiseId) => {
    const response = await api.post(
      `${OPERATION_TOUR_BASE_URL}/${tourId}/approve`,
      null,
      {
        params: {
          cruiseId,
        },
      },
    );
    return response.data;
  },
};

export default operationTourService;
