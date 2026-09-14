import api from "../../../api/axios";

const BASE_URL = "/operation/tours";

const tourCruiseAssignmentService = {
  getAvailableCruises: async (tourId) => {
    const response = await api.get(`${BASE_URL}/${tourId}/available-cruises`);
    return response.data;
  },

  getCruiseLayout: async (tourId) => {
    const response = await api.get(`${BASE_URL}/${tourId}/cruise-layout`);
    return response.data;
  },

  /**
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
