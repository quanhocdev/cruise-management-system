import { useState } from "react";
import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  ConciergeBell,
  Package,
  ClipboardList,
  ChevronLeft,
  ChevronRight,
  Sparkles,
} from "lucide-react";

import "../../styles/convenience/ConvenienceSidebar.css";

const menuItems = [
  {
    label: "Dashboard",
    icon: LayoutDashboard,
    path: "/convenience/dashboard",
  },
  {
    label: "Quản lý Dịch vụ",
    icon: ConciergeBell,
    path: "/convenience/services",
  },
  {
    label: "Quản lý Sản phẩm",
    icon: Package,
    path: "/convenience/products",
  },
  {
    label: "Cấu hình Tour",
    icon: ClipboardList,
    path: "/convenience/tour-config",
  },
];

function ConvenienceSidebar() {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <aside className={`convenience-sidebar ${collapsed ? "collapsed" : ""}`}>
      {/* =====================================================
          TOP
         ===================================================== */}

      <div className="convenience-sidebar-top">
        <div className="convenience-sidebar-brand">
          <Sparkles size={24} />

          {!collapsed && <span>CONVENIENCE</span>}
        </div>

        <button
          type="button"
          className="convenience-sidebar-toggle"
          onClick={() => setCollapsed((prev) => !prev)}
          title={collapsed ? "Mở rộng" : "Thu gọn"}
        >
          {collapsed ? <ChevronRight size={20} /> : <ChevronLeft size={20} />}
        </button>
      </div>

      {/* =====================================================
          MENU
         ===================================================== */}

      <nav className="convenience-sidebar-menu">
        {menuItems.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `convenience-sidebar-item ${isActive ? "active" : ""}`
              }
              title={collapsed ? item.label : undefined}
            >
              <Icon size={20} />

              {!collapsed && <span>{item.label}</span>}
            </NavLink>
          );
        })}
      </nav>

      {/* =====================================================
          BOTTOM
         ===================================================== */}

      {!collapsed && (
        <div className="convenience-sidebar-bottom">
          <span>Convenience Portal</span>
          <small>Service & Product</small>
        </div>
      )}
    </aside>
  );
}

export default ConvenienceSidebar;
