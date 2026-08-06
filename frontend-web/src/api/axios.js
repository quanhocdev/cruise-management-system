// src/api/axios.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080", // URL Spring Boot Backend
  withCredentials: true, // BẮT BUỘC: Cho phép gửi & nhận Cookie
});

// Interceptor: Xử lý tự động khi Access Token hết hạn (401) -> Gọi API Refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Nếu bị 401 và chưa thử refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        // Gọi Endpoint Refresh Token (Spring Boot sẽ đọc Cookie refreshToken và set lại Cookie accessToken mới)
        await axios.post(
          "http://localhost:8080/api/auth/refresh",
          {},
          { withCredentials: true },
        );
        return api(originalRequest); // Thực hiện lại request ban đầu
      } catch (refreshError) {
        // Refresh token cũng hết hạn -> Chuyển về trang đăng nhập
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  },
);

export default api;
