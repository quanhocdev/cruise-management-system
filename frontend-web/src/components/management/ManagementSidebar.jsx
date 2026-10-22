// components/management/ManagementSidebar.jsx
// components/management/ManagementSidebar.jsx
import { useLocation, NavLink } from "react-router-dom";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { isPathActive } from "./utils";
import "../../styles/management/sidebar.css";

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
              {config.brand.subtitle && <span>{config.brand.subtitle}</span>}
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

      {config.status && !(config.status.hideDot && collapsed) && (
        <div className="mgmt-sidebar-bottom">
          <div
            className="mgmt-sidebar-status"
            title={
              collapsed
                ? `${config.status.title} - ${config.status.subtitle}`
                : undefined
            }
          >
            {!config.status.hideDot && <span className="mgmt-status-dot" />}
            {!collapsed && (
              <div>
                <strong>{config.status.title}</strong>
                <span>{config.status.subtitle}</span>
              </div>
            )}
          </div>
        </div>
      )}
    </aside>
  );
}

export default ManagementSidebar;
