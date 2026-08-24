import { useState } from "react";
import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  Ship,
  CalendarDays,
  Ticket,
  Settings,
  ChevronLeft,
  ChevronRight,
  X,
  Activity,
} from "lucide-react";

import "../../styles/onboard/OnboardSidebar.css";

const menuItems = [
  {
    label: "Dashboard",
    icon: LayoutDashboard,
    path: "/onboard",
    end: true,
  },
  {
    label: "Cấu hình hoạt động",
    icon: Activity,
    path: "/onboard/activity-cruise",
  },
  {
    label: "Lịch trình",
    icon: CalendarDays,
    path: "/onboard/schedules",
  },
  {
    label: "Du thuyền",
    icon: Ship,
    path: "/onboard/cruises",
  },
  {
    label: "Booking",
    icon: Ticket,
    path: "/onboard/bookings",
  },
  {
    label: "Cài đặt",
    icon: Settings,
    path: "/onboard/settings",
  },
];

function OnboardSidebar({ mobileOpen, onCloseMobile }) {
  const [collapsed, setCollapsed] = useState(false);

  const isExpanded = !collapsed || mobileOpen;

  return (
    <aside
      className={`onboard-sidebar ${collapsed ? "collapsed" : ""} ${
        mobileOpen ? "mobile-open" : ""
      }`}
    >
      {/* HEADER / BRAND */}
      <div className="onboard-sidebar-header">
        <div className="onboard-sidebar-brand">
          <div className="onboard-brand-icon">
            <Ship size={22} />
          </div>

          {isExpanded && (
            <div className="onboard-brand-text">
              <strong>Cruise</strong>
              <span>Onboard</span>
            </div>
          )}
        </div>

        {/* Toggle Desktop */}
        <button
          type="button"
          className="onboard-sidebar-toggle desktop-only"
          onClick={() => setCollapsed(!collapsed)}
          title={collapsed ? "Mở rộng" : "Thu gọn"}
        >
          {collapsed ? <ChevronRight size={20} /> : <ChevronLeft size={20} />}
        </button>

        {/* Close Mobile */}
        <button
          type="button"
          className="onboard-sidebar-toggle mobile-only"
          onClick={onCloseMobile}
          title="Đóng menu"
        >
          <X size={20} />
        </button>
      </div>

      {/* NAVIGATION MENU */}
      <nav className="onboard-sidebar-menu">
        {isExpanded && (
          <div className="onboard-sidebar-section-title">QUẢN LÝ TRÊN TÀU</div>
        )}

        {menuItems.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.end}
              onClick={onCloseMobile}
              className={({ isActive }) =>
                `onboard-nav-item ${isActive ? "active" : ""}`
              }
              title={collapsed && !mobileOpen ? item.label : undefined}
            >
              <Icon size={19} className="onboard-nav-icon" />

              {isExpanded && <span>{item.label}</span>}
            </NavLink>
          );
        })}
      </nav>

      {/* FOOTER / STATUS */}
      <div className="onboard-sidebar-bottom">
        <div className="onboard-sidebar-status">
          <span className="onboard-status-dot" title="Đã kết nối" />

          {isExpanded && (
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

export default OnboardSidebar;
