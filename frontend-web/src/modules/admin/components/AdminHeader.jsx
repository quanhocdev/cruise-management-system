import { UserCircle } from "lucide-react";

import NotificationBell from "./NotificationBell";
import "../styles/AdminHeader.css";

function AdminHeader() {
  return (
    <header className="admin-header">
      <div className="admin-header-left">
        <h1>Dashboard</h1>
      </div>

      <div className="admin-header-right">
        <NotificationBell />

        <div className="admin-header-user">
          <UserCircle size={32} />

          <div className="admin-header-user-info">
            <span className="admin-header-user-name">Admin</span>

            <span className="admin-header-user-role">Administrator</span>
          </div>
        </div>
      </div>
    </header>
  );
}

export default AdminHeader;
