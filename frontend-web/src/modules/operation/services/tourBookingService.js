// src/modules/operation/services/tourBookingService.js
import api from "../../../api/axios";

const TOUR_BOOKING_BASE_URL = "/operation/bookings";

const tourBookingService = {
  /**
   * GET /api/operation/bookings/tours/{id}
   * Lấy thông tin cấu hình booking của tour
   */
  getBookingConfig: async (tourId) => {
    const response = await api.get(`${TOUR_BOOKING_BASE_URL}/tours/${tourId}`);
    return response.data;
  },

  /**
   * POST /api/operation/bookings/tours/{id}/open
   * Tạo mới / Mở bán vé cho tour
   */
  openTourBooking: async (tourId, bookingData) => {
    const response = await api.post(
      `${TOUR_BOOKING_BASE_URL}/tours/${tourId}/open`,
      bookingData,
    );
    return response.data;
  },

  /**
   * PATCH /api/operation/bookings/tours/{id}
   * Cập nhật thời gian mở bán booking (chỉ khi NOT_OPEN hoặc WAITING)
   */
  updateTourBooking: async (tourId, bookingData) => {
    const response = await api.patch(
      `${TOUR_BOOKING_BASE_URL}/tours/${tourId}`,
      bookingData,
    );
    return response.data;
  },

  /**
   * DELETE /api/operation/bookings/tours/{id}
   * Hủy cấu hình booking (chỉ khi NOT_OPEN hoặc WAITING)
   */
  deleteTourBooking: async (tourId) => {
    const response = await api.delete(
      `${TOUR_BOOKING_BASE_URL}/tours/${tourId}`,
    );
    return response.data;
  },
};

export default tourBookingService;
