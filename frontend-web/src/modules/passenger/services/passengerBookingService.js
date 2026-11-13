import api from "../../../api/axios";

const normalizeBooking = (booking) => {
  if (!booking || typeof booking !== "object") {
    return booking;
  }

  return {
    ...booking,
    passengers: booking.passengers ?? booking.bookingPassengers ?? [],
  };
};

const normalizeBookings = (bookings) => {
  if (!Array.isArray(bookings)) {
    return bookings;
  }

  return bookings.map(normalizeBooking);
};

const passengerBookingService = {
  async createBooking(payload) {
    const response = await api.post("/passengers/bookings", payload);
    return normalizeBooking(response.data?.data ?? response.data);
  },

  async getMyBookings() {
    const response = await api.get("/passengers/bookings");
    return normalizeBookings(response.data?.data ?? response.data);
  },

  async getBooking(bookingId) {
    const response = await api.get(`/passengers/bookings/${bookingId}`);
    return normalizeBooking(response.data?.data ?? response.data);
  },

  async cancelBooking(bookingId) {
    const response = await api.patch(
      `/passengers/bookings/${bookingId}/cancel`,
    );
    return normalizeBooking(response.data?.data ?? response.data);
  },
};

export default passengerBookingService;
