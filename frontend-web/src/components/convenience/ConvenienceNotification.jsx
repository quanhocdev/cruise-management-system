import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Bell } from "lucide-react";
import convenienceNotificationService from "../../modules/convenience/services/convenienceNotificationService";
import "../../styles/convenience/ConvenienceNotification.css";

export default function NotificationBell() {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    convenienceNotificationService.connect((newNotif) => {
      setUnreadCount((prev) => prev + 1);
      setNotifications((prev) => [newNotif, ...prev]);
    });

    // Cleanup khi component unmount thì ngắt kết nối socket
    return () => {
      convenienceNotificationService.disconnect();
    };
  }, []);

  const handleNotificationClick = (item) => {
    setUnreadCount((prev) => Math.max(0, prev - 1));
    setIsOpen(false);
    navigate(item.actionLink);
  };

  return (
    <div className="relative">
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="relative p-2 rounded-full hover:bg-gray-100 transition flex items-center justify-center text-gray-700"
      >
        <Bell size={24} />
        {unreadCount > 0 && (
          <span className="absolute top-0 right-0 bg-red-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
            {unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white shadow-xl rounded-lg overflow-hidden z-50 border border-gray-100">
          <div className="p-3 font-semibold text-gray-700 border-b bg-gray-50 flex justify-between items-center">
            <span>Thông báo</span>
            <span className="text-xs text-blue-600 font-normal">
              {unreadCount} mới
            </span>
          </div>
          <div className="max-h-72 overflow-y-auto">
            {notifications.length === 0 ? (
              <div className="p-4 text-center text-gray-400 text-sm">
                Chưa có thông báo nào
              </div>
            ) : (
              notifications.map((item) => (
                <div
                  key={item.id}
                  onClick={() => handleNotificationClick(item)}
                  className="p-3 border-b hover:bg-blue-50 cursor-pointer text-sm transition"
                >
                  <p className="font-semibold text-gray-800">{item.title}</p>
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
