import { useCallback, useEffect, useState } from "react";

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

/*
 * =====================================================
 * POLICY HOOK
 *
 * Manage:
 * - Policy
 * - Booking Policy Rules
 * - Cancel Policy Rules
 *
 * API:
 * /api/admin/policies
 * /api/admin/policies/{policyId}/booking-rules
 * /api/admin/policies/{policyId}/cancel-rules
 * =====================================================
 */

export default function usePolicy() {
  const [policies, setPolicies] = useState([]);

  const [bookingRules, setBookingRules] = useState([]);
  const [cancelRules, setCancelRules] = useState([]);

  const [loading, setLoading] = useState(false);
  const [rulesLoading, setRulesLoading] = useState(false);

  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  /*
   * =====================================================
   * GET TOKEN
   * =====================================================
   *
   * Nếu project của bạn lưu JWT bằng cookie HttpOnly
   * thì không cần Authorization ở đây.
   *
   * Nếu đang lưu accessToken trong localStorage,
   * hook sẽ tự lấy ra.
   */

  const getAccessToken = () => {
    return (
      localStorage.getItem("accessToken") ||
      localStorage.getItem("access_token") ||
      localStorage.getItem("token") ||
      ""
    );
  };

  /*
   * =====================================================
   * REQUEST HELPER
   * =====================================================
   */

  const request = async (url, options = {}) => {
    const token = getAccessToken();

    const headers = {
      ...(options.body instanceof FormData
        ? {}
        : {
            "Content-Type": "application/json",
          }),
      ...(options.headers || {}),
    };

    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${url}`, {
      ...options,
      headers,
      credentials: "include",
    });

    /*
     * DELETE 204 No Content
     */
    if (response.status === 204) {
      return null;
    }

    const contentType = response.headers.get("content-type");

    let data = null;

    if (contentType && contentType.includes("application/json")) {
      data = await response.json();
    } else {
      const text = await response.text();

      data = text || null;
    }

    if (!response.ok) {
      const message =
        data?.message ||
        data?.error ||
        data?.detail ||
        "Có lỗi xảy ra khi gọi API.";

      throw new Error(message);
    }

    return data;
  };

  /*
   * =====================================================
   * ERROR MESSAGE
   * =====================================================
   */

  const getErrorMessage = (exception) => {
    if (exception instanceof Error) {
      return exception.message;
    }

    return "Có lỗi xảy ra. Vui lòng thử lại.";
  };

  /*
   * =====================================================
   * GET ALL POLICIES
   *
   * GET /api/admin/policies
   *
   * Có thể truyền:
   * type = BOOKING | CANCEL
   * status = ACTIVE | INACTIVE
   * =====================================================
   */

  const fetchPolicies = useCallback(async (type = "", status = "") => {
    setLoading(true);
    setError("");

    try {
      const params = new URLSearchParams();

      if (type) {
        params.append("type", type);
      }

      if (status) {
        params.append("status", status);
      }

      const queryString = params.toString();

      const data = await request(
        `/api/admin/policies${queryString ? `?${queryString}` : ""}`,
      );

      setPolicies(Array.isArray(data) ? data : []);

      return data;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  /*
   * =====================================================
   * GET POLICY BY ID
   * =====================================================
   */

  const getPolicyById = useCallback(async (policyId) => {
    if (!policyId) {
      return null;
    }

    try {
      return await request(`/api/admin/policies/${policyId}`);
    } catch (exception) {
      setError(getErrorMessage(exception));

      return null;
    }
  }, []);

  /*
   * =====================================================
   * CREATE POLICY
   *
   * POST /api/admin/policies
   * =====================================================
   */

  const createPolicy = async (data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const created = await request("/api/admin/policies", {
        method: "POST",
        body: JSON.stringify({
          type: data.type,
          title: data.title,
          content: data.content,
        }),
      });

      setPolicies((previous) => [created, ...previous]);

      setSuccess("Tạo chính sách thành công.");

      return created;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * UPDATE POLICY
   *
   * PATCH /api/admin/policies/{id}
   * =====================================================
   */

  const updatePolicy = async (policyId, data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await request(`/api/admin/policies/${policyId}`, {
        method: "PATCH",
        body: JSON.stringify({
          title: data.title,
          content: data.content,
          status: data.status,
        }),
      });

      setPolicies((previous) =>
        previous.map((policy) => (policy.id === policyId ? updated : policy)),
      );

      setSuccess("Cập nhật chính sách thành công.");

      return updated;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * DELETE POLICY
   *
   * DELETE /api/admin/policies/{id}
   *
   * Lưu ý:
   * Backend hiện tại đang deactivate.
   * Nếu bạn đã đổi backend thành delete thật,
   * hook này đã đúng API.
   * =====================================================
   */

  const deletePolicy = async (policyId) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await request(`/api/admin/policies/${policyId}`, {
        method: "DELETE",
      });

      setPolicies((previous) =>
        previous.filter((policy) => policy.id !== policyId),
      );

      setSuccess("Xóa chính sách thành công.");

      return true;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return false;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * GET BOOKING RULES
   *
   * GET
   * /api/admin/policies/{policyId}/booking-rules
   * =====================================================
   */

  const fetchBookingRules = useCallback(
    async (policyId, activeOnly = false) => {
      if (!policyId) {
        setBookingRules([]);

        return [];
      }

      setRulesLoading(true);
      setError("");

      try {
        const params = new URLSearchParams();

        params.append("activeOnly", String(activeOnly));

        const data = await request(
          `/api/admin/policies/${policyId}/booking-rules?${params.toString()}`,
        );

        setBookingRules(Array.isArray(data) ? data : []);

        return data;
      } catch (exception) {
        const message = getErrorMessage(exception);

        setError(message);

        return null;
      } finally {
        setRulesLoading(false);
      }
    },
    [],
  );

  /*
   * =====================================================
   * GET CANCEL RULES
   *
   * GET
   * /api/admin/policies/{policyId}/cancel-rules
   * =====================================================
   */

  const fetchCancelRules = useCallback(async (policyId, activeOnly = false) => {
    if (!policyId) {
      setCancelRules([]);

      return [];
    }

    setRulesLoading(true);
    setError("");

    try {
      const params = new URLSearchParams();

      params.append("activeOnly", String(activeOnly));

      const data = await request(
        `/api/admin/policies/${policyId}/cancel-rules?${params.toString()}`,
      );

      setCancelRules(Array.isArray(data) ? data : []);

      return data;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return null;
    } finally {
      setRulesLoading(false);
    }
  }, []);

  /*
   * =====================================================
   * CREATE BOOKING RULE
   *
   * POST
   * /api/admin/policies/{policyId}/booking-rules
   * =====================================================
   */

  const createBookingRule = async (policyId, data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const created = await request(
        `/api/admin/policies/${policyId}/booking-rules`,
        {
          method: "POST",
          body: JSON.stringify({
            daysBeforeDeparture: Number(data.daysBeforeDeparture),
            discountPercent: Number(data.discountPercent),
          }),
        },
      );

      setBookingRules((previous) =>
        [...previous, created].sort(
          (a, b) =>
            Number(b.daysBeforeDeparture) - Number(a.daysBeforeDeparture),
        ),
      );

      setSuccess("Thêm chính sách đăng ký thành công.");

      return created;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * UPDATE BOOKING RULE
   *
   * PATCH
   * /api/admin/policies/{policyId}/booking-rules/{ruleId}
   * =====================================================
   */

  const updateBookingRule = async (policyId, ruleId, data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await request(
        `/api/admin/policies/${policyId}/booking-rules/${ruleId}`,
        {
          method: "PATCH",
          body: JSON.stringify({
            daysBeforeDeparture: Number(data.daysBeforeDeparture),
            discountPercent: Number(data.discountPercent),
            status: data.status,
          }),
        },
      );

      setBookingRules((previous) =>
        previous
          .map((rule) => (rule.id === ruleId ? updated : rule))
          .sort(
            (a, b) =>
              Number(b.daysBeforeDeparture) - Number(a.daysBeforeDeparture),
          ),
      );

      setSuccess("Cập nhật chính sách đăng ký thành công.");

      return updated;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * DELETE BOOKING RULE
   *
   * DELETE
   * /api/admin/policies/{policyId}/booking-rules/{ruleId}
   * =====================================================
   */

  const deleteBookingRule = async (policyId, ruleId) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await request(`/api/admin/policies/${policyId}/booking-rules/${ruleId}`, {
        method: "DELETE",
      });

      setBookingRules((previous) =>
        previous.filter((rule) => rule.id !== ruleId),
      );

      setSuccess("Xóa chính sách đăng ký thành công.");

      return true;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return false;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * CREATE CANCEL RULE
   *
   * POST
   * /api/admin/policies/{policyId}/cancel-rules
   * =====================================================
   */

  const createCancelRule = async (policyId, data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const created = await request(
        `/api/admin/policies/${policyId}/cancel-rules`,
        {
          method: "POST",
          body: JSON.stringify({
            daysBefore: Number(data.daysBefore),
            refundPercent: Number(data.refundPercent),
          }),
        },
      );

      setCancelRules((previous) =>
        [...previous, created].sort(
          (a, b) => Number(b.daysBefore) - Number(a.daysBefore),
        ),
      );

      setSuccess("Thêm chính sách hủy hoàn tiền thành công.");

      return created;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * UPDATE CANCEL RULE
   *
   * PATCH
   * /api/admin/policies/{policyId}/cancel-rules/{ruleId}
   * =====================================================
   */

  const updateCancelRule = async (policyId, ruleId, data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await request(
        `/api/admin/policies/${policyId}/cancel-rules/${ruleId}`,
        {
          method: "PATCH",
          body: JSON.stringify({
            daysBefore: Number(data.daysBefore),
            refundPercent: Number(data.refundPercent),
            status: data.status,
          }),
        },
      );

      setCancelRules((previous) =>
        previous
          .map((rule) => (rule.id === ruleId ? updated : rule))
          .sort((a, b) => Number(b.daysBefore) - Number(a.daysBefore)),
      );

      setSuccess("Cập nhật chính sách hủy hoàn tiền thành công.");

      return updated;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * DELETE CANCEL RULE
   *
   * DELETE
   * /api/admin/policies/{policyId}/cancel-rules/{ruleId}
   * =====================================================
   */

  const deleteCancelRule = async (policyId, ruleId) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await request(`/api/admin/policies/${policyId}/cancel-rules/${ruleId}`, {
        method: "DELETE",
      });

      setCancelRules((previous) =>
        previous.filter((rule) => rule.id !== ruleId),
      );

      setSuccess("Xóa chính sách hủy hoàn tiền thành công.");

      return true;
    } catch (exception) {
      const message = getErrorMessage(exception);

      setError(message);

      return false;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * CLEAR MESSAGES
   * =====================================================
   */

  const clearMessages = () => {
    setError("");
    setSuccess("");
  };

  /*
   * =====================================================
   * CLEAR RULES
   * =====================================================
   */

  const clearRules = () => {
    setBookingRules([]);
    setCancelRules([]);
  };

  /*
   * =====================================================
   * INITIAL LOAD
   * =====================================================
   */

  useEffect(() => {
    fetchPolicies();
  }, [fetchPolicies]);

  /*
   * =====================================================
   * RETURN
   * =====================================================
   */

  return {
    /*
     * Policy
     */
    policies,

    fetchPolicies,
    getPolicyById,
    createPolicy,
    updatePolicy,
    deletePolicy,

    /*
     * Booking rules
     */
    bookingRules,
    fetchBookingRules,
    createBookingRule,
    updateBookingRule,
    deleteBookingRule,

    /*
     * Cancel rules
     */
    cancelRules,
    fetchCancelRules,
    createCancelRule,
    updateCancelRule,
    deleteCancelRule,

    /*
     * State
     */
    loading,
    rulesLoading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    clearMessages,
    clearRules,
  };
}
