// src/modules/admin/hooks/usePolicyRefundRules.js

import { useCallback, useEffect, useRef, useState } from "react";

import policyRefundRuleService from "../services/policyRefundRuleService";
import { sortRules } from "../utils/refundRuleUtils";

const usePolicyRefundRules = (policyId) => {
  const [rules, setRules] = useState([]);

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // Tránh request cũ ghi đè request mới (khi đổi policy nhanh)
  const requestIdRef = useRef(0);

  // =====================================================
  // LOAD
  // =====================================================

  const loadRules = useCallback(async () => {
    if (!policyId) {
      setRules([]);
      return null;
    }

    const requestId = ++requestIdRef.current;

    setLoading(true);
    setError("");

    try {
      const data = await policyRefundRuleService.getByPolicyId(policyId);

      if (requestId === requestIdRef.current) {
        setRules(sortRules(data));
      }

      return data;
    } catch (err) {
      console.error("🔥 LOAD REFUND RULES ERROR:", err);

      if (requestId === requestIdRef.current) {
        setError(
          err?.response?.data?.message || "Không thể tải luật hoàn tiền.",
        );
      }

      return null;
    } finally {
      if (requestId === requestIdRef.current) {
        setLoading(false);
      }
    }
  }, [policyId]);

  useEffect(() => {
    setSuccess("");
    setRules([]);
    loadRules();
  }, [loadRules]);

  // =====================================================
  // CREATE
  // =====================================================

  const createRule = useCallback(
    async (data) => {
      setSaving(true);
      setError("");
      setSuccess("");

      try {
        const created = await policyRefundRuleService.create(policyId, data);

        setRules((prev) => sortRules([...prev, created]));
        setSuccess("Thêm luật hoàn tiền thành công.");

        return created;
      } catch (err) {
        console.error("🔥 CREATE REFUND RULE ERROR:", err);

        setError(
          err?.response?.data?.message || "Không thể thêm luật hoàn tiền.",
        );

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

  const updateRule = useCallback(
    async (ruleId, data) => {
      setSaving(true);
      setError("");
      setSuccess("");

      try {
        const updated = await policyRefundRuleService.update(
          policyId,
          ruleId,
          data,
        );

        setRules((prev) =>
          sortRules(prev.map((rule) => (rule.id === ruleId ? updated : rule))),
        );
        setSuccess("Cập nhật luật hoàn tiền thành công.");

        return updated;
      } catch (err) {
        console.error("🔥 UPDATE REFUND RULE ERROR:", err);

        setError(
          err?.response?.data?.message || "Không thể cập nhật luật hoàn tiền.",
        );

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

  const deleteRule = useCallback(
    async (ruleId) => {
      setSaving(true);
      setError("");
      setSuccess("");

      try {
        await policyRefundRuleService.delete(policyId, ruleId);

        setRules((prev) => prev.filter((rule) => rule.id !== ruleId));
        setSuccess("Xóa luật hoàn tiền thành công.");

        return true;
      } catch (err) {
        console.error("🔥 DELETE REFUND RULE ERROR:", err);

        setError(
          err?.response?.data?.message || "Không thể xóa luật hoàn tiền.",
        );

        return false;
      } finally {
        setSaving(false);
      }
    },
    [policyId],
  );

  return {
    rules,

    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadRules,

    createRule,
    updateRule,
    deleteRule,
  };
};

export default usePolicyRefundRules;
