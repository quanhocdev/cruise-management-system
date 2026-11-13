// src/modules/admin/services/policyRefundRuleService.js

import api from "../../../api/axios";

const getBaseUrl = (policyId) => `/admin/policies/${policyId}/refund-rules`;

const policyRefundRuleService = {
  // GET /api/admin/policies/{policyId}/refund-rules
  async getByPolicyId(policyId) {
    const response = await api.get(getBaseUrl(policyId));
    return response.data;
  },

  // POST /api/admin/policies/{policyId}/refund-rules
  async create(policyId, data) {
    const response = await api.post(getBaseUrl(policyId), data);
    return response.data;
  },

  // PATCH /api/admin/policies/{policyId}/refund-rules/{ruleId}
  async update(policyId, ruleId, data) {
    const response = await api.patch(`${getBaseUrl(policyId)}/${ruleId}`, data);
    return response.data;
  },

  // DELETE /api/admin/policies/{policyId}/refund-rules/{ruleId}
  async delete(policyId, ruleId) {
    await api.delete(`${getBaseUrl(policyId)}/${ruleId}`);
  },
};

export default policyRefundRuleService;
