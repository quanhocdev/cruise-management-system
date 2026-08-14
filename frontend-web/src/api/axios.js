import axios from "axios";

const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_BASE_URL}/api`,
  withCredentials: true,
});

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error);
    } else {
      resolve();
    }
  });

  failedQueue = [];
};

api.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config;

    if (!originalRequest) {
      return Promise.reject(error);
    }

    const url = originalRequest.url || "";

    // =====================================================
    // REFRESH REQUEST BỊ LỖI
    // =====================================================

    if (url.includes("/auth/refresh")) {
      isRefreshing = false;
      processQueue(error);

      return Promise.reject(error);
    }

    // =====================================================
    // AUTH ENDPOINTS
    // =====================================================

    if (
      url.includes("/auth/login") ||
      url.includes("/auth/register") ||
      url.includes("/auth/activate") ||
      url.includes("/auth/verify-otp") ||
      url.includes("/auth/resend-otp")
    ) {
      return Promise.reject(error);
    }

    // =====================================================
    // KHÔNG PHẢI 401
    // =====================================================

    if (error.response?.status !== 401 || originalRequest._retry) {
      return Promise.reject(error);
    }

    // =====================================================
    // ĐANG REFRESH
    // Cho request này chờ
    // =====================================================

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        failedQueue.push({
          resolve: () => {
            resolve(api(originalRequest));
          },
          reject,
        });
      });
    }

    // =====================================================
    // REQUEST ĐẦU TIÊN PHÁT HIỆN ACCESS TOKEN HẾT HẠN
    // =====================================================

    originalRequest._retry = true;
    isRefreshing = true;

    try {
      await api.post("/auth/refresh");

      // Refresh thành công
      isRefreshing = false;

      processQueue(null);

      // Retry request ban đầu
      return api(originalRequest);
    } catch (refreshError) {
      isRefreshing = false;

      processQueue(refreshError);

      return Promise.reject(refreshError);
    }
  },
);

export default api;
