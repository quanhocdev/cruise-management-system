import axios from "axios";

const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_BASE_URL}/api`,
  withCredentials: true,
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (
      error.response?.status === 401 &&
      !originalRequest._retry &&
      !originalRequest.url.includes("/auth/login") &&
      !originalRequest.url.includes("/auth/register") &&
      !originalRequest.url.includes("/auth/refresh") // 🟢 Cho phép /auth/me đi qua để auto refresh!
    ) {
      originalRequest._retry = true;

      try {
        // Tự động gọi API Refresh Token ngầm
        await api.post("/auth/refresh");

        // Refresh thành công, gọi lại API ban đầu (ví dụ: /auth/me)
        return api(originalRequest);
      } catch (refreshError) {
        // Nếu Refresh Token cũng đã hết hạn/không hợp lệ -> Mới chuyển về Login
        setUserNullAndRedirect();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export default api;
