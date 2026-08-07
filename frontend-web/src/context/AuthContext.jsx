// src/context/AuthContext.jsx
import { createContext, useContext, useState, useEffect } from "react";
import api from "../api/axios";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null); // Lưu thông tin { username, role }
  const [loading, setLoading] = useState(true);

  // Khôi phục phiên đăng nhập khi F5 lại trang
  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const res = await api.get("/auth/me");

      // 🟢 SỬA LẠI: Kiểm tra res.data.username hoặc res.data.id thay vì res.data.authenticated
      if (res.data && (res.data.username || res.data.id)) {
        setUser({
          username: res.data.username,
          role: res.data.role,
        });
      } else {
        setUser(null);
      }
    } catch (err) {
      console.error("Phiên đăng nhập hết hạn hoặc chưa đăng nhập", err);
      setUser(null);
    } finally {
      setLoading(false);
    }
  };
  const login = async (username, password) => {
    const res = await api.post("/auth/login", { username, password });
    // Backend vừa set Cookie tự động, vừa trả về JwtResponse { username, role, ... }
    setUser({ username: res.data.username, role: res.data.role });
    return res.data.role; // Trả về role để chuyển hướng route
  };

  const register = async (userData) => {
    return await api.post("/auth/register", userData);
  };

  const logout = async () => {
    try {
      await api.post("/auth/logout"); // Backend xóa Cookie accessToken & refreshToken
    } catch (err) {
      console.error("Lỗi khi đăng xuất:", err);
    } finally {
      setUser(null);
      window.location.href = "/login";
    }
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
