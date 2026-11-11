import { useState } from "react";
import { Bell } from "react-bootstrap-icons";

import NotificationDropdown from "./NotificationDropdown";
import "./NotificationBell.css";

const NotificationBell = ({
  notifications,
  unreadCount,
  loading,
  onMarkAsRead,
  onMarkAllAsRead,
  onRefresh,
  onNotificationClick,
}) => {
  const [open, setOpen] = useState(false);

  return (
    <div className="notification-bell">
      <button
        type="button"
        className="notification-bell__button"
        onClick={() => setOpen((previous) => !previous)}
        aria-label="Thông báo"
      >
        <Bell size={21} />

        {unreadCount > 0 && (
          <span className="notification-bell__badge">
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {open && (
        <>
          <div
            className="notification-bell__backdrop"
            onClick={() => setOpen(false)}
          />

          <NotificationDropdown
            notifications={notifications}
            unreadCount={unreadCount}
            loading={loading}
            onMarkAsRead={onMarkAsRead}
            onMarkAllAsRead={onMarkAllAsRead}
            onRefresh={onRefresh}
            onClose={() => setOpen(false)}
            onNotificationClick={onNotificationClick}
          />
        </>
      )}
    </div>
  );
};

export default NotificationBell;
