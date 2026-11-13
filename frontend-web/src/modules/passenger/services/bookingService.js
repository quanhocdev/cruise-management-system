import api from "../../../api/axios";

const BASE_URL = "/passengers/bookings";

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

const bookingService = {
  getMine: async () => {
    const response = await api.get(BASE_URL);
    return normalizeBookings(response.data?.data ?? response.data);
  },

  getById: async (id, privileged = false) => {
    const response = await api.get(`${BASE_URL}/${id}`, {
      params: { privileged },
    });
    return normalizeBooking(response.data?.data ?? response.data);
  },

  create: async (data) => {
    const response = await api.post(BASE_URL, data);
    return normalizeBooking(response.data?.data ?? response.data);
  },

  cancel: async (id) => {
    const response = await api.patch(`${BASE_URL}/${id}/cancel`);
    return normalizeBooking(response.data?.data ?? response.data);
  },
};

export default bookingService;
