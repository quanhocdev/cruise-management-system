import React from "react";
import { UserCircle } from "lucide-react";
// import NotificationBell from "./NotificationBell";
import "../../styles/convenience/ConvenienceHeader.css";

function ConvenienceHeader() {
  return (
    <header className="convenience-header">
      <div className="convenience-header-left">
        <h1>Tiện ích & Dịch vụ</h1>
      </div>

      <div className="convenience-header-right">
        {/* Tạm comment lại thẻ này cho đến khi tạo file NotificationBell.jsx */}
        {/* <NotificationBell /> */}

        <div className="convenience-header-user">
          <UserCircle size={32} />

          <div className="convenience-header-user-info">
            <span className="convenience-header-user-name">Convenience</span>
            <span className="convenience-header-user-role">
              Service Manager
            </span>
          </div>
        </div>
      </div>
    </header>
  );
}

export default ConvenienceHeader;
