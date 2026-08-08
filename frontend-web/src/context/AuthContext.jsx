import { createContext, useContext, useState, useEffect } from "react";
import api from "../api/axios";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuthStatus();
  }, []);

  // =========================================================
// KIỂM TRA PHIÊN ĐĂNG NHẬP
// =========================================================
const checkAuthStatus = async () => {
    try {
        /*
         * GET /api/auth/me
         *
         * Browser tự động gửi Cookie nhờ withCredentials: true.
         *
         * Trường hợp 1: Chưa đăng nhập
         *     /auth/me → 401
         *     /auth/refresh → 401
         *     → catch → user = null
         *
         * Trường hợp 2: Access token còn hạn
         *     /auth/me → 200
         *     → lấy thông tin user
         *
         * Trường hợp 3: Access token hết hạn
         *     /auth/me → 401
         *     → Axios interceptor tự gọi /auth/refresh
         *     → tạo access token mới
         *     → gọi lại /auth/me
         *     → 200 → lấy thông tin user
         */
    const res = await api.get("/auth/me");

    if (res.data && res.data.username) {
        setUser({
            username: res.data.username,
            role: res.data.role,
        });
    } else {
        setUser(null);
    }
} catch (err) {
    /*
     * Không xác thực được phiên đăng nhập.
     *
     * Có thể do:
     * - Chưa đăng nhập
     * - Refresh token hết hạn
     * - Refresh token không hợp lệ
     */
    setUser(null);
} finally {
    setLoading(false);
}
  };

  // =========================================================
  // LOGIN
  // =========================================================
  const login = async (username, password) => {
    const res = await api.post("/auth/login", {
      username,
      password,
    });

    /*
     * Auth Service:
     *
     * 1. Tạo accessToken
     * 2. Tạo refreshToken
     * 3. Set HttpOnly Cookie
     *
     * Browser tự lưu Cookie.
     */
    setUser({
      username: res.data.username,
      role: res.data.role,
    });

    return res.data.role;
  };

  // =========================================================
  // REGISTER
  // =========================================================
  const register = async (userData) => {
    return await api.post("/auth/register", userData);
  };

  // =========================================================
  // LOGOUT
  // =========================================================
  const logout = async () => {
    try {
      await api.post("/auth/logout");
    } catch (err) {
      console.error("Lỗi khi đăng xuất:", err);
    } finally {
      setUser(null);
      window.location.href = "/login";
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        register,
        logout,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
