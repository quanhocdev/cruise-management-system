import api from "../../../api/axios";

const roleService = {
  async getAll() {
    return api.get("/admin/roles");
  },
};

export default roleService;
