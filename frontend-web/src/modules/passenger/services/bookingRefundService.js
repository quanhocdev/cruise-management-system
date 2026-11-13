import api from "../../../api/axios";

const BASE_URL = "/passengers";

const bookingRefundService = {
  // GET /api/passengers/bookings/{bookingId}/refund-preview
  preview: async (bookingId) => {
    const response = await api.get(
      `${BASE_URL}/bookings/${bookingId}/refund-preview`,
    );
    return response.data?.data ?? response.data;
  },

  // POST /api/passengers/bookings/{bookingId}/refunds
  create: async (bookingId, data) => {
    const response = await api.post(
      `${BASE_URL}/bookings/${bookingId}/refunds`,
      data,
    );
    return response.data?.data ?? response.data;
  },

  // GET /api/passengers/bookings/{bookingId}/refunds/latest
  getLatest: async (bookingId) => {
    const response = await api.get(
      `${BASE_URL}/bookings/${bookingId}/refunds/latest`,
    );
    return response.data?.data ?? response.data;
  },

  // GET /api/passengers/refunds
  getMine: async () => {
    const response = await api.get(`${BASE_URL}/refunds`);
    return response.data?.data ?? response.data;
  },
};

export default bookingRefundService;
