// src/modules/onboard/services/activityCruiseTourService.js
import api from "../../../api/axios";

const API_URL = "/onboard/activity-cruise-tours";

export const activityCruiseTourService = {
  getAll: async () => {
    const response = await api.get(API_URL);
    return response.data;
  },

  getPendingConfig: async () => {
    const response = await api.get(`${API_URL}/pending-config`);
    return response.data;
  },

  configure: async (assignmentId, data) => {
    const response = await api.post(`${API_URL}/${assignmentId}/config`, data);
    return response.data;
  },

  updateConfig: async (assignmentId, data) => {
    const response = await api.patch(`${API_URL}/${assignmentId}/config`, data);
    return response.data;
  },

  // POST /api/onboard/activity-cruise-tours/{tourId}/complete
  completeTourConfiguration: async (tourId) => {
    const response = await api.post(`${API_URL}/${tourId}/complete`);
    return response.data;
  },
  // GET /api/onboard/activity-cruise-tours/configuration-history
  getConfigurationHistory: async () => {
    const response = await api.get(`${API_URL}/configuration-history`);
    return response.data;
  },
  // GET /api/onboard/activity-cruise-tours/tour/{tourId}
  getConfigurationDetail: async (tourId) => {
    const response = await api.get(`${API_URL}/tour/${tourId}`);
    return response.data;
  },
};
