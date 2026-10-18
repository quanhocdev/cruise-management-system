import { useState, useEffect, useCallback } from "react";
import passengerBookingService from "../services/passengerBookingService";
import passengerService from "../services/passengerService";

export default function useBookingDetail(bookingId) {
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [cancelling, setCancelling] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // silent = true: tải lại ngầm, không bật spinner toàn trang
  const fetchBooking = useCallback(
    async ({ silent = false } = {}) => {
      if (!silent) setLoading(true);
      setError("");
      try {
        const data = await passengerBookingService.getBooking(bookingId);
        setBooking(data);
      } catch (err) {
        setError(
          err.response?.data?.message || "Không thể tải chi tiết booking.",
        );
      } finally {
        if (!silent) setLoading(false);
      }
    },
    [bookingId],
  );

  useEffect(() => {
    fetchBooking();
  }, [fetchBooking]);

  const cancelBooking = async () => {
    if (!booking) return false;
    setCancelling(true);
    setError("");
    setSuccess("");
    try {
      const updated = await passengerBookingService.cancelBooking(booking.id);
      setBooking(updated);
      setSuccess("Hủy đơn hàng thành công.");
      return true;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể hủy đơn hàng.");
      return false;
    } finally {
      setCancelling(false);
    }
  };

  // Ném lỗi ra ngoài để modal tự hiển thị lỗi
  const savePassenger = async (passenger) => {
    setError("");
    setSuccess("");
    await passengerService.update(passenger.id, passenger);
    setSuccess("Cập nhật thông tin hành khách thành công.");
    await fetchBooking({ silent: true });
  };

  return {
    booking,
    loading,
    cancelling,
    error,
    success,
    cancelBooking,
    savePassenger,
  };
}
