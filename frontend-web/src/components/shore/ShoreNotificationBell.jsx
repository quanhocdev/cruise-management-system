import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Bell } from "lucide-react";
import shoreNotificationService from "../../modules/shore/services/shoreNotificationService";
import "../../styles/shore/NotificationBell.css";

function ShoreNotificationBell() {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    shoreNotificationService.connect((newNotif) => {
      setUnreadCount((prev) => prev + 1);
      setNotifications((prev) => [newNotif, ...prev]);
    });

    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      shoreNotificationService.disconnect();
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
        className="relative bg-transparent border-none text-gray-600 cursor-pointer p-2 rounded-full flex items-center justify-center hover:bg-gray-100 transition"
        title="Thông báo"
        onClick={() => setIsOpen(!isOpen)}
      >
        <Bell size={21} />

        {unreadCount > 0 && (
          <span className="absolute top-1 right-1 bg-red-500 text-white text-[10px] font-bold h-4 min-w-[16px] rounded-full flex items-center justify-center px-1">
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white shadow-xl rounded-lg overflow-hidden z-50 border border-gray-100 text-left">
          <div className="p-3 font-semibold text-gray-700 border-b bg-gray-50 flex justify-between items-center text-sm">
            <span>Thông báo Shore</span>
            <span className="text-xs text-blue-600 font-normal">
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
                  className="p-3 border-b hover:bg-blue-50 cursor-pointer text-sm transition"
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

export default ShoreNotificationBell;
