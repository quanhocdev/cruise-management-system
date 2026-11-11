import { useCallback, useMemo, useState } from "react";
import feedbackService from "../services/feedbackService";

const FILTER_ALL = "ALL";
const FILTER_PENDING = "PENDING";
const FILTER_PROCESSED = "PROCESSED";

export default function useFeedback() {
  // =====================================================
  // 1. STATES
  // =====================================================

  const [invitations, setInvitations] = useState([]);
  const [currentFeedback, setCurrentFeedback] = useState(null);

  const [filter, setFilter] = useState(FILTER_ALL);

  const [loading, setLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [skipping, setSkipping] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // 2. HELPERS
  // =====================================================

  const extractList = (data) => {
    if (Array.isArray(data)) {
      return data;
    }

    return data?.content || data?.data || [];
  };

  const getErrorMessage = (err, fallback) => {
    return (
      err.response?.data?.message ||
      err.response?.data?.error ||
      err.message ||
      fallback
    );
  };

  const isPendingInvitation = (invitation) => {
    return invitation?.status === "PENDING";
  };

  // =====================================================
  // 3. LOAD ALL INVITATIONS
  // =====================================================

  const loadMyInvitations = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await feedbackService.getMyInvitations();

      const list = extractList(data);

      setInvitations(list);

      return list;
    } catch (err) {
      setError(
        getErrorMessage(err, "Không thể tải danh sách lời mời đánh giá."),
      );

      setInvitations([]);

      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // 4. LOAD PENDING INVITATIONS
  // =====================================================

  const loadMyPendingInvitations = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await feedbackService.getMyPendingInvitations();

      const list = extractList(data);

      return list;
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Không thể tải danh sách lời mời đang chờ đánh giá.",
        ),
      );

      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // 5. FILTER INVITATIONS
  // =====================================================

  const filteredInvitations = useMemo(() => {
    switch (filter) {
      case FILTER_PENDING:
        return invitations.filter(isPendingInvitation);

      case FILTER_PROCESSED:
        return invitations.filter(
          (invitation) => !isPendingInvitation(invitation),
        );

      case FILTER_ALL:
      default:
        return invitations;
    }
  }, [invitations, filter]);

  const pendingCount = useMemo(() => {
    return invitations.filter(isPendingInvitation).length;
  }, [invitations]);

  const processedCount = useMemo(() => {
    return invitations.length - pendingCount;
  }, [invitations.length, pendingCount]);

  // =====================================================
  // 6. SUBMIT FEEDBACK
  // =====================================================

  const submitFeedback = useCallback(
    async (invitationId, data) => {
      if (!invitationId) {
        throw new Error("Không tìm thấy lời mời đánh giá.");
      }

      if (!data || !Number.isInteger(data.rating)) {
        throw new Error("Vui lòng chọn số sao đánh giá.");
      }

      if (data.rating < 1 || data.rating > 5) {
        throw new Error("Số sao đánh giá phải từ 1 đến 5.");
      }

      if ((data.comment || "").length > 2000) {
        throw new Error("Bình luận không được vượt quá 2000 ký tự.");
      }

      setSubmitting(true);
      setError("");
      setSuccess("");

      try {
        const result = await feedbackService.submitFeedback(invitationId, {
          rating: data.rating,
          comment: data.comment?.trim() || null,
        });

        // Cập nhật lại danh sách sau khi gửi đánh giá thành công.
        await loadMyInvitations();

        setCurrentFeedback(result);
        setSuccess("Gửi đánh giá thành công.");

        return result;
      } catch (err) {
        setError(
          getErrorMessage(err, "Không thể gửi đánh giá. Vui lòng thử lại."),
        );

        throw err;
      } finally {
        setSubmitting(false);
      }
    },
    [loadMyInvitations],
  );

  // =====================================================
  // 7. SKIP INVITATION
  // =====================================================

  const skipInvitation = useCallback(
    async (invitationId) => {
      if (!invitationId) {
        throw new Error("Không tìm thấy lời mời đánh giá.");
      }

      setSkipping(true);
      setError("");
      setSuccess("");

      try {
        const result = await feedbackService.skipInvitation(invitationId);

        // Cập nhật lại trạng thái invitation từ backend.
        await loadMyInvitations();

        setCurrentFeedback(null);
        setSuccess("Đã bỏ qua lời mời đánh giá.");

        return result;
      } catch (err) {
        setError(getErrorMessage(err, "Không thể bỏ qua lời mời đánh giá."));

        throw err;
      } finally {
        setSkipping(false);
      }
    },
    [loadMyInvitations],
  );

  // =====================================================
  // 8. GET SUBMITTED FEEDBACK DETAIL
  // =====================================================

  const loadMyFeedback = useCallback(async (invitationId) => {
    if (!invitationId) {
      setCurrentFeedback(null);
      return null;
    }

    setDetailLoading(true);
    setError("");

    try {
      const data = await feedbackService.getMyFeedback(invitationId);

      setCurrentFeedback(data);

      return data;
    } catch (err) {
      setError(getErrorMessage(err, "Không thể tải nội dung đánh giá."));

      setCurrentFeedback(null);

      throw err;
    } finally {
      setDetailLoading(false);
    }
  }, []);

  // =====================================================
  // 9. CLEAR MESSAGES
  // =====================================================

  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  const clearCurrentFeedback = useCallback(() => {
    setCurrentFeedback(null);
  }, []);

  // =====================================================
  // 10. RETURN
  // =====================================================

  return {
    invitations,
    filteredInvitations,
    currentFeedback,

    filter,
    setFilter,

    loading,
    detailLoading,
    submitting,
    skipping,

    error,
    success,

    pendingCount,
    processedCount,
    totalCount: invitations.length,

    loadMyInvitations,
    loadMyPendingInvitations,

    submitFeedback,
    skipInvitation,
    loadMyFeedback,

    clearMessages,
    clearCurrentFeedback,
  };
}

export { FILTER_ALL, FILTER_PENDING, FILTER_PROCESSED };
