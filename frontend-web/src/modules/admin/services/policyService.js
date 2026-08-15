import api from "../../../api/axios";

const POLICY_BASE_URL = "/admin/policies";

const policyService = {
  // =====================================================
  // GET ALL POLICIES
  // GET /api/admin/policies
  // =====================================================
  async getPolicies({ type, status } = {}) {
    const params = {};

    if (type) {
      params.type = type;
    }

    if (status) {
      params.status = status;
    }

    const response = await api.get(POLICY_BASE_URL, {
      params,
    });

    return response.data;
  },

  // =====================================================
  // GET POLICY BY ID
  // GET /api/admin/policies/{id}
  // =====================================================
  async getPolicyById(policyId) {
    const response = await api.get(`${POLICY_BASE_URL}/${policyId}`);

    return response.data;
  },

  // =====================================================
  // CREATE POLICY
  // POST /api/admin/policies
  // =====================================================
  async createPolicy(data) {
    const response = await api.post(POLICY_BASE_URL, data);

    return response.data;
  },

  // =====================================================
  // UPDATE POLICY
  // PATCH /api/admin/policies/{id}
  // =====================================================
  async updatePolicy(policyId, data) {
    const response = await api.patch(`${POLICY_BASE_URL}/${policyId}`, data);

    return response.data;
  },

  // =====================================================
  // DELETE POLICY
  // DELETE /api/admin/policies/{id}
  // =====================================================
  async deletePolicy(policyId) {
    await api.delete(`${POLICY_BASE_URL}/${policyId}`);
  },
};

export default policyService;
