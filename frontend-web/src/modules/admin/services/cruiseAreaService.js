// src/modules/admin/services/cruiseAreaService.js

import api from "../../../api/axios";

const cruiseAreaService = {
  // =====================================================
  // AREA
  // =====================================================

  getAreas: async (deckId, activeOnly = false) => {
    const response = await api.get(`/admin/decks/${deckId}/areas`, {
      params: { activeOnly },
    });

    return response.data;
  },

  getAreaById: async (deckId, areaId) => {
    const response = await api.get(`/admin/decks/${deckId}/areas/${areaId}`);

    return response.data;
  },

  // =====================================================
  // CREATE AREA
  // =====================================================

  createArea: async (deckId, formData) => {
    const response = await api.post(`/admin/decks/${deckId}/areas`, formData);

    return response.data;
  },

  // =====================================================
  // UPDATE AREA
  // =====================================================

  updateArea: async (deckId, areaId, formData) => {
    const response = await api.patch(
      `/admin/decks/${deckId}/areas/${areaId}`,
      formData,
    );

    return response.data;
  },

  // =====================================================
  // DEACTIVATE AREA
  // =====================================================

  deactivateArea: async (deckId, areaId) => {
    const response = await api.patch(
      `/admin/decks/${deckId}/areas/${areaId}/deactivate`,
    );

    return response.data;
  },

  // =====================================================
  // DELETE AREA
  // =====================================================

  deleteArea: async (deckId, areaId) => {
    const response = await api.delete(`/admin/decks/${deckId}/areas/${areaId}`);

    return response.data;
  },
};

export default cruiseAreaService;
