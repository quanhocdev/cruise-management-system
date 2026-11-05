import api from "../../../api/axios";

const accountService = {
  // Lấy toàn bộ tài khoản, bao gồm ADMIN, nhân viên, quản lý và PASSENGER
  async getAll() {
    return api.get("/admin/accounts");
  },

  async getById(id) {
    return api.get(`/admin/accounts/${id}`);
  },

  // Tạo tài khoản nhân viên: giữ nguyên API cũ
  async create(requestData) {
    return api.post("/admin/staff", requestData);
  },

  // Cập nhật username và email, không thay đổi role
  async update(id, requestData) {
    return api.patch(`/admin/accounts/${id}`, {
      username: requestData.username,
      email: requestData.email,
    });
  },

  // Vô hiệu hóa tài khoản bằng API cập nhật trạng thái
  async deactivate(id) {
    return api.patch(`/admin/accounts/${id}/status`, {
      status: "INACTIVE",
    });
  },
};

export default accountService;
