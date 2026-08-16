import { Menu, UserCircle } from "lucide-react";

import NotificationBell from "./NotificationBell";

import "../../styles/operation/OperationHeader.css";

function OperationHeader({ onMenuClick }) {
  return (
    <header className="operation-header">
      <div className="operation-header-left">
        <button
          type="button"
          className="operation-mobile-menu"
          onClick={onMenuClick}
          title="Mở menu"
        >
          <Menu size={21} />
        </button>

        <div className="operation-header-title">
          <span className="operation-header-title-main">
            Operation Management
          </span>

          <span className="operation-header-title-sub">Quản lý vận hành</span>
        </div>
      </div>

      <div className="operation-header-right">
        <NotificationBell />

        <div className="operation-header-user">
          <UserCircle size={32} />

          <div className="operation-header-user-info">
            <strong>Operation</strong>
            <span>Quản lý vận hành</span>
          </div>
        </div>
      </div>
    </header>
  );
}

export default OperationHeader;
