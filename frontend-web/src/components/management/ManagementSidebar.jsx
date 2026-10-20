// components/management/ManagementSidebar.jsx
import { useLocation, NavLink } from "react-router-dom";
import { ChevronLeft, ChevronRight } from "lucide-react";
import "../../styles/management/sidebar.css";

function isPathActive(pathname, item) {
  if (pathname === item.path) return true;
  if (!item.end && pathname.startsWith(`${item.path}/`)) return true;
  return item.extraPaths?.some(
    (p) => pathname === p || pathname.startsWith(`${p}/`),
  );
}

function ManagementSidebar({
  config,
  collapsed,
  onToggle,
  mobileOpen,
  onClose,
}) {
  const { pathname } = useLocation();
  const BrandIcon = config.brand.icon;

  return (
    <aside
      className={`mgmt-sidebar ${collapsed ? "collapsed" : ""} ${mobileOpen ? "mobile-open" : ""}`}
    >
      <div className="mgmt-sidebar-header">
        <div className="mgmt-sidebar-brand">
          <div className="mgmt-sidebar-logo">
            <BrandIcon size={22} />
          </div>
          {!collapsed && (
            <div className="mgmt-sidebar-brand-text">
              <strong>{config.brand.title}</strong>
              <span>{config.brand.subtitle}</span>
            </div>
          )}
        </div>

        <button
          type="button"
          className="mgmt-sidebar-toggle"
          onClick={onToggle}
          aria-label={
            collapsed ? "Mở rộng thanh điều hướng" : "Thu gọn thanh điều hướng"
          }
        >
          {collapsed ? <ChevronRight size={20} /> : <ChevronLeft size={20} />}
        </button>
      </div>

      <nav className="mgmt-sidebar-nav">
        {config.menu.map((section, i) => (
          <div key={section.title || i}>
            {!collapsed && section.title && (
              <div className="mgmt-sidebar-section-title">{section.title}</div>
            )}

            {section.items.map((item) => {
              const Icon = item.icon;
              return (
                <NavLink
                  key={item.path}
                  to={item.path}
                  end={item.end}
                  onClick={onClose}
                  className={`mgmt-sidebar-link ${isPathActive(pathname, item) ? "active" : ""}`}
                  title={collapsed ? item.label : undefined}
                >
                  <Icon size={19} />
                  {!collapsed && <span>{item.label}</span>}
                </NavLink>
              );
            })}
          </div>
        ))}
      </nav>

      <div className="mgmt-sidebar-bottom">
        <div className="mgmt-sidebar-status">
          <span className="mgmt-status-dot" />
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

export default ManagementSidebar;
