// src/modules/passenger/services/passengerBookingService.js
import api from "../../../api/axios";

const passengerBookingService = {
  async createBooking(payload) {
    const response = await api.post("/passengers/bookings", payload);
    return response.data?.data ?? response.data;
  },

  async getMyBookings() {
    const response = await api.get("/passengers/bookings");
    return response.data?.data ?? response.data;
  },

  async getBooking(bookingId) {
    const response = await api.get(`/passengers/bookings/${bookingId}`);
    return response.data?.data ?? response.data;
  },

  async cancelBooking(bookingId) {
    const response = await api.patch(
      `/passengers/bookings/${bookingId}/cancel`,
    );
    return response.data?.data ?? response.data;
  },
};

export default passengerBookingService;
