import { useCallback, useState } from "react";
import bookingService from "../services/bookingService";

export default function useBookings() {
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
      const data = await bookingService.getMine();
      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setBookings(list);
    } catch (err) {
      setError(
        err.response?.data?.message || "Không thể tải danh sách đơn đặt vé.",
      );
      setBookings([]);
    } finally {
      setLoading(false);
    }
  }, []);

  const loadBookingById = useCallback(async (id, privileged = false) => {
    if (!id) return;
    setLoading(true);
    setError("");
    try {
      const data = await bookingService.getById(id, privileged);
      setCurrentBooking(data);
      return data;
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Không thể tải thông tin chi tiết đơn hàng.",
      );
      setCurrentBooking(null);
    } finally {
      setLoading(false);
    }
  }, []);

  const createBooking = useCallback(async (data) => {
    setSubmitting(true);
    setError("");
    setSuccess("");
    try {
      const created = await bookingService.create(data);
      setSuccess("Đặt tour thành công.");
      return created;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể tạo đơn đặt tour.");
      throw err;
    } finally {
      setSubmitting(false);
    }
  }, []);

  const cancelBooking = useCallback(async (id) => {
    setSubmitting(true);
    setError("");
    setSuccess("");
    try {
      const updated = await bookingService.cancel(id);
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
    createBooking,
    cancelBooking,
    clearMessages,
  };
}
