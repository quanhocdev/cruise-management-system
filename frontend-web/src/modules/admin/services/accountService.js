import api from "../../../api/axios";

const accountService = {
  async getAll() {
    return api.get("/admin/staff");
  },

  async getById(id) {
    return api.get(`/admin/staff/${id}`);
  },

  async create(requestData) {
    return api.post("/admin/staff", requestData);
  },

  async update(id, requestData) {
    return api.patch(`/admin/staff/${id}`, requestData);
  },

  async deactivate(id) {
    return api.delete(`/admin/staff/${id}`);
  },
};

export default accountService;
