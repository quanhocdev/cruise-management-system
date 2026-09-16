// src/modules/finance/services/financeService.js
import api from "../../../api/axios";

const BASE_URL = "/finance";

const financeService = {
  getTours: async (cruiseId) => {
    const res = await api.get(`${BASE_URL}/tours`, {
      params: cruiseId ? { cruiseId } : undefined,
    });
    return res.data;
  },

  getSchedulesByTour: async (tourId) =>
    (await api.get(`${BASE_URL}/tours/${tourId}/schedules`)).data,

  getScheduleStops: async (scheduleId) => {
    const data = (await api.get(`${BASE_URL}/schedules/${scheduleId}/stops`))
      .data;
    return [...data].sort((a, b) => (a.stopOrder ?? 0) - (b.stopOrder ?? 0));
  },
  getCruiseByTour: async (tourId) =>
    (await api.get(`${BASE_URL}/tours/${tourId}/cruise`)).data,

  getDecksByCruise: async (cruiseId) =>
    (await api.get(`${BASE_URL}/cruises/${cruiseId}/decks`)).data,

  getAreasByDeck: async (deckId) =>
    (await api.get(`${BASE_URL}/decks/${deckId}/areas`)).data,

  getRoomsByDeck: async (deckId) =>
    (await api.get(`${BASE_URL}/decks/${deckId}/rooms`)).data,

  getRoomTypes: async () => (await api.get(`${BASE_URL}/room-types`)).data,

  getNfcCards: async () => (await api.get(`${BASE_URL}/nfc-cards`)).data,
};

export default financeService;
