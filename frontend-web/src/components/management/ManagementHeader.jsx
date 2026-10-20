// ManagementHeader.jsx
import { Menu, UserCircle } from "lucide-react";
import NotificationBell from "./NotificationBell";
import "../../styles/management/header.css";

function ManagementHeader({ config, onMenuClick, notificationProps }) {
  return (
    <header className="mgmt-header">
      <div className="mgmt-header-left">
        <button
          type="button"
          className="mgmt-mobile-menu"
          onClick={onMenuClick}
          title="Mở menu"
        >
          <Menu size={21} />
        </button>
        <div className="mgmt-header-title">
          <span className="mgmt-header-title-main">{config.header.title}</span>
          <span className="mgmt-header-title-sub">
            {config.header.subtitle}
          </span>
        </div>
      </div>

      <div className="mgmt-header-right">
        <NotificationBell {...notificationProps} />
        <div className="mgmt-header-user">
          <UserCircle size={32} />
          <div className="mgmt-header-user-info">
            <strong>{config.user.name}</strong>
            <span>{config.user.role}</span>
          </div>
        </div>
      </div>
    </header>
  );
}

export default ManagementHeader;
