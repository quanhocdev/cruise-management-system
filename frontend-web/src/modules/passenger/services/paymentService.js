// src/modules/passenger/services/paymentService.js
import api from "../../../api/axios";

// ==========================
// PAYMENT API
// Backend: /api/v1/payments
// ==========================
const PAYMENT_BASE_URL = "/v1/payments";

// ==========================
// PASSENGER PAYMENT API
// Backend: /api/passenger/payments
// ==========================
const PASSENGER_PAYMENT_BASE_URL = "/passenger/payments";

const paymentService = {
  /**
   * POST /api/v1/payments
   *
   * Tạo Payment và nhận thông tin thanh toán.
   * Backend sẽ xử lý việc tạo payment URL cho VNPay.
   */
  async createVnPayPayment(booking) {
    const response = await api.post(PAYMENT_BASE_URL, {
      referenceId: booking.id,
      referenceType: "BOOKING",
      amount: booking.totalAmount,
      method: "VNPAY",
    });

    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/v1/payments/{paymentId}
   *
   * Lấy thông tin Payment theo ID.
   */
  async getPayment(paymentId) {
    const response = await api.get(`${PAYMENT_BASE_URL}/${paymentId}`);

    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/passenger/payments/booking/{bookingId}
   *
   * Passenger lấy Payment theo Booking.
   *
   * Payment có thể chưa tồn tại nếu Payment Service
   * vẫn đang xử lý event Kafka.
   */
  async getPaymentByBookingId(bookingId) {
    const response = await api.get(
      `${PASSENGER_PAYMENT_BASE_URL}/booking/${bookingId}`,
    );

    return response.data?.data ?? response.data;
  },
};

export default paymentService;
