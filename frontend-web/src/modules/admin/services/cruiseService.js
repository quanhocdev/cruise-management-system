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

  // =====================================================
  // DECK
  // =====================================================

  getDecks: async (cruiseId, activeOnly = false) => {
    const response = await api.get(`/admin/cruises/${cruiseId}/decks`, {
      params: { activeOnly },
    });

    return response.data;
  },

  getDeckById: async (cruiseId, deckId) => {
    const response = await api.get(
      `/admin/cruises/${cruiseId}/decks/${deckId}`,
    );

    return response.data;
  },

  // TẠO NHIỀU TẦNG
  // Backend nhận:
  // {
  //   "totalDecks": 5
  // }
  //
  // Backend tự tạo:
  // Tầng 1
  // Tầng 2
  // Tầng 3
  // Tầng 4
  // Tầng 5
  createDecks: async (cruiseId, totalDecks) => {
    const response = await api.post(`/admin/cruises/${cruiseId}/decks`, {
      totalDecks: Number(totalDecks),
    });

    return response.data;
  },

  updateDeck: async (cruiseId, deckId, data) => {
    const response = await api.patch(
      `/admin/cruises/${cruiseId}/decks/${deckId}`,
      data,
    );

    return response.data;
  },

  deleteDeck: async (cruiseId, deckId) => {
    const response = await api.delete(
      `/admin/cruises/${cruiseId}/decks/${deckId}`,
    );

    return response.data;
  },

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

  createArea: async (deckId, formData) => {
    const response = await api.post(`/admin/decks/${deckId}/areas`, formData);

    return response.data;
  },

  updateArea: async (deckId, areaId, formData) => {
    const response = await api.patch(
      `/admin/decks/${deckId}/areas/${areaId}`,
      formData,
    );

    return response.data;
  },

  deactivateArea: async (deckId, areaId) => {
    const response = await api.patch(
      `/admin/decks/${deckId}/areas/${areaId}/deactivate`,
    );

    return response.data;
  },

  deleteArea: async (deckId, areaId) => {
    const response = await api.delete(`/admin/decks/${deckId}/areas/${areaId}`);

    return response.data;
  },

  // =====================================================
  // ROOM
  // =====================================================

  getRooms: async (deckId, activeOnly = false) => {
    const response = await api.get(`/admin/decks/${deckId}/rooms`, {
      params: { activeOnly },
    });

    return response.data;
  },

  getRoomById: async (deckId, roomId) => {
    const response = await api.get(`/admin/decks/${deckId}/rooms/${roomId}`);

    return response.data;
  },

  createRoom: async (deckId, data) => {
    const response = await api.post(`/admin/decks/${deckId}/rooms`, data);

    return response.data;
  },

  updateRoom: async (deckId, roomId, data) => {
    const response = await api.patch(
      `/admin/decks/${deckId}/rooms/${roomId}`,
      data,
    );

    return response.data;
  },

  deleteRoom: async (deckId, roomId) => {
    const response = await api.delete(`/admin/decks/${deckId}/rooms/${roomId}`);

    return response.data;
  },
};

export default cruiseService;
