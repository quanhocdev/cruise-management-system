// src/modules/admin/services/cruiseDeckService.js

import api from "../../../api/axios";

const cruiseDeckService = {
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

  // =====================================================
  // CREATE MULTIPLE DECKS
  // =====================================================

  createDecks: async (cruiseId, totalDecks) => {
    const response = await api.post(`/admin/cruises/${cruiseId}/decks`, {
      totalDecks: Number(totalDecks),
    });

    return response.data;
  },

  // =====================================================
  // UPDATE DECK
  // =====================================================

  updateDeck: async (cruiseId, deckId, data) => {
    const response = await api.patch(
      `/admin/cruises/${cruiseId}/decks/${deckId}`,
      data,
    );

    return response.data;
  },

  // =====================================================
  // DELETE DECK
  // =====================================================

  deleteDeck: async (cruiseId, deckId) => {
    const response = await api.delete(
      `/admin/cruises/${cruiseId}/decks/${deckId}`,
    );

    return response.data;
  },
};

export default cruiseDeckService;
