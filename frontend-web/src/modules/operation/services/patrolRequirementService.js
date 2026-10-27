import api from "../../../api/axios";

const BASE_URL = "/operation/tours";

const patrolRequirementService = {
  /**
   * GET /api/operation/tours/{tourId}/patrol-requirements
   * Lấy yêu cầu nhân sự tuần tra theo từng tầng.
   */
  getByTour: async (tourId) => {
    const response = await api.get(`${BASE_URL}/${tourId}/patrol-requirements`);

    return response.data;
  },

  save: async (tourId, payload) => {
    const response = await api.put(
      `${BASE_URL}/${tourId}/patrol-requirements`,
      payload,
      {
        headers: {
          "Content-Type": "application/json",
        },
      },
    );

    return response.data;
  },
};

export default patrolRequirementService;
