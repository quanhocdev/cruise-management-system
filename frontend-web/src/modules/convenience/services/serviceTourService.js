// src/modules/convenience/services/serviceTourService.js
// src/modules/convenience/services/serviceTourService.js

import api from "../../../api/axios";

const API_URL = "/convenience/service-tours";

export const serviceTourService = {
  // =====================================================
  // GET CONFIGURABLE
  // =====================================================

  // GET /api/convenience/service-tours/pending-config
  getPendingConfig: async () => {
    const response = await api.get(`${API_URL}/pending-config`);

    return response.data;
  },

  // =====================================================
  // POST CONFIG
  // =====================================================

  // POST /api/convenience/service-tours/{assignmentId}/config
  configure: async (assignmentId, data) => {
    const response = await api.post(`${API_URL}/${assignmentId}/config`, data);

    return response.data;
  },

  // =====================================================
  // PATCH CONFIG
  // =====================================================

  // PATCH /api/convenience/service-tours/{assignmentId}/config
  updateConfig: async (assignmentId, data) => {
    const response = await api.patch(`${API_URL}/${assignmentId}/config`, data);

    return response.data;
  },
};

export default serviceTourService;
