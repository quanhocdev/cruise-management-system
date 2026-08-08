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
       * Browser tự động gửi accessToken Cookie
       * nhờ withCredentials: true.
       *
       * Nếu chưa đăng nhập:
       *     /auth/me → 401
       *     Axios KHÔNG gọi /auth/refresh
       *     → user = null
       *
       * Nếu đã đăng nhập:
       *     /auth/me → 200
       *     → lấy thông tin user.
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
       * 401 ở /auth/me là bình thường khi chưa đăng nhập.
       *
       * Không cần gọi refresh ở đây.
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
