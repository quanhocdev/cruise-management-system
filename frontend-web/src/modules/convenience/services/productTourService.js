// src/modules/convenience/services/productTourService.js
// src/modules/convenience/services/productTourService.js

import api from "../../../api/axios";

const API_URL = "/convenience/product-tours";

export const productTourService = {
  // =====================================================
  // GET CONFIGURABLE
  // =====================================================

  // GET /api/convenience/product-tours/pending-config
  getPendingConfig: async () => {
    const response = await api.get(`${API_URL}/pending-config`);

    return response.data;
  },

  // =====================================================
  // POST CONFIG
  // =====================================================

  // POST /api/convenience/product-tours/{assignmentId}/config
  configure: async (assignmentId, data) => {
    const response = await api.post(`${API_URL}/${assignmentId}/config`, data);

    return response.data;
  },

  // =====================================================
  // PATCH CONFIG
  // =====================================================

  // PATCH /api/convenience/product-tours/{assignmentId}/config
  updateConfig: async (assignmentId, data) => {
    const response = await api.patch(`${API_URL}/${assignmentId}/config`, data);

    return response.data;
  },
};

export default productTourService;
