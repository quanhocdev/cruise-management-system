// src/components/finance/FinanceSidebar.jsx (hoặc đường dẫn sidebar hiện tại của bạn)
import { useState } from "react";
import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  Ship,
  CalendarDays,
  DoorOpen,
  CreditCard,
  ChevronLeft,
  ChevronRight,
  X,
} from "lucide-react";

import "../../styles/onboard/OnboardSidebar.css";

const menuItems = [
  {
    label: "Dashboard",
    icon: LayoutDashboard,
    path: "/finance",
    end: true,
  },
  {
    label: "Quản lý Lịch trình Tour",
    icon: CalendarDays,
    path: "/finance/tours-schedule", // Đường dẫn trang lịch trình tour
  },
  {
    label: "Danh sách phòng",
    icon: DoorOpen,
    path: "/finance/rooms", // Đường dẫn trang quản lý phòng
  },
  {
    label: "Danh sách vòng NFC",
    icon: CreditCard,
    path: "/finance/nfc-cards", // Đường dẫn trang quản lý vòng NFC
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
              <span>Finance</span>
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
          <div className="onboard-sidebar-section-title">QUẢN LÝ LỄ TÂN</div>
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
