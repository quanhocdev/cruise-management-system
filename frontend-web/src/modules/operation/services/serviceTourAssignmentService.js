// src/modules/operation/services/serviceTourAssignmentService.js

import api from "../../../api/axios";

const BASE_URL = "/operation/service-tour-assignment";

const serviceTourAssignmentService = {
  /**
   * GET /api/operation/service-tour-assignment/tour/{tourId}
   */
  getByTour: async (tourId) => {
    const response = await api.get(`${BASE_URL}/tour/${tourId}`);

    return response.data;
  },

  /**
   * POST /api/operation/service-tour-assignment
   */
  assign: async (payload) => {
    const response = await api.post(BASE_URL, payload, {
      headers: {
        "Content-Type": "application/json",
      },
    });

    return response.data;
  },

  /**
   * DELETE
   * /api/operation/service-tour-assignment/tour/{tourId}/area/{cruiseAreaId}
   */
  delete: async (tourId, cruiseAreaId) => {
    const response = await api.delete(
      `${BASE_URL}/tour/${tourId}/area/${cruiseAreaId}`,
    );

    return response.data;
  },
};

export default serviceTourAssignmentService;
