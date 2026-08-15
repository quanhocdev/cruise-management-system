// src/modules/admin/services/portService.js

import api from "../../../api/axios";

const PORT_BASE_URL = "/admin/ports";

const portService = {
  // =====================================================
  // GET ALL PORTS
  // GET /api/admin/ports
  // =====================================================

  async getPorts(activeOnly = false) {
    const response = await api.get(PORT_BASE_URL, {
      params: {
        activeOnly,
      },
    });

    return response.data;
  },

  // =====================================================
  // GET PORT BY ID
  // GET /api/admin/ports/{id}
  // =====================================================

  async getPortById(id) {
    const response = await api.get(`${PORT_BASE_URL}/${id}`);

    return response.data;
  },

  // =====================================================
  // CREATE PORT
  // POST /api/admin/ports
  // =====================================================

  async createPort(data) {
    const response = await api.post(PORT_BASE_URL, data);

    return response.data;
  },

  // =====================================================
  // UPDATE PORT
  // PATCH /api/admin/ports/{id}
  // =====================================================

  async updatePort(id, data) {
    const response = await api.patch(`${PORT_BASE_URL}/${id}`, data);

    return response.data;
  },

  // =====================================================
  // DELETE / DEACTIVATE PORT
  // DELETE /api/admin/ports/{id}
  // =====================================================

  async deactivatePort(id) {
    await api.delete(`${PORT_BASE_URL}/${id}`);
  },
};

export default portService;
