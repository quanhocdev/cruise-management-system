import { Bell } from "lucide-react";

import "../../styles/onboard/NotificationBell.css";

function OnboardNotificationBell() {
  const notificationCount = 3; // Ví dụ có 3 thông báo

  return (
    <button
      type="button"
      className="onboard-notification-button"
      title="Thông báo"
    >
      <Bell size={20} />

      {notificationCount > 0 && (
        <span className="onboard-notification-badge">
          {notificationCount > 99 ? "99+" : notificationCount}
        </span>
      )}
    </button>
  );
}

export default OnboardNotificationBell;
