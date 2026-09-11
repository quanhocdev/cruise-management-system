import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Bell } from "lucide-react";
import onboardNotificationService from "../services/onboardNotificationService";
import "../../styles/onboard/NotificationBell.css";

function OnboardNotificationBell() {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    // Kết nối WebSocket thông qua service của Onboard
    onboardNotificationService.connect((newNotif) => {
      setUnreadCount((prev) => prev + 1);
      setNotifications((prev) => [newNotif, ...prev]);
    });

    // Đóng dropdown khi click ra bên ngoài
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      onboardNotificationService.disconnect();
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleNotificationClick = (item) => {
    setUnreadCount((prev) => Math.max(0, prev - 1));
    setIsOpen(false);
    navigate(item.actionLink);
  };

  return (
    <div className="relative inline-block" ref={dropdownRef}>
      <button
        type="button"
        className="onboard-notification-button"
        title="Thông báo"
        onClick={() => setIsOpen(!isOpen)}
      >
        <Bell size={20} />

        {unreadCount > 0 && (
          <span className="onboard-notification-badge">
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white shadow-xl rounded-lg overflow-hidden z-50 border border-gray-100 text-left">
          <div className="p-3 font-semibold text-gray-700 border-b bg-gray-50 flex justify-between items-center text-sm">
            <span>Thông báo Onboard</span>
            <span className="text-xs text-teal-600 font-normal">
              {unreadCount} mới
            </span>
          </div>
          <div className="max-h-72 overflow-y-auto">
            {notifications.length === 0 ? (
              <div className="p-4 text-center text-gray-400 text-sm">
                Chưa có thông báo mới
              </div>
            ) : (
              notifications.map((item) => (
                <div
                  key={item.id}
                  onClick={() => handleNotificationClick(item)}
                  className="p-3 border-b hover:bg-teal-50 cursor-pointer text-sm transition"
                >
                  <p className="font-semibold text-gray-800 text-xs">
                    {item.title}
                  </p>
                  <p className="text-gray-600 text-xs mt-0.5">{item.message}</p>
                  <span className="text-[10px] text-gray-400 mt-1 block">
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

export default OnboardNotificationBell;
