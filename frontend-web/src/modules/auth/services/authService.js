// src/modules/auth/services/authService.js
// (FILE MỚI) API đăng ký / xác thực email / kích hoạt tài khoản.
// Nếu project đã có authService thì gộp 4 hàm này vào đó, đừng tạo trùng.

import api from "../../../api/axios";

const authService = {
  /** POST /auth/register -> { id, email, ... } */
  register: async (payload) => {
    const response = await api.post("/auth/register", payload);
    return response.data;
  },

  /** POST /auth/verify-email */
  verifyEmail: async (userId, otp) => {
    const response = await api.post("/auth/verify-email", { userId, otp });
    return response.data;
  },

  /** POST /auth/activate/verify -> { username } */
  verifyActivationToken: async (token) => {
    const response = await api.post("/auth/activate/verify", { token });
    return response.data;
  },

  /** POST /auth/activate/set-password */
  setActivationPassword: async ({ token, password, confirmPassword }) => {
    const response = await api.post("/auth/activate/set-password", {
      token,
      password,
      confirmPassword,
    });
    return response.data;
  },
};

export default authService;
