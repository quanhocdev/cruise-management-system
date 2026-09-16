// src/components/finance/FinanceHeader.jsx
import { Menu } from "lucide-react";
import NotificationBell from "./NotificationBell"; // Hoặc chỉnh lại đường dẫn relative chính xác tới file NotificationBell

import "../../styles/onboard/OnboardHeader.css";

function FinanceHeader({ onMenuClick }) {
  return (
    <header className="onboard-header">
      <div className="onboard-header-left">
        <button
          type="button"
          className="onboard-mobile-menu"
          onClick={onMenuClick}
          title="Mở menu"
        >
          <Menu size={22} />
        </button>

        <div className="onboard-header-title-group">
          <h1 className="onboard-header-title">Finance Management</h1>
          <span className="onboard-header-subtitle">
            Quản lý lễ tân - tài chính
          </span>
        </div>
      </div>

      <div className="onboard-header-right">
        {/* Dùng đúng tên NotificationBell thay vì OnboardNotificationBell */}
        <NotificationBell />

        <div className="onboard-user-profile">
          <div className="onboard-avatar">FN</div>

          <div className="onboard-user-info">
            <span className="onboard-user-name">Finance role</span>
            <span className="onboard-user-role">
              Quản lý lễ tân - tài chính
            </span>
          </div>
        </div>
      </div>
    </header>
  );
}

export default FinanceHeader;
