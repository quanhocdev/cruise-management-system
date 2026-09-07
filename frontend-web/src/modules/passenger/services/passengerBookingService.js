import api from "../../../api/axios";

const passengerBookingService = {
  async createBooking(payload) {
    const response = await api.post("/api/passengers/bookings", payload);
    return response.data?.data ?? response.data;
  },

  async getMyBookings() {
    const response = await api.get("/api/passengers/bookings");
    return response.data?.data ?? response.data;
  },

  async getBooking(bookingId) {
    const response = await api.get(`/api/passengers/bookings/${bookingId}`);
    return response.data?.data ?? response.data;
  },

  async cancelBooking(bookingId) {
    const response = await api.patch(
      `/api/passengers/bookings/${bookingId}/cancel`,
    );
    return response.data?.data ?? response.data;
  },
};

export default passengerBookingService;
