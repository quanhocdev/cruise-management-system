import { useCallback, useState } from "react";
import passengerService from "../services/passengerService";

export default function usePassengers() {
  const [passengers, setPassengers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadPassengers = useCallback(async (userId) => {
    if (!userId) return;
    setLoading(true);
    setError("");
    try {
      const data = await passengerService.getAll(userId);
      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setPassengers(list);
    } catch (err) {
      setError(
        err.response?.data?.message || "Không thể tải danh sách hành khách.",
      );
      setPassengers([]);
    } finally {
      setLoading(false);
    }
  }, []);

  const createPassenger = useCallback(async (data, userId) => {
    setSubmitting(true);
    setError("");
    setSuccess("");
    try {
      const created = await passengerService.create(data, userId);
      setSuccess("Thêm hành khách thành công.");
      return created;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể thêm hành khách.");
      throw err;
    } finally {
      setSubmitting(false);
    }
  }, []);

  const updatePassenger = useCallback(async (id, data) => {
    setSubmitting(true);
    setError("");
    setSuccess("");
    try {
      const updated = await passengerService.update(id, data);
      setSuccess("Cập nhật thông tin hành khách thành công.");
      return updated;
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Không thể cập nhật thông tin hành khách.",
      );
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
    passengers,
    loading,
    submitting,
    error,
    success,
    loadPassengers,
    createPassenger,
    updatePassenger,
    clearMessages,
  };
}
