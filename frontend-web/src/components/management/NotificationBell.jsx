import { useEffect, useRef, useState } from "react";
import { Bell } from "lucide-react";
import "../../styles/management/notification.css";

function NotificationBell({
  notifications = [],
  unreadCount = 0,
  onItemClick,
  onOpen,
  title = "Thông báo",
}) {
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target)
      ) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleToggle = () => {
    const next = !isOpen;
    setIsOpen(next);
    if (next) onOpen?.();
  };

  const handleItemClick = (item) => {
    setIsOpen(false);

    onItemClick?.(item);
  };

  return (
    <div className="mgmt-notification-container" ref={containerRef}>
      <button
        type="button"
        className="mgmt-notification-button"
        title="Thông báo"
        onClick={handleToggle}
      >
        <Bell size={21} />
        {unreadCount > 0 && (
          <span className="mgmt-notification-badge">
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="mgmt-notification-dropdown">
          <div className="mgmt-notification-dropdown-header">
            <span>{title}</span>
            <span>{unreadCount} mới</span>
          </div>

          <div className="mgmt-notification-list">
            {notifications.length === 0 ? (
              <div className="mgmt-notification-empty">
                Chưa có thông báo mới
              </div>
            ) : (
              notifications.map((item) => (
                <div
                  key={item.id}
                  className="mgmt-notification-item"
                  onClick={() => handleItemClick(item)}
                >
                  <p className="mgmt-notification-item-title">{item.title}</p>
                  <p className="mgmt-notification-item-message">
                    {item.message}
                  </p>
                  <span className="mgmt-notification-item-time">
                    {new Date(item.createdAt).toLocaleTimeString()}
                  </span>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default NotificationBell;
