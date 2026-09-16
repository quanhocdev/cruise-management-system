// src/modules/finance/services/financeService.js
import api from "../../../api/axios";

const BASE_URL = "/finance";

const financeService = {
  // Lấy danh sách tất cả các tour
  getTours: async (cruiseId = null) => {
    const url = cruiseId
      ? `${BASE_URL}/tours?cruiseId=${cruiseId}`
      : `${BASE_URL}/tours`;
    const response = await api.get(url);
    return response.data;
  },

  // Lấy lịch trình (schedules) của 1 tour
  getSchedulesByTour: async (tourId) => {
    const response = await api.get(`${BASE_URL}/tours/${tourId}/schedules`);
    return response.data;
  },

  // Lấy lịch dừng (stops) theo scheduleId
  getScheduleStops: async (scheduleId) => {
    const response = await api.get(`${BASE_URL}/schedules/${scheduleId}/stops`);
    return response.data;
  },

  // Lấy danh sách phòng theo tầng (deckId)
  getRoomsByDeck: async (deckId) => {
    const response = await api.get(`${BASE_URL}/decks/${deckId}/rooms`);
    return response.data;
  },

  // Lấy danh sách tầng (decks) của tàu theo cruiseId
  getDecksByCruise: async (cruiseId) => {
    const response = await api.get(`${BASE_URL}/cruises/${cruiseId}/decks`);
    return response.data;
  },

  // Lấy danh sách vòng NFC
  getNfcCards: async () => {
    const response = await api.get(`${BASE_URL}/nfc-cards`);
    return response.data;
  },
};

export default financeService;
