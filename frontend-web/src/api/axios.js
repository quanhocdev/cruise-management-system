import axios from "axios";

const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_BASE_URL}/api`,
  withCredentials: true,
});

api.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config;

    // =====================================================
    // 401 → thử refresh access token
    // =====================================================
    if (
      error.response?.status === 401 &&
      originalRequest &&
      !originalRequest._retry &&
      !originalRequest.url.includes("/auth/login") &&
      !originalRequest.url.includes("/auth/register") &&
      !originalRequest.url.includes("/auth/refresh") &&
      !originalRequest.url.includes("/auth/me")
    ) {
      originalRequest._retry = true;

      try {
        // Refresh token nằm trong HttpOnly Cookie.
        // Browser tự gửi Cookie nhờ withCredentials: true.
        await api.post("/auth/refresh");

        // Refresh thành công → gọi lại request ban đầu
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh token hết hạn / không hợp lệ
        setUserNullAndRedirect();

        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export default api;
