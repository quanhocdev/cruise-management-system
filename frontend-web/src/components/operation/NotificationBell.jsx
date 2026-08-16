import { Bell } from "lucide-react";

import "../../styles/operation/NotificationBell.css";

function NotificationBell() {
  const notificationCount = 0;

  return (
    <button
      type="button"
      className="operation-notification-button"
      title="Thông báo"
    >
      <Bell size={20} />

      {notificationCount > 0 && (
        <span className="operation-notification-badge">
          {notificationCount > 99 ? "99+" : notificationCount}
        </span>
      )}
    </button>
  );
}

export default NotificationBell;
