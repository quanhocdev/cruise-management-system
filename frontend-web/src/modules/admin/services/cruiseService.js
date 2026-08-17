// src/modules/admin/services/cruiseService.js

import api from "../../../api/axios";

const cruiseService = {
  // =====================================================
  // CRUISE
  // =====================================================

  getAllCruises: async (activeOnly = false) => {
    const response = await api.get("/admin/cruises", {
      params: { activeOnly },
    });

    return response.data;
  },

  getCruiseById: async (cruiseId) => {
    const response = await api.get(`/admin/cruises/${cruiseId}`);

    return response.data;
  },

  createCruise: async (formData) => {
    const response = await api.post("/admin/cruises", formData);

    return response.data;
  },

  updateCruise: async (cruiseId, formData) => {
    const response = await api.patch(`/admin/cruises/${cruiseId}`, formData);

    return response.data;
  },

  deleteCruise: async (cruiseId) => {
    const response = await api.delete(`/admin/cruises/${cruiseId}`);

    return response.data;
  },
};

export default cruiseService;
