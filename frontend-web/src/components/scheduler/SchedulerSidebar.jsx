// src/components/scheduler/SchedulerSidebar.jsx

import { useState } from "react";
import { NavLink } from "react-router-dom";

import {
  LayoutDashboard,
  Map,
  ChevronLeft,
  ChevronRight,
  Ship,
} from "lucide-react";

import "../../styles/scheduler/SchedulerSidebar.css";

const menuItems = [
  {
    label: "Dashboard",
    icon: LayoutDashboard,
    path: "/scheduler/dashboard",
  },
  {
    label: "Quản lý Tour",
    icon: Map,
    path: "/scheduler/tours",
  },
];

function SchedulerSidebar() {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <aside className={`scheduler-sidebar ${collapsed ? "collapsed" : ""}`}>
      {/* =====================================================
          TOP
         ===================================================== */}

      <div className="scheduler-sidebar-top">
        <div className="scheduler-sidebar-brand">
          <Ship size={24} />

          {!collapsed && <span>CRUISE SCHEDULER</span>}
        </div>

        <button
          type="button"
          className="scheduler-sidebar-toggle"
          onClick={() => setCollapsed((prev) => !prev)}
          title={collapsed ? "Mở rộng" : "Thu gọn"}
        >
          {collapsed ? <ChevronRight size={20} /> : <ChevronLeft size={20} />}
        </button>
      </div>

      {/* =====================================================
          MENU
         ===================================================== */}

      <nav className="scheduler-sidebar-menu">
        {menuItems.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `scheduler-sidebar-item ${isActive ? "active" : ""}`
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
        <div className="scheduler-sidebar-bottom">
          <span>Tour Management</span>
          <small>Scheduler</small>
        </div>
      )}
    </aside>
  );
}

export default SchedulerSidebar;
