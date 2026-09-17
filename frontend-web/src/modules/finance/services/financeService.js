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

  getBookingsByTour: async (tourId) => {
    console.log("========== FINANCE GET BOOKINGS ==========");
    console.log("[FE] tourId:", tourId);
    console.log("[FE] URL:", `${BASE_URL}/bookings`);
    console.log("[FE] params:", { tourId });

    try {
      const res = await api.get(`${BASE_URL}/bookings`, {
        params: { tourId },
      });

      console.log("[FE] response status:", res.status);
      console.log("[FE] response data:", res.data);

      return res.data;
    } catch (error) {
      console.error("========== FINANCE GET BOOKINGS ERROR ==========");
      console.error("[FE] message:", error.message);
      console.error("[FE] status:", error.response?.status);
      console.error("[FE] response data:", error.response?.data);
      console.error("[FE] response headers:", error.response?.headers);
      console.error("[FE] request URL:", error.config?.url);
      console.error("[FE] request params:", error.config?.params);
      console.error("[FE] full error:", error);

      throw error;
    }
  },

  getPassengersByBooking: async (bookingId) => {
    const res = await api.get(`${BASE_URL}/bookings/${bookingId}/passengers`);
    return res.data;
  },
};

export default financeService;
