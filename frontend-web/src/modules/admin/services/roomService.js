// src/modules/admin/services/roomService.js

import api from "../../../api/axios";

const ROOM_BASE_URL = "/admin/decks";

const roomService = {
  // =====================================================
  // GET ROOMS BY DECK
  // =====================================================

  async getRoomsByDeck(deckId, activeOnly = false) {
    const response = await api.get(`${ROOM_BASE_URL}/${deckId}/rooms`, {
      params: {
        activeOnly,
      },
    });

    return response.data;
  },

  // =====================================================
  // GET ROOM BY ID
  // =====================================================

  async getRoomById(deckId, roomId) {
    const response = await api.get(
      `${ROOM_BASE_URL}/${deckId}/rooms/${roomId}`,
    );

    return response.data;
  },

  // =====================================================
  // CREATE ROOMS
  // =====================================================

  async createRooms(deckId, data) {
    const response = await api.post(`${ROOM_BASE_URL}/${deckId}/rooms`, data);

    return response.data;
  },

  // =====================================================
  // UPDATE ROOM
  // =====================================================

  async updateRoom(deckId, roomId, data) {
    const response = await api.patch(
      `${ROOM_BASE_URL}/${deckId}/rooms/${roomId}`,
      data,
    );

    return response.data;
  },

  // =====================================================
  // DELETE ROOM
  // =====================================================

  async deleteRoom(deckId, roomId) {
    const response = await api.delete(
      `${ROOM_BASE_URL}/${deckId}/rooms/${roomId}`,
    );

    return response.data;
  },
};

export default roomService;
