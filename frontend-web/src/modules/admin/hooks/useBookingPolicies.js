import { useCallback, useEffect, useState } from "react";

import bookingPolicyService from "../services/bookingPolicyService";

const useBookingPolicies = (policyId, activeOnly = false) => {
  const [bookingPolicies, setBookingPolicies] = useState([]);

  const [loading, setLoading] = useState(false);

  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");

  const [success, setSuccess] = useState("");

  // =====================================================
  // GET ALL
  // =====================================================

  const loadBookingPolicies = useCallback(async () => {
    if (!policyId) {
      setBookingPolicies([]);
      return [];
    }

    setLoading(true);
    setError("");

    try {
      const data = await bookingPolicyService.getBookingPolicies(
        policyId,
        activeOnly,
      );

      setBookingPolicies(data);

      return data;
    } catch (err) {
      console.error("🔥 LOAD BOOKING POLICIES ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể tải các quy tắc đặt tour.";

      setError(message);

      throw err;
    } finally {
      setLoading(false);
    }
  }, [policyId, activeOnly]);

  // =====================================================
  // CREATE
  // =====================================================

  const createBookingPolicy = useCallback(
    async (data) => {
      if (!policyId) {
        return null;
      }

      setSaving(true);
      setError("");
      setSuccess("");

      try {
        const created = await bookingPolicyService.createBookingPolicy(
          policyId,
          data,
        );

        setBookingPolicies((prev) =>
          [...prev, created].sort(
            (a, b) => b.daysBeforeDeparture - a.daysBeforeDeparture,
          ),
        );

        setSuccess("Tạo quy tắc đặt tour thành công.");

        return created;
      } catch (err) {
        console.error("🔥 CREATE BOOKING POLICY ERROR:", err);

        const message =
          err?.response?.data?.message || "Không thể tạo quy tắc đặt tour.";

        setError(message);

        return null;
      } finally {
        setSaving(false);
      }
    },
    [policyId],
  );

  // =====================================================
  // UPDATE
  // =====================================================

  const updateBookingPolicy = useCallback(
    async (ruleId, data) => {
      if (!policyId) {
        return null;
      }

      setSaving(true);
      setError("");
      setSuccess("");

      try {
        const updated = await bookingPolicyService.updateBookingPolicy(
          policyId,
          ruleId,
          data,
        );

        setBookingPolicies((prev) =>
          prev
            .map((rule) => (rule.id === ruleId ? updated : rule))
            .sort((a, b) => b.daysBeforeDeparture - a.daysBeforeDeparture),
        );

        setSuccess("Cập nhật quy tắc đặt tour thành công.");

        return updated;
      } catch (err) {
        console.error("🔥 UPDATE BOOKING POLICY ERROR:", err);

        const message =
          err?.response?.data?.message ||
          "Không thể cập nhật quy tắc đặt tour.";

        setError(message);

        return null;
      } finally {
        setSaving(false);
      }
    },
    [policyId],
  );

  // =====================================================
  // DELETE
  // =====================================================

  const deleteBookingPolicy = useCallback(
    async (ruleId) => {
      if (!policyId) {
        return false;
      }

      setSaving(true);
      setError("");
      setSuccess("");

      try {
        await bookingPolicyService.deleteBookingPolicy(policyId, ruleId);

        setBookingPolicies((prev) => prev.filter((rule) => rule.id !== ruleId));

        setSuccess("Xóa quy tắc đặt tour thành công.");

        return true;
      } catch (err) {
        console.error("🔥 DELETE BOOKING POLICY ERROR:", err);

        const message =
          err?.response?.data?.message || "Không thể xóa quy tắc đặt tour.";

        setError(message);

        return false;
      } finally {
        setSaving(false);
      }
    },
    [policyId],
  );

  useEffect(() => {
    loadBookingPolicies();
  }, [loadBookingPolicies]);

  return {
    bookingPolicies,

    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadBookingPolicies,
    createBookingPolicy,
    updateBookingPolicy,
    deleteBookingPolicy,
  };
};

export default useBookingPolicies;
