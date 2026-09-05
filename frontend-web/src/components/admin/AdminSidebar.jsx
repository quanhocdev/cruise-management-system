import { useState } from "react";
import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  Users,
  Ship,
  Anchor,
  Package,
  ConciergeBell,
  FileText,
  MonitorSmartphone,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";

import "../../styles/admin/AdminSidebar.css";

const menuItems = [
  {
    label: "Dashboard",
    icon: LayoutDashboard,
    path: "/admin/dashboard",
  },
  {
    label: "Quản lý tài khoản",
    icon: Users,
    path: "/admin/accounts",
  },
  {
    label: "Quản lý du thuyền",
    icon: Ship,
    path: "/admin/cruises",
  },
  {
    label: "Quản lý cảng",
    icon: Anchor,
    path: "/admin/ports",
  },
  {
    label: "Quản lý sản phẩm",
    icon: Package,
    path: "/admin/products",
  },
  {
    label: "Quản lý dịch vụ",
    icon: ConciergeBell,
    path: "/admin/services",
  },
  {
    label: "Quản lý máy POS",
    icon: MonitorSmartphone,
    path: "/admin/pos-terminals",
  },
  {
    label: "Quản lý chính sách",
    icon: FileText,
    path: "/admin/policies",
  },
];

function AdminSidebar() {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <aside className={`admin-sidebar ${collapsed ? "collapsed" : ""}`}>
      <div className="admin-sidebar-top">
        <div className="admin-sidebar-brand">
          <Ship size={24} />

          {!collapsed && <span>CRUISE ADMIN</span>}
        </div>

        <button
          type="button"
          className="admin-sidebar-toggle"
          onClick={() => setCollapsed(!collapsed)}
          title={collapsed ? "Mở rộng" : "Thu gọn"}
        >
          {collapsed ? <ChevronRight size={20} /> : <ChevronLeft size={20} />}
        </button>
      </div>

      <nav className="admin-sidebar-menu">
        {menuItems.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `admin-sidebar-item ${isActive ? "active" : ""}`
              }
              title={collapsed ? item.label : undefined}
            >
              <Icon size={20} />

              {!collapsed && <span>{item.label}</span>}
            </NavLink>
          );
        })}
      </nav>
    </aside>
  );
}

export default AdminSidebar;
