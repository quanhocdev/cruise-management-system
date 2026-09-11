import React from "react";
import { UserCircle } from "lucide-react";
import NotificationBell from "./ConvenienceNotification";
import "../../styles/convenience/ConvenienceHeader.css";

function ConvenienceHeader() {
  return (
    <header className="convenience-header">
      <div className="convenience-header-left">
        <h1>Tiện ích & Dịch vụ</h1>
      </div>

      <div className="convenience-header-right flex items-center gap-4">
        <NotificationBell />

        <div className="convenience-header-user flex items-center gap-2">
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
