import api from "../../../api/axios";

const adminProfileService = {
  // Lấy hồ sơ theo UUID tài khoản
  getByUserId(userId) {
    return api.get(`/admin/profiles/${userId}`);
  },

  // Tạo hồ sơ cho tài khoản chưa có
  create(userId, data) {
    return api.post(`/admin/profiles/${userId}`, data);
  },

  // Cập nhật hồ sơ
  update(userId, data) {
    return api.patch(`/admin/profiles/${userId}`, data);
  },

  // Xóa hồ sơ
  delete(userId) {
    return api.delete(`/admin/profiles/${userId}`);
  },

  // Tải ảnh đại diện lên
  uploadAvatar(userId, file) {
    const formData = new FormData();

    // Phải trùng với @RequestParam("avatar") ở backend
    formData.append("avatar", file);

    return api.post(`/admin/profiles/${userId}/avatar`, formData);
  },

  // Xóa ảnh đại diện
  deleteAvatar(userId) {
    return api.delete(`/admin/profiles/${userId}/avatar`);
  },
};

export default adminProfileService;
