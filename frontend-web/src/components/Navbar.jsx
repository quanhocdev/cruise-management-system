// src/components/Navbar.jsx
import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";

export default function Navbar() {
  const { user, logout } = useAuth();

  if (!user) return null;

  return (
    <nav
      style={{
        display: "flex",
        justifyContent: "space-between",
        padding: "10px 20px",
        background: "#1890ff",
        color: "#fff",
      }}
    >
      <div style={{ display: "flex", gap: "15px", alignItems: "center" }}>
        <strong>CRUISE SYSTEM</strong>

        {/* Menu động theo Role */}
        {user.role === "ADMIN" && (
          <Link to="/admin" style={{ color: "#fff" }}>
            Admin
          </Link>
        )}
        {(user.role === "FINANCE" || user.role === "ADMIN") && (
          <Link to="/finance" style={{ color: "#fff" }}>
            Tài Chính
          </Link>
        )}
        {(user.role === "SCHEDULER" || user.role === "ADMIN") && (
          <Link to="/scheduler" style={{ color: "#fff" }}>
            Lịch Trình
          </Link>
        )}
        {(user.role === "ONBOARD" || user.role === "ADMIN") && (
          <Link to="/onboard" style={{ color: "#fff" }}>
            Dịch Vụ Tàu
          </Link>
        )}
        {(user.role === "SHORE" || user.role === "ADMIN") && (
          <Link to="/shore" style={{ color: "#fff" }}>
            Dịch Vụ Bờ
          </Link>
        )}
        {user.role === "PASSENGER" && (
          <Link to="/passenger" style={{ color: "#fff" }}>
            Chuyến Đi Của Tôi
          </Link>
        )}
      </div>

      <div>
        <span>
          Xin chào, <b>{user.username}</b> [{user.role}]{" "}
        </span>
        <button
          onClick={logout}
          style={{
            marginLeft: "10px",
            backgroundColor: "#ff4d4f",
            color: "#fff",
            border: "none",
            padding: "5px 10px",
            cursor: "pointer",
            borderRadius: "4px",
          }}
        >
          Đăng xuất
        </button>
      </div>
    </nav>
  );
}
