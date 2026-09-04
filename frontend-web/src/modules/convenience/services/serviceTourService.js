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
  // =====================================================
  // GET ALL
  // =====================================================

  // GET /api/convenience/service-tours
  getAll: async () => {
    const response = await api.get(API_URL);

    return response.data;
  },

  // =====================================================
  // GET CONFIGURATION HISTORY
  // =====================================================

  // GET /api/convenience/service-tours/configuration-history
  getConfigurationHistory: async () => {
    const response = await api.get(`${API_URL}/configuration-history`);

    return response.data;
  },

  // =====================================================
  // GET CONFIGURATION HISTORY DETAIL
  // =====================================================

  // GET /api/convenience/service-tours/tour/{tourId}
  getConfigurationHistoryDetail: async (tourId) => {
    const response = await api.get(`${API_URL}/tour/${tourId}`);

    return response.data;
  },

  // =====================================================
  // COMPLETE CONFIGURATION
  // =====================================================

  // POST /api/convenience/service-tours/{tourId}/complete
  completeConfiguration: async (tourId) => {
    const response = await api.post(`${API_URL}/${tourId}/complete`);

    return response.data;
  },
};

export default serviceTourService;
