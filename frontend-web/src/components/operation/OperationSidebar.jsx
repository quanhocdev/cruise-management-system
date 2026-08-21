// src/modules/operation/components/OperationSidebar.jsx

import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  Ship,
  ClipboardCheck,
  CalendarDays,
  Ticket,
  Package,
  Settings,
  SlidersHorizontal,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";

import "../../styles/operation/OperationSidebar.css";

// Tách danh sách menu ra ngoài component để tránh re-declare mỗi lần re-render
const MENU_SECTIONS = [
  {
    title: "VẬN HÀNH",
    items: [
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
        label: "Cấu hình Tour",
        icon: SlidersHorizontal,
        path: "/operation/tour-configuration",
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
    ],
  },
  {
    title: "HỆ THỐNG",
    items: [
      {
        label: "Cài đặt",
        icon: Settings,
        path: "/operation/settings",
      },
    ],
  },
];

function OperationSidebar() {
  const [collapsed, setCollapsed] = useState(false);

  const toggleSidebar = () => {
    setCollapsed((prev) => !prev);
  };

  return (
    <aside
      className={`operation-sidebar ${collapsed ? "collapsed" : ""}`}
      aria-expanded={!collapsed}
    >
      {/* HEADER / BRAND */}
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
          onClick={toggleSidebar}
          aria-label={
            collapsed ? "Mở rộng thanh điều hướng" : "Thu gọn thanh điều hướng"
          }
          title={collapsed ? "Mở rộng" : "Thu gọn"}
        >
          {collapsed ? <ChevronRight size={20} /> : <ChevronLeft size={20} />}
        </button>
      </div>

      {/* NAVIGATION MENU */}
      <nav className="operation-sidebar-nav">
        {MENU_SECTIONS.map((section, sectionIdx) => (
          <div
            key={section.title || sectionIdx}
            className="operation-sidebar-group"
          >
            {!collapsed && section.title && (
              <div className="operation-sidebar-section-title">
                {section.title}
              </div>
            )}

            {section.items.map((item) => {
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
                  <div className="operation-sidebar-icon-wrapper">
                    <Icon size={19} />
                  </div>

                  {!collapsed && (
                    <span className="operation-sidebar-label">
                      {item.label}
                    </span>
                  )}
                </NavLink>
              );
            })}
          </div>
        ))}
      </nav>

      {/* FOOTER / SYSTEM STATUS */}
      <div className="operation-sidebar-bottom">
        <div
          className="operation-sidebar-status"
          title={collapsed ? "Hệ thống hoạt động - Đã kết nối" : undefined}
        >
          <span className="operation-status-dot" />

          {!collapsed && (
            <div className="operation-sidebar-status-text">
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
