import api from "../../../api/axios";

const paymentService = {
  async createVnPayPayment(booking) {
    const response = await api.post("/v1/payments", {
      referenceId: booking.id,
      referenceType: "BOOKING",
      amount: booking.totalAmount,
      method: "VNPAY",
    });
    return response.data;
  },

  async getPayment(paymentId) {
    const response = await api.get(`/v1/payments/${paymentId}`);
    return response.data;
  },
};

export default paymentService;
