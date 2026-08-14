import { Bell } from "lucide-react";

import "../../styles/admin/NotificationBell.css";

function NotificationBell() {
  return (
    <button type="button" className="notification-bell" title="Thông báo">
      <Bell size={21} />

      <span className="notification-badge">3</span>
    </button>
  );
}

export default NotificationBell;
