import { useState } from "react";
import bookingRefundService from "../services/bookingRefundService";

export default function useBookingRefunds() {
  const [preview, setPreview] = useState(null);
  const [latestRefund, setLatestRefund] = useState(null);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const clearMessages = () => {
    setError("");
    setSuccess("");
  };

  const loadRefundPreview = async (bookingId) => {
    setLoading(true);
    setError("");
    setPreview(null);

    try {
      const data = await bookingRefundService.preview(bookingId);

      setPreview(data);
      return data;
    } catch (err) {
      setError(
        err.response?.data?.message || "Không thể tính toán số tiền hoàn trả.",
      );
      return null;
    } finally {
      setLoading(false);
    }
  };

  const createRefund = async (bookingId, request) => {
    setSubmitting(true);
    clearMessages();

    try {
      const data = await bookingRefundService.create(bookingId, request);

      setLatestRefund(data);
      setSuccess("Gửi yêu cầu hoàn tiền thành công.");
      setPreview(null);

      return data;
    } catch (err) {
      setError(
        err.response?.data?.message || "Không thể gửi yêu cầu hoàn tiền.",
      );
      return null;
    } finally {
      setSubmitting(false);
    }
  };

  const loadLatestRefund = async (bookingId) => {
    try {
      const data = await bookingRefundService.getLatest(bookingId);
      setLatestRefund(data);
      return data;
    } catch (err) {
      // Booking chưa từng có yêu cầu hoàn tiền.
      if (err.response?.status === 404) {
        setLatestRefund(null);
        return null;
      }

      setError(
        err.response?.data?.message ||
          "Không thể tải thông tin yêu cầu hoàn tiền.",
      );
      return null;
    }
  };

  const clearPreview = () => {
    setPreview(null);
    setError("");
  };

  return {
    preview,
    latestRefund,
    loading,
    submitting,
    error,
    success,
    loadRefundPreview,
    createRefund,
    loadLatestRefund,
    clearPreview,
    clearMessages,
  };
}
