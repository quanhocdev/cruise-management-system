import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
} from "react";

import api from "../api/axios";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Lấy thông tin tài khoản từ cookie phiên nội bộ.
  const refreshUser = useCallback(async () => {
    const res = await api.get("/auth/me");

    if (res.data && res.data.username) {
      const currentUser = {
        username: res.data.username,
        role: res.data.role,
      };

      setUser(currentUser);
      return currentUser;
    }

    setUser(null);
    return null;
  }, []);

  useEffect(() => {
    let active = true;

    const checkAuthStatus = async () => {
      try {
        const res = await api.get("/auth/me");

        if (!active) return;

        if (res.data && res.data.username) {
          setUser({
            username: res.data.username,
            role: res.data.role,
          });
        } else {
          setUser(null);
        }
      } catch {
        if (active) {
          setUser(null);
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    checkAuthStatus();

    return () => {
      active = false;
    };
  }, []);

  const login = async (username, password) => {
    const res = await api.post("/auth/login", {
      username,
      password,
    });

    const loggedInUser = {
      username: res.data.username,
      role: res.data.role,
    };

    setUser(loggedInUser);

    return res.data.role;
  };

  const register = async (userData) => {
    return api.post("/auth/register", userData);
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
    <AuthContext.Provider
      value={{
        user,
        setUser,
        login,
        register,
        logout,
        loading,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
