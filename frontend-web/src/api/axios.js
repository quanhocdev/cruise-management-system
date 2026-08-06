import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api", // Base URL trỏ thẳng vào /api
  withCredentials: true, // BẮT BUỘC: Để gửi & nhận HttpOnly Cookie
});

// Interceptor: Tự động refresh token khi nhận lỗi 401
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Chỉ thử refresh nếu gặp 401, chưa thử retry, và KHÔNG PHẢI request login/refresh
    if (
      error.response?.status === 401 &&
      !originalRequest._retry &&
      !originalRequest.url.includes("/auth/login") &&
      !originalRequest.url.includes("/auth/refresh")
    ) {
      originalRequest._retry = true;
      try {
        // Gọi API refresh token (Cookie refreshToken sẽ tự động gửi kèm)
        await api.post("/auth/refresh");

        // Thực hiện lại request ban đầu bị 401
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh token cũng hết hạn -> Chuyển về trang login
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export default api;
