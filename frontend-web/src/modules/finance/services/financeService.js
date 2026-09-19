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
      throw error;
    }
  },

  getPassengersByBooking: async (bookingId) => {
    const res = await api.get(`${BASE_URL}/bookings/${bookingId}/passengers`);
    return res.data;
  },
  // Thêm vào trong đối tượng financeService ở src/modules/finance/services/financeService.js

  // Lấy danh sách phòng trống theo booking và hạng phòng
  getAvailableRooms: async (bookingId, roomTypeId) => {
    const res = await api.get(
      `${BASE_URL}/bookings/${bookingId}/available-rooms`,
      {
        params: { roomTypeId },
      },
    );
    return res.data;
  },

  // Lấy danh sách vòng NFC còn trống (AVAILABLE) theo tour
  getAvailableWristbands: async (tourId) => {
    const res = await api.get(
      `${BASE_URL}/tours/${tourId}/available-wristbands`,
    );
    return res.data;
  },

  // Gửi request check-in từng hành khách sang booking-service
  checkInPassenger: async (bookingId, payload) => {
    const res = await api.post(
      `/api/finance/check-passenger/${bookingId}/check-in-passenger`,
      payload,
    );
    return res.data;
  },
};

export default financeService;
