// src/components/common/UserMenu.jsx
// Dropdown tài khoản dùng chung cho GuestHeader và header của các role quản lý.
import { Link } from "react-router-dom";
import { Dropdown } from "react-bootstrap";
import { getRedirectPathByRole } from "../../routes/roleRoutes";

// "/scheduler/dashboard" -> "/scheduler/profile"
// ⚠️ Nếu dashboard của role nào không có dạng "/<role>/dashboard", truyền prop profilePath.
const deriveProfilePath = (role) =>
  getRedirectPathByRole(role)?.replace(/\/[^/]+\/?$/, "/profile") || "/profile";

export default function UserMenu({
  user,
  onLogout,
  profilePath,
  showDashboard = true,
  extraItems = null, // ví dụ: mục riêng của Passenger
}) {
  if (!user) return null;

  return (
    <Dropdown align="end">
      <Dropdown.Toggle
        variant="outline-primary"
        size="sm"
        className="rounded-pill px-3 fw-semibold d-flex align-items-center gap-2"
      >
        <span>👤 {user.username}</span>
        <span className="badge bg-primary text-light small">{user.role}</span>
      </Dropdown.Toggle>

      <Dropdown.Menu className="shadow border-0 py-2 rounded-3 mt-2">
        <Dropdown.Item
          as={Link}
          to={profilePath || deriveProfilePath(user.role)}
          className="py-2 small fw-medium"
        >
          🙍 Thông tin tài khoản
        </Dropdown.Item>

        {extraItems}

        {showDashboard && (
          <Dropdown.Item
            as={Link}
            to={getRedirectPathByRole(user.role)}
            className="py-2 small fw-medium"
          >
            📊 Trang Dashboard
          </Dropdown.Item>
        )}

        <Dropdown.Divider />

        <Dropdown.Item
          onClick={onLogout}
          className="py-2 small text-danger fw-semibold"
        >
          🚪 Đăng xuất
        </Dropdown.Item>
      </Dropdown.Menu>
    </Dropdown>
  );
}
