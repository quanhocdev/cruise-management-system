// src/modules/finance/hooks/useFinanceCheckout.js
import { useCallback, useState } from "react";
import financeService from "../services/financeService";

const getErrorMessage = (error, fallback) =>
  error?.response?.data?.message || fallback;

export const useFinanceCheckout = () => {
  const [currentBookingId, setCurrentBookingId] = useState(null);
  const [bookingCode, setBookingCode] = useState("");
  const [checkoutPreview, setCheckoutPreview] = useState(null);

  const [loadingCheckout, setLoadingCheckout] = useState(false);
  const [processingPayment, setProcessingPayment] = useState(false);
  const [error, setError] = useState("");

  const clearCheckout = useCallback(() => {
    setCurrentBookingId(null);
    setCheckoutPreview(null);
  }, []);

  // Tải preview theo bookingId (dùng cho cả quét QR và tìm tay)
  const loadPreview = useCallback(async (bookingId) => {
    if (!bookingId) return;

    setLoadingCheckout(true);
    setError("");

    try {
      const data = await financeService.getCheckoutPreview(bookingId);
      setCheckoutPreview(data);
      setCurrentBookingId(bookingId);
    } catch (err) {
      console.error("Không thể tải thông tin checkout:", err);
      setCheckoutPreview(null);
      setError(
        getErrorMessage(err, "Không thể tải thông tin thanh toán của booking!"),
      );
    } finally {
      setLoadingCheckout(false);
    }
  }, []);

  // Callback cho WebSocket khi có booking được quét
  const handleScanned = useCallback(
    (notification) => {
      setBookingCode(notification.bookingCode || "");
      setCurrentBookingId(notification.bookingId || null);

      if (notification.bookingId) {
        loadPreview(notification.bookingId);
      }
    },
    [loadPreview],
  );

  // Nhập tay mã booking: tìm trong tour rồi tải preview
  const searchByCode = useCallback(
    async (tourId, code) => {
      const keyword = code?.trim().toLowerCase();
      if (!keyword || !tourId) return;

      setLoadingCheckout(true);
      setError("");
      setCheckoutPreview(null);

      try {
        const bookings = await financeService.getBookingsByTour(tourId);
        const found = bookings.find(
          (b) => b.bookingCode?.toLowerCase() === keyword,
        );

        if (!found) {
          clearCheckout();
          setError("Không tìm thấy mã booking này trong tour hiện tại!");
          return;
        }

        setCurrentBookingId(found.id);
        setBookingCode(found.bookingCode);

        const data = await financeService.getCheckoutPreview(found.id);
        setCheckoutPreview(data);
      } catch (err) {
        console.error("Lỗi tìm kiếm booking:", err);
        clearCheckout();
        setError(
          getErrorMessage(err, "Lỗi khi tìm kiếm hoặc tải thông tin checkout!"),
        );
      } finally {
        setLoadingCheckout(false);
      }
    },
    [clearCheckout],
  );

  // Tạo phiên thanh toán, trả về paymentUrl (hoặc null nếu lỗi)
  const confirmPayment = useCallback(async () => {
    if (!currentBookingId) {
      setError("Chưa có booking để thanh toán!");
      return null;
    }

    setProcessingPayment(true);
    setError("");

    try {
      const response = await financeService.confirmCheckout(currentBookingId);

      if (!response?.paymentUrl) {
        setError("Không nhận được đường dẫn thanh toán VNPay!");
        return null;
      }

      return response.paymentUrl;
    } catch (err) {
      console.error("Lỗi tạo thanh toán:", err);
      setError(
        getErrorMessage(err, "Không thể tạo thanh toán. Vui lòng thử lại!"),
      );
      return null;
    } finally {
      setProcessingPayment(false);
    }
  }, [currentBookingId]);

  // Dùng khi đổi tour
  const reset = useCallback(() => {
    clearCheckout();
    setBookingCode("");
    setError("");
  }, [clearCheckout]);

  return {
    currentBookingId,
    bookingCode,
    setBookingCode,
    checkoutPreview,
    loadingCheckout,
    processingPayment,
    error,
    handleScanned,
    searchByCode,
    confirmPayment,
    reset,
  };
};
