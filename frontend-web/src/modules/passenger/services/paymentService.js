// src/modules/passenger/services/paymentService.js
import api from "../../../api/axios";

const BASE_URL = "/passenger/payments";

const paymentService = {
  /**
   * POST /api/passenger/payments
   * Tạo yêu cầu thanh toán thủ công nếu cần
   */
  async createVnPayPayment(booking) {
    const response = await api.post(BASE_URL, {
      referenceId: booking.id,
      referenceType: "BOOKING",
      amount: booking.totalAmount,
      method: "VNPAY",
    });
    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/passenger/payments/{id}
   * Lấy thông tin thanh toán theo ID
   */
  async getPayment(paymentId) {
    const response = await api.get(`${BASE_URL}/${paymentId}`);
    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/passenger/payments/booking/{bookingId}
   * Lấy link VNPay (paymentUrl) dựa vào bookingId do Kafka tạo tự động
   */
  async getPaymentByBookingId(bookingId) {
    const response = await api.get(`${BASE_URL}/booking/${bookingId}`);
    return response.data?.data ?? response.data;
  },
};

export default paymentService;
