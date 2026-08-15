import api from "../../../api/axios";

const getBaseUrl = (policyId) => `/admin/policies/${policyId}/cancel-rules`;

const cancelPolicyService = {
  // =====================================================
  // GET ALL CANCEL RULES
  // GET /api/admin/policies/{policyId}/cancel-rules
  // =====================================================
  async getCancelPolicies(policyId, activeOnly = false) {
    const response = await api.get(getBaseUrl(policyId), {
      params: {
        activeOnly,
      },
    });

    return response.data;
  },

  // =====================================================
  // CREATE CANCEL RULE
  // POST /api/admin/policies/{policyId}/cancel-rules
  // =====================================================
  async createCancelPolicy(policyId, data) {
    const response = await api.post(getBaseUrl(policyId), data);

    return response.data;
  },

  // =====================================================
  // UPDATE CANCEL RULE
  // PATCH /api/admin/policies/{policyId}/cancel-rules/{ruleId}
  // =====================================================
  async updateCancelPolicy(policyId, ruleId, data) {
    const response = await api.patch(`${getBaseUrl(policyId)}/${ruleId}`, data);

    return response.data;
  },

  // =====================================================
  // DELETE CANCEL RULE
  // DELETE /api/admin/policies/{policyId}/cancel-rules/{ruleId}
  // =====================================================
  async deleteCancelPolicy(policyId, ruleId) {
    await api.delete(`${getBaseUrl(policyId)}/${ruleId}`);
  },
};

export default cancelPolicyService;
