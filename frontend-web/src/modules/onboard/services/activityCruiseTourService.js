// src/modules/onboard/services/activityCruiseTourService.js

import api from "../../../api/axios";

const API_URL = "/onboard/activity-cruise-tours";

export const activityCruiseTourService = {
  // =====================================================
  // GET ALL ASSIGNMENTS
  // =====================================================

  getAll: async () => {
    const response = await api.get(API_URL);
    return response.data;
  },

  // =====================================================
  // GET PENDING CONFIGURATION
  // =====================================================

  getPendingConfig: async () => {
    const response = await api.get(`${API_URL}/pending-config`);
    return response.data;
  },

  // =====================================================
  // CREATE / SAVE CONFIGURATION
  // =====================================================

  configure: async (assignmentId, data) => {
    const response = await api.post(`${API_URL}/${assignmentId}/config`, data);

    return response.data;
  },

  // =====================================================
  // UPDATE CONFIGURATION
  // =====================================================

  updateConfig: async (assignmentId, data) => {
    const response = await api.patch(`${API_URL}/${assignmentId}/config`, data);

    return response.data;
  },

  // =====================================================
  // COMPLETE TOUR CONFIGURATION
  // =====================================================

  completeTourConfiguration: async (tourId) => {
    const response = await api.post(`${API_URL}/${tourId}/complete`);

    return response.data;
  },

  // =====================================================
  // GET CONFIGURATION DETAIL BY TOUR
  // =====================================================

  getConfigurationDetail: async (tourId) => {
    const response = await api.get(`${API_URL}/tour/${tourId}`);

    return response.data;
  },
};
