import axios from "axios";

const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_BASE_URL}/api`,
  withCredentials: true,
});

// Cờ kiểm tra xem có đang thực hiện refresh token hay không
let isRefreshing = false;

api.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config;

    // =====================================================
    // 1. XỬ LÝ KHI CHÍNH REQUEST /auth/refresh BỊ LỖI (400, 401,...)
    // =====================================================
    if (originalRequest?.url?.includes("/auth/refresh")) {
      isRefreshing = false;

      // Tránh lặp reload nếu người dùng đang ở sẵn trang /login
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }

      return Promise.reject(error);
    }

    // =====================================================
    // 2. XỬ LÝ LỖI 401 DÀNH CHO CÁC REQUEST KHÁC
    // =====================================================
    if (
      error.response?.status === 401 &&
      originalRequest &&
      !originalRequest._retry &&
      !originalRequest.url.includes("/auth/login") &&
      !originalRequest.url.includes("/auth/register") &&
      !originalRequest.url.includes("/auth/activate/")
    ) {
      // Nếu đang có 1 request refresh chạy dở, không gọi thêm nữa
      if (isRefreshing) {
        return Promise.reject(error);
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // Refresh token nằm trong HttpOnly Cookie
        await api.post("/auth/refresh");

        isRefreshing = false;

        // Refresh thành công → gọi lại request ban đầu
        return api(originalRequest);
      } catch (refreshError) {
        isRefreshing = false;

        if (window.location.pathname !== "/login") {
          window.location.href = "/login";
        }

        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export default api;
