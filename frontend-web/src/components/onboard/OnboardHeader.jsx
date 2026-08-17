// frontend-web/src/components/onboard/OperationHeader.jsx
import { Menu } from "lucide-react";
import OnboardNotificationBell from "./OnboardNotificationBell";

import "../../styles/onboard/OnboardHeader.css";

function OnboardHeader({ onMenuClick }) {
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
          <h1 className="onboard-header-title">Onboard Management</h1>
          <span className="onboard-header-subtitle">Quản lý trên tàu</span>
        </div>
      </div>

      <div className="onboard-header-right">
        <OnboardNotificationBell />

        <div className="onboard-user-profile">
          <div className="onboard-avatar">OB</div>

          <div className="onboard-user-info">
            <span className="onboard-user-name">Onboard Admin</span>
            <span className="onboard-user-role">Quản lý vận hành</span>
          </div>
        </div>
      </div>
    </header>
  );
}

export default OnboardHeader;
