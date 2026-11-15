import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { ChevronDown, LogOut, UserCircle, UserRound } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import { getRedirectPathByRole } from "../../routes/roleRoutes";
import "../../styles/management/user-menu.css";

// "/scheduler/dashboard" -> "/scheduler/profile"
const deriveProfilePath = (role) =>
  getRedirectPathByRole(role)?.replace(/\/[^/]+\/?$/, "/profile") || "/profile";

export default function ManagementUserMenu({ config }) {
  const { user, logout } = useAuth();
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  // Đóng khi bấm ra ngoài hoặc nhấn Esc
  useEffect(() => {
    if (!open) return;
    const onClick = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    };
    const onKey = (e) => e.key === "Escape" && setOpen(false);
    document.addEventListener("mousedown", onClick);
    document.addEventListener("keydown", onKey);
    return () => {
      document.removeEventListener("mousedown", onClick);
      document.removeEventListener("keydown", onKey);
    };
  }, [open]);

  const profilePath =
    config.profilePath || deriveProfilePath(user?.role || config.user.role);

  const handleLogout = async () => {
    setOpen(false);
    await logout();
  };

  return (
    <div className="mgmt-user-menu" ref={ref}>
      <button
        type="button"
        className="mgmt-header-user mgmt-user-trigger"
        aria-haspopup="menu"
        aria-expanded={open}
        onClick={() => setOpen((v) => !v)}
      >
        {config.user.initials ? (
          <div className="mgmt-avatar">{config.user.initials}</div>
        ) : (
          <UserCircle size={32} />
        )}
        <div className="mgmt-header-user-info">
          <strong>{config.user.name}</strong>
          <span>{config.user.role}</span>
        </div>
        <ChevronDown
          size={16}
          className={`mgmt-user-chevron ${open ? "open" : ""}`}
        />
      </button>

      {open && (
        <div className="mgmt-user-dropdown" role="menu">
          <Link
            to={profilePath}
            role="menuitem"
            className="mgmt-user-item"
            onClick={() => setOpen(false)}
          >
            <UserRound size={16} />
            <span>Thông tin tài khoản</span>
          </Link>

          <div className="mgmt-user-divider" />

          <button
            type="button"
            role="menuitem"
            className="mgmt-user-item danger"
            onClick={handleLogout}
          >
            <LogOut size={16} />
            <span>Đăng xuất</span>
          </button>
        </div>
      )}
    </div>
  );
}
