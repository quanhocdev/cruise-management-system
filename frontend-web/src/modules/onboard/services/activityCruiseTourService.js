// src/modules/onboard/services/activityCruiseTourService.js
import api from "../../../api/axios";

// Backend:
// /api/onboard/activity-cruise-tours
const API_URL = "/onboard/activity-cruise-tours";

export const activityCruiseTourService = {
  // =====================================================
  // GET PENDING CONFIG
  // =====================================================

  // GET /api/onboard/activity-cruise-tours/pending-config
  getPendingConfig: async () => {
    const response = await api.get(`${API_URL}/pending-config`);
    return response.data;
  },

  // =====================================================
  // POST CONFIG
  // =====================================================

  // POST /api/onboard/activity-cruise-tours/{assignmentId}/config
  configure: async (assignmentId, data) => {
    const response = await api.post(`${API_URL}/${assignmentId}/config`, data);

    return response.data;
  },

  // =====================================================
  // PATCH CONFIG
  // =====================================================

  // PATCH /api/onboard/activity-cruise-tours/{assignmentId}/config
  updateConfig: async (assignmentId, data) => {
    const response = await api.patch(`${API_URL}/${assignmentId}/config`, data);

    return response.data;
  },
};
