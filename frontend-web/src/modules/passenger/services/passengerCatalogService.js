import api from "../../../api/axios";

const passengerCatalogService = {
  async getOpenTours() {
    const response = await api.get("/passenger/tours");
    return response.data;
  },

  async getTourDetail(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}`);
    return response.data;
  },

  async getDepartures(tourId) {
    const response = await api.get(`/passenger/tours/${tourId}/departures`);
    return response.data;
  },

  async getAvailableRooms(voyageId) {
    const response = await api.get(
      `/v1/bookings/voyages/${voyageId}/available-rooms`,
    );
    return response.data;
  },

  async createBooking(payload) {
    const response = await api.post("/v1/bookings", payload);
    return response.data;
  },

  async getMyBookings() {
    const response = await api.get("/v1/bookings/me");
    return response.data;
  },

  async getBooking(bookingId) {
    const response = await api.get(`/v1/bookings/${bookingId}`);
    return response.data;
  },

  async cancelBooking(bookingId) {
    const response = await api.patch(`/v1/bookings/${bookingId}/cancel`);
    return response.data;
  },
};

export default passengerCatalogService;
