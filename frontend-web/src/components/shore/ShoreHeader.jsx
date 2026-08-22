import { Menu, UserCircle } from "lucide-react";

import NotificationBell from "../operation/NotificationBell";

import "../../styles/shore/ShoreHeader.css";

function ShoreHeader({ onMenuClick }) {
  return (
    <header className="shore-header">
      <div className="shore-header-left">
        <button
          type="button"
          className="shore-mobile-menu"
          onClick={onMenuClick}
          title="Mở menu"
        >
          <Menu size={21} />
        </button>

        <div className="shore-header-title">
          <span className="shore-header-title-main">Shore Management</span>

          <span className="shore-header-title-sub">Quản lý tour bờ</span>
        </div>
      </div>

      <div className="shore-header-right">
        <NotificationBell />

        <div className="shore-header-user">
          <UserCircle size={32} />

          <div className="shore-header-user-info">
            <strong>Shore Manager</strong>
            <span>Quản lý tour bờ</span>
          </div>
        </div>
      </div>
    </header>
  );
}

export default ShoreHeader;
