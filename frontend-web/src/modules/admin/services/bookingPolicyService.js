import api from "../../../api/axios";

const getBaseUrl = (policyId) => `/admin/policies/${policyId}/booking-rules`;

const bookingPolicyService = {
  // =====================================================
  // GET ALL BOOKING RULES
  // GET /api/admin/policies/{policyId}/booking-rules
  // =====================================================
  async getBookingPolicies(policyId, activeOnly = false) {
    const response = await api.get(getBaseUrl(policyId), {
      params: {
        activeOnly,
      },
    });

    return response.data;
  },

  // =====================================================
  // CREATE BOOKING RULE
  // POST /api/admin/policies/{policyId}/booking-rules
  // =====================================================
  async createBookingPolicy(policyId, data) {
    const response = await api.post(getBaseUrl(policyId), data);

    return response.data;
  },

  // =====================================================
  // UPDATE BOOKING RULE
  // PATCH /api/admin/policies/{policyId}/booking-rules/{ruleId}
  // =====================================================
  async updateBookingPolicy(policyId, ruleId, data) {
    const response = await api.patch(`${getBaseUrl(policyId)}/${ruleId}`, data);

    return response.data;
  },

  // =====================================================
  // DELETE BOOKING RULE
  // DELETE /api/admin/policies/{policyId}/booking-rules/{ruleId}
  // =====================================================
  async deleteBookingPolicy(policyId, ruleId) {
    await api.delete(`${getBaseUrl(policyId)}/${ruleId}`);
  },
};

export default bookingPolicyService;
