import { useState } from "react";
import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  Ship,
  ClipboardCheck,
  CalendarDays,
  Ticket,
  Package,
  Settings,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";

import "../../styles/operation/OperationSidebar.css";

const menuItems = [
  {
    label: "Dashboard",
    icon: LayoutDashboard,
    path: "/operation",
    end: true,
  },
  {
    label: "Quản lý tour",
    icon: ClipboardCheck,
    path: "/operation/tours",
  },
  {
    label: "Lịch trình",
    icon: CalendarDays,
    path: "/operation/schedules",
  },
  {
    label: "Du thuyền",
    icon: Ship,
    path: "/operation/cruises",
  },
  {
    label: "Booking",
    icon: Ticket,
    path: "/operation/bookings",
  },
  {
    label: "Sản phẩm / Dịch vụ",
    icon: Package,
    path: "/operation/products",
  },
  {
    label: "Cài đặt",
    icon: Settings,
    path: "/operation/settings",
  },
];

function OperationSidebar() {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <aside className={`operation-sidebar ${collapsed ? "collapsed" : ""}`}>
      {/* HEADER / TOP */}
      <div className="operation-sidebar-header">
        <div className="operation-sidebar-brand">
          <div className="operation-sidebar-logo">
            <Ship size={22} />
          </div>

          {!collapsed && (
            <div className="operation-sidebar-brand-text">
              <strong>Cruise</strong>
              <span>Operation</span>
            </div>
          )}
        </div>

        <button
          type="button"
          className="operation-sidebar-toggle"
          onClick={() => setCollapsed(!collapsed)}
          title={collapsed ? "Mở rộng" : "Thu gọn"}
        >
          {collapsed ? <ChevronRight size={20} /> : <ChevronLeft size={20} />}
        </button>
      </div>

      {/* NAVIGATION MENU */}
      <nav className="operation-sidebar-nav">
        {!collapsed && (
          <div className="operation-sidebar-section-title">VẬN HÀNH</div>
        )}

        {menuItems.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.end}
              className={({ isActive }) =>
                `operation-sidebar-link ${isActive ? "active" : ""}`
              }
              title={collapsed ? item.label : undefined}
            >
              <Icon size={19} />
              {!collapsed && <span>{item.label}</span>}
            </NavLink>
          );
        })}
      </nav>

      {/* FOOTER / STATUS */}
      <div className="operation-sidebar-bottom">
        <div className="operation-sidebar-status">
          <span className="operation-status-dot" />

          {!collapsed && (
            <div>
              <strong>Hệ thống hoạt động</strong>
              <span>Đã kết nối</span>
            </div>
          )}
        </div>
      </div>
    </aside>
  );
}

export default OperationSidebar;
