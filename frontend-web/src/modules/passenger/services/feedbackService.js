import api from "../../../api/axios";

const BASE_URL = "/passengers/feedback";

const feedbackService = {
  /**
   * GET /api/passengers/feedback/invitations
   * Lấy tất cả lời mời đánh giá của tài khoản hiện tại.
   */
  getMyInvitations: async () => {
    const response = await api.get(`${BASE_URL}/invitations`);
    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/passengers/feedback/invitations/pending
   * Lấy các lời mời đang chờ đánh giá.
   */
  getMyPendingInvitations: async () => {
    const response = await api.get(`${BASE_URL}/invitations/pending`);
    return response.data?.data ?? response.data;
  },

  /**
   * POST /api/passengers/feedback/invitations/{id}/submit
   * Gửi đánh giá cho một lời mời.
   *
   * data:
   * {
   *   rating: 1-5,
   *   comment: string
   * }
   */
  submitFeedback: async (invitationId, data) => {
    const response = await api.post(
      `${BASE_URL}/invitations/${invitationId}/submit`,
      data,
    );

    return response.data?.data ?? response.data;
  },

  /**
   * POST /api/passengers/feedback/invitations/{id}/skip
   * Bỏ qua một lời mời đánh giá.
   */
  skipInvitation: async (invitationId) => {
    const response = await api.post(
      `${BASE_URL}/invitations/${invitationId}/skip`,
    );

    return response.data?.data ?? response.data;
  },

  /**
   * GET /api/passengers/feedback/invitations/{id}/feedback
   * Xem đánh giá đã gửi cho một lời mời.
   */
  getMyFeedback: async (invitationId) => {
    const response = await api.get(
      `${BASE_URL}/invitations/${invitationId}/feedback`,
    );

    return response.data?.data ?? response.data;
  },
};

export default feedbackService;
