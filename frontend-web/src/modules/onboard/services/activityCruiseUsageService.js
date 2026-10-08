// src/modules/onboard/services/activityCruiseUsageService.js
import api from "../../../api/axios";

const API_URL = "/onboard/activity-cruise-usages";

export const activityCruiseUsageService = {
  // =====================================================
  // GET ALL ACTIVITY CRUISE USAGES
  // =====================================================

  getAll: async () => {
    const response = await api.get(API_URL);

    return response.data;
  },
};
