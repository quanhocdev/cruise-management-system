// src/context/AuthContext.jsx
import { createContext, useContext, useState, useEffect } from "react";
import api from "../api/axios";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null); // Lưu thông tin { username, role }
  const [loading, setLoading] = useState(true);

  // Khi reload trang: Tự động khôi phục phiên làm việc từ Cookie hiện có
  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      // Viết 1 API /api/auth/me phía Backend trả về thông tin user hiện tại từ SecurityContext
      const res = await api.get("/api/auth/me");
      setUser(res.data);
    } catch {
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (username, password) => {
    const res = await api.post("/api/auth/login", { username, password });
    // Backend set Cookie accessToken tự động, trả về role trong body
    setUser({ username, role: res.data.role });
    return res.data.role; // Trả về role để redirect
  };

  const register = async (userData) => {
    return await api.post("/api/auth/register", userData);
  };

  const logout = async () => {
    try {
      await api.post("/api/auth/logout"); // Backend xóa Cookie
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
