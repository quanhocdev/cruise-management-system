import { createContext, useContext, useState, useEffect } from "react";
import api from "../api/axios";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const res = await api.get("/auth/me");

      if (res.data && (res.data.username || res.data.id)) {
        setUser({
          username: res.data.username,
          role: res.data.role,
        });
      } else {
        setUser(null);
      }
    } catch (err) {
      // Khi xuống tới đây nghĩa là CẢ Access Token LẪN Refresh Token đều đã hết hạn
      console.log(
        "Phiên đăng nhập hết hạn, chưa đăng nhập hoặc Refresh thất bại",
      );
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (username, password) => {
    const res = await api.post("/auth/login", { username, password });
    setUser({ username: res.data.username, role: res.data.role });
    return res.data.role;
  };

  const register = async (userData) => {
    return await api.post("/auth/register", userData);
  };

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
    <AuthContext.Provider value={{ user, login, register, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
