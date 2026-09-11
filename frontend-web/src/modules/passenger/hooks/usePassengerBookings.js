// src/modules/passenger/hooks/usePassengerBookings.js
import { useState, useCallback } from "react";
import passengerBookingService from "../services/passengerBookingService";

export default function usePassengerBookings() {
  const [bookings, setBookings] = useState([]);
  const [currentBooking, setCurrentBooking] = useState(null);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadMyBookings = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await passengerBookingService.getMyBookings();
      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setBookings(list);
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách đơn đặt vé của bạn.",
      );
      setBookings([]);
    } finally {
      setLoading(false);
    }
  }, []);

  const loadBookingById = useCallback(async (id) => {
    if (!id) return;
    setLoading(true);
    setError("");
    try {
      const data = await passengerBookingService.getBooking(id); // 👈 Sửa thành passengerBookingService
      setCurrentBooking(data);
      return data;
    } catch (err) {
      setError(
        err.response?.data?.message || "Không thể tải chi tiết đơn hàng.",
      );
      setCurrentBooking(null);
    } finally {
      setLoading(false);
    }
  }, []);

  const cancelBooking = useCallback(async (id) => {
    setSubmitting(true);
    setError("");
    setSuccess("");
    try {
      const updated = await passengerBookingService.cancelBooking(id); // 👈 Sửa thành passengerBookingService
      setSuccess("Hủy đơn hàng thành công.");
      return updated;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể hủy đơn hàng.");
      throw err;
    } finally {
      setSubmitting(false);
    }
  }, []);

  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  return {
    bookings,
    currentBooking,
    loading,
    submitting,
    error,
    success,
    loadMyBookings,
    loadBookingById,
    cancelBooking,
    clearMessages,
  };
}
