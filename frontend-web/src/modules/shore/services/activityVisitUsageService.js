import api from "../../../api/axios";

const API_URL = "/shore/activity-visit-usages";

export const activityVisitUsageService = {
  // GET ALL ACTIVITY VISIT USAGES
  getAll: async () => {
    const response = await api.get(API_URL);
    return response.data;
  },
};
