// src/components/shore/ShoreSidebar.jsx

import { useState } from "react";
import { NavLink, useLocation } from "react-router-dom"; // Import thêm useLocation
import {
  LayoutDashboard,
  ClipboardList,
  History,
  Ship,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import "../../styles/shore/ShoreSidebar.css";

const MENU_SECTIONS = [
  {
    title: "QUẢN LÝ TOUR BỜ",
    items: [
      {
        label: "Dashboard",
        icon: LayoutDashboard,
        path: "/shore/dashboard",
        end: true,
      },
      {
        label: "Danh sách Tour",
        icon: ClipboardList,
        path: "/shore/tours",
        extraPaths: ["/shore/visit-tour-configuration"],
      },
      {
        label: "Lịch sử",
        icon: History,
        path: "/shore/history",
      },
    ],
  },
];

function ShoreSidebar() {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation(); // Lấy pathname hiện tại

  const toggleSidebar = () => {
    setCollapsed((prev) => !prev);
  };

  return (
    <aside
      className={`shore-sidebar ${collapsed ? "collapsed" : ""}`}
      aria-expanded={!collapsed}
    >
      {/* HEADER / BRAND */}
      <div className="shore-sidebar-header">
        <div className="shore-sidebar-brand">
          <div className="shore-sidebar-logo">
            <Ship size={22} />
          </div>

          {!collapsed && (
            <div className="shore-sidebar-brand-text">
              <strong>Cruise</strong>
              <span>Shore</span>
            </div>
          )}
        </div>

        <button
          type="button"
          className="shore-sidebar-toggle"
          onClick={toggleSidebar}
          aria-label={
            collapsed ? "Mở rộng thanh điều hướng" : "Thu gọn thanh điều hướng"
          }
          title={collapsed ? "Mở rộng" : "Thu gọn"}
        >
          {collapsed ? <ChevronRight size={20} /> : <ChevronLeft size={20} />}
        </button>
      </div>

      {/* NAVIGATION */}
      <nav className="shore-sidebar-nav">
        {MENU_SECTIONS.map((section, sectionIdx) => (
          <div
            key={section.title || sectionIdx}
            className="shore-sidebar-group"
          >
            {!collapsed && section.title && (
              <div className="shore-sidebar-section-title">{section.title}</div>
            )}

            {section.items.map((item) => {
              const Icon = item.icon;

              // Kiểm tra xem pathname hiện tại có khớp với item.path hoặc extraPaths không
              const isCustomActive =
                location.pathname === item.path ||
                (item.extraPaths &&
                  item.extraPaths.some((p) => location.pathname.startsWith(p)));

              return (
                <NavLink
                  key={item.path}
                  to={item.path}
                  end={item.end}
                  className={() =>
                    `shore-sidebar-link ${isCustomActive ? "active" : ""}`
                  }
                  title={collapsed ? item.label : undefined}
                >
                  <div className="shore-sidebar-icon-wrapper">
                    <Icon size={19} />
                  </div>

                  {!collapsed && (
                    <span className="shore-sidebar-label">{item.label}</span>
                  )}
                </NavLink>
              );
            })}
          </div>
        ))}
      </nav>

      {/* STATUS */}
      <div className="shore-sidebar-bottom">
        <div
          className="shore-sidebar-status"
          title={collapsed ? "Hệ thống hoạt động - Đã kết nối" : undefined}
        >
          <span className="shore-status-dot" />

          {!collapsed && (
            <div className="shore-sidebar-status-text">
              <strong>Hệ thống hoạt động</strong>
              <span>Đã kết nối</span>
            </div>
          )}
        </div>
      </div>
    </aside>
  );
}

export default ShoreSidebar;
