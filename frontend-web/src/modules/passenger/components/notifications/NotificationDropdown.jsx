import { Bell, CheckCheck, RefreshCw, X } from "react-bootstrap-icons";

import "./NotificationDropdown.css";

const getNotificationIcon = (type) => {
  const icons = {
    FEEDBACK_INVITATION: "⭐",
    BOOKING_CONFIRMED: "🎫",
    BOOKING_CANCELLED: "❌",
    PENDING_PAYMENT: "💳",
    TOUR_REMINDER: "🚢",
    BILL_PAID: "💰",
  };

  return icons[type] || "🔔";
};

const formatDate = (date) => {
  if (!date) return "";

  return new Intl.DateTimeFormat("vi-VN", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(new Date(date));
};

const NotificationDropdown = ({
  notifications = [],
  unreadCount = 0,
  loading = false,
  onMarkAsRead,
  onMarkAllAsRead,
  onRefresh,
  onClose,
  onNotificationClick,
}) => {
  const handleNotificationClick = (notification) => {
    onNotificationClick?.(notification);
  };

  return (
    <div className="notification-dropdown">
      <div className="notification-dropdown__header">
        <div>
          <h6>Thông báo</h6>
          <small>
            {unreadCount > 0
              ? `${unreadCount} thông báo chưa đọc`
              : "Bạn đã đọc tất cả thông báo"}
          </small>
        </div>

        <div className="notification-dropdown__actions">
          <button type="button" title="Làm mới" onClick={onRefresh}>
            <RefreshCw size={16} />
          </button>

          <button type="button" title="Đóng" onClick={onClose}>
            <X size={18} />
          </button>
        </div>
      </div>

      {unreadCount > 0 && (
        <div className="notification-dropdown__toolbar">
          <button type="button" onClick={onMarkAllAsRead}>
            <CheckCheck size={15} />
            Đánh dấu tất cả đã đọc
          </button>
        </div>
      )}

      <div className="notification-dropdown__list">
        {loading ? (
          <div className="notification-dropdown__empty">
            Đang tải thông báo...
          </div>
        ) : notifications.length === 0 ? (
          <div className="notification-dropdown__empty">
            <Bell size={28} />
            <p>Chưa có thông báo nào</p>
          </div>
        ) : (
          notifications.map((notification) => (
            <button
              type="button"
              key={notification.id}
              className={`notification-item ${
                !notification.isRead ? "notification-item--unread" : ""
              }`}
              onClick={() => handleNotificationClick(notification)}
            >
              <span className="notification-item__icon">
                {getNotificationIcon(notification.type)}
              </span>

              <span className="notification-item__content">
                <strong>{notification.title}</strong>

                <span className="notification-item__message">
                  {notification.message}
                </span>

                <small>{formatDate(notification.createdAt)}</small>
              </span>

              {!notification.isRead && (
                <span className="notification-item__unread-dot" />
              )}
            </button>
          ))
        )}
      </div>

      <div className="notification-dropdown__footer">
        {notifications.length > 0 && (
          <span>Hiển thị {notifications.length} thông báo</span>
        )}
      </div>
    </div>
  );
};

export default NotificationDropdown;
