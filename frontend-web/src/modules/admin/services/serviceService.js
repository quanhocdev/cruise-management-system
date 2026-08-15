// src/modules/admin/services/serviceService.js
// src/modules/admin/services/serviceService.js

import api from "../../../api/axios";

const SERVICE_BASE_URL = "/admin/services";

const serviceService = {
  // =====================================================
  // GET SERVICES
  // =====================================================

  async getServices(activeOnly = false) {
    const response = await api.get(SERVICE_BASE_URL, {
      params: {
        activeOnly,
      },
    });

    return response.data;
  },

  // =====================================================
  // GET SERVICE BY ID
  // =====================================================

  async getServiceById(serviceId) {
    const response = await api.get(`${SERVICE_BASE_URL}/${serviceId}`);

    return response.data;
  },

  // =====================================================
  // CREATE SERVICE
  // =====================================================

  async createService(data) {
    const response = await api.post(SERVICE_BASE_URL, data);

    return response.data;
  },

  // =====================================================
  // UPDATE SERVICE
  // =====================================================

  async updateService(serviceId, data) {
    const response = await api.patch(`${SERVICE_BASE_URL}/${serviceId}`, data);

    return response.data;
  },

  // =====================================================
  // DELETE SERVICE
  // =====================================================

  async deleteService(serviceId) {
    const response = await api.delete(`${SERVICE_BASE_URL}/${serviceId}`);

    return response.data;
  },
};

export default serviceService;
