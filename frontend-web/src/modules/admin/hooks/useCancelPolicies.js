import { useCallback, useEffect, useState } from "react";

import cancelPolicyService from "../services/cancelPolicyService";

const useCancelPolicies = (policyId, activeOnly = false) => {
  const [cancelPolicies, setCancelPolicies] = useState([]);

  const [loading, setLoading] = useState(false);

  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");

  const [success, setSuccess] = useState("");

  // =====================================================
  // GET ALL
  // =====================================================

  const loadCancelPolicies = useCallback(async () => {
    if (!policyId) {
      setCancelPolicies([]);
      return [];
    }

    setLoading(true);
    setError("");

    try {
      const data = await cancelPolicyService.getCancelPolicies(
        policyId,
        activeOnly,
      );

      setCancelPolicies(data);

      return data;
    } catch (err) {
      console.error("🔥 LOAD CANCEL POLICIES ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể tải các quy tắc hủy.";

      setError(message);

      throw err;
    } finally {
      setLoading(false);
    }
  }, [policyId, activeOnly]);

  // =====================================================
  // CREATE
  // =====================================================

  const createCancelPolicy = useCallback(
    async (data) => {
      if (!policyId) {
        return null;
      }

      setSaving(true);
      setError("");
      setSuccess("");

      try {
        const created = await cancelPolicyService.createCancelPolicy(
          policyId,
          data,
        );

        setCancelPolicies((prev) =>
          [...prev, created].sort((a, b) => b.daysBefore - a.daysBefore),
        );

        setSuccess("Tạo quy tắc hủy thành công.");

        return created;
      } catch (err) {
        console.error("🔥 CREATE CANCEL POLICY ERROR:", err);

        const message =
          err?.response?.data?.message || "Không thể tạo quy tắc hủy.";

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

  const updateCancelPolicy = useCallback(
    async (ruleId, data) => {
      if (!policyId) {
        return null;
      }

      setSaving(true);
      setError("");
      setSuccess("");

      try {
        const updated = await cancelPolicyService.updateCancelPolicy(
          policyId,
          ruleId,
          data,
        );

        setCancelPolicies((prev) =>
          prev
            .map((rule) => (rule.id === ruleId ? updated : rule))
            .sort((a, b) => b.daysBefore - a.daysBefore),
        );

        setSuccess("Cập nhật quy tắc hủy thành công.");

        return updated;
      } catch (err) {
        console.error("🔥 UPDATE CANCEL POLICY ERROR:", err);

        const message =
          err?.response?.data?.message || "Không thể cập nhật quy tắc hủy.";

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

  const deleteCancelPolicy = useCallback(
    async (ruleId) => {
      if (!policyId) {
        return false;
      }

      setSaving(true);
      setError("");
      setSuccess("");

      try {
        await cancelPolicyService.deleteCancelPolicy(policyId, ruleId);

        setCancelPolicies((prev) => prev.filter((rule) => rule.id !== ruleId));

        setSuccess("Xóa quy tắc hủy thành công.");

        return true;
      } catch (err) {
        console.error("🔥 DELETE CANCEL POLICY ERROR:", err);

        const message =
          err?.response?.data?.message || "Không thể xóa quy tắc hủy.";

        setError(message);

        return false;
      } finally {
        setSaving(false);
      }
    },
    [policyId],
  );

  useEffect(() => {
    loadCancelPolicies();
  }, [loadCancelPolicies]);

  return {
    cancelPolicies,

    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadCancelPolicies,
    createCancelPolicy,
    updateCancelPolicy,
    deleteCancelPolicy,
  };
};

export default useCancelPolicies;
