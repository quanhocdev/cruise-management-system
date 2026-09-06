import { useState } from "react";
import tourBookingService from "../services/tourBookingService";

const useTourBooking = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // 1. Lấy thông tin cấu hình
  const getBookingConfig = async (tourId) => {
    try {
      setLoading(true);
      setError(null);
      const response = await tourBookingService.getBookingConfig(tourId);
      return response;
    } catch (err) {
      const errorMessage =
        err.response?.data?.message ||
        "Không thể lấy thông tin cấu hình booking";
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // 2. Mở bán / Tạo mới cấu hình
  const openBooking = async (tourId, bookingData) => {
    try {
      setLoading(true);
      setError(null);
      const response = await tourBookingService.openTourBooking(
        tourId,
        bookingData,
      );
      return response;
    } catch (err) {
      const errorMessage =
        err.response?.data?.message || "Có lỗi xảy ra khi mở bán vé";
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // 3. Cập nhật cấu hình (Patch)
  const updateBooking = async (tourId, bookingData) => {
    try {
      setLoading(true);
      setError(null);
      const response = await tourBookingService.updateTourBooking(
        tourId,
        bookingData,
      );
      return response;
    } catch (err) {
      const errorMessage =
        err.response?.data?.message || "Có lỗi xảy ra khi cập nhật booking";
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // 4. Hủy/Xóa cấu hình
  const deleteBooking = async (tourId) => {
    try {
      setLoading(true);
      setError(null);
      const response = await tourBookingService.deleteTourBooking(tourId);
      return response;
    } catch (err) {
      const errorMessage =
        err.response?.data?.message || "Có lỗi xảy ra khi hủy cấu hình booking";
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return {
    getBookingConfig,
    openBooking,
    updateBooking,
    deleteBooking,
    loading,
    error,
  };
};

export default useTourBooking;
