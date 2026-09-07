// src/modules/passenger/hooks/usePayment.js
import { useState, useCallback } from "react";
import paymentService from "../services/paymentService";

export const usePayment = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchPaymentUrl = useCallback(async (bookingId) => {
    setLoading(true);
    setError(null);
    try {
      const data = await paymentService.getPaymentByBookingId(bookingId);
      return data?.paymentUrl || data;
    } catch (err) {
      setError(err.response?.data?.message || err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    fetchPaymentUrl,
    loading,
    error,
  };
};
