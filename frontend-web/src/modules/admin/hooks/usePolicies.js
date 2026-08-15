import { useCallback, useEffect, useRef, useState } from "react";

import policyService from "../services/policyService";

const usePolicies = () => {
  const [policies, setPolicies] = useState([]);

  // Loading lần đầu
  const [loading, setLoading] = useState(true);

  // Loading riêng cho filter
  // Không làm biến mất bảng hiện tại
  const [filterLoading, setFilterLoading] = useState(false);

  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // Dùng để tránh request cũ ghi đè request mới
  const requestIdRef = useRef(0);

  // =====================================================
  // LOAD POLICIES
  // =====================================================

  const loadPolicies = useCallback(async (filters = {}, options = {}) => {
    const { isFilter = false } = options;

    const requestId = ++requestIdRef.current;

    if (isFilter) {
      setFilterLoading(true);
    } else {
      setLoading(true);
    }

    setError("");

    try {
      const data = await policyService.getPolicies(filters);

      // Chỉ nhận kết quả của request mới nhất
      if (requestId === requestIdRef.current) {
        setPolicies(data);
      }

      return data;
    } catch (err) {
      console.error("🔥 LOAD POLICIES ERROR:", err);

      // Chỉ hiển thị lỗi của request mới nhất
      if (requestId === requestIdRef.current) {
        const message =
          err?.response?.data?.message || "Không thể tải danh sách chính sách.";

        setError(message);
      }

      return null;
    } finally {
      if (requestId === requestIdRef.current) {
        if (isFilter) {
          setFilterLoading(false);
        } else {
          setLoading(false);
        }
      }
    }
  }, []);

  // =====================================================
  // CREATE
  // =====================================================

  const createPolicy = useCallback(async (data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const created = await policyService.createPolicy(data);

      setPolicies((prev) => [created, ...prev]);

      setSuccess("Tạo chính sách thành công.");

      return created;
    } catch (err) {
      console.error("🔥 CREATE POLICY ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể tạo chính sách.";

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // UPDATE
  // =====================================================

  const updatePolicy = useCallback(async (policyId, data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await policyService.updatePolicy(policyId, data);

      setPolicies((prev) =>
        prev.map((policy) => (policy.id === policyId ? updated : policy)),
      );

      setSuccess("Cập nhật chính sách thành công.");

      return updated;
    } catch (err) {
      console.error("🔥 UPDATE POLICY ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể cập nhật chính sách.";

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // DELETE
  // =====================================================

  const deletePolicy = useCallback(async (policyId) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await policyService.deletePolicy(policyId);

      setPolicies((prev) => prev.filter((policy) => policy.id !== policyId));

      setSuccess("Xóa chính sách thành công.");

      return true;
    } catch (err) {
      console.error("🔥 DELETE POLICY ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể xóa chính sách.";

      setError(message);

      return false;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadPolicies();
  }, [loadPolicies]);

  return {
    policies,

    loading,
    filterLoading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadPolicies,

    createPolicy,
    updatePolicy,
    deletePolicy,
  };
};

export default usePolicies;
