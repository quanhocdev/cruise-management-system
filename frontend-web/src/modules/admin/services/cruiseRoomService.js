// src/modules/admin/services/cruiseRoomService.js

import api from "../../../api/axios";

const cruiseRoomService = {
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

  // =====================================================
  // CREATE ROOM
  // =====================================================

  createRoom: async (deckId, data) => {
    const response = await api.post(`/admin/decks/${deckId}/rooms`, data);

    return response.data;
  },

  // =====================================================
  // UPDATE ROOM
  // =====================================================

  updateRoom: async (deckId, roomId, data) => {
    const response = await api.patch(
      `/admin/decks/${deckId}/rooms/${roomId}`,
      data,
    );

    return response.data;
  },

  // =====================================================
  // DELETE ROOM
  // =====================================================

  deleteRoom: async (deckId, roomId) => {
    const response = await api.delete(`/admin/decks/${deckId}/rooms/${roomId}`);

    return response.data;
  },
};

export default cruiseRoomService;
