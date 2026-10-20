import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import shoreNotificationService from "../modules/shore/services/shoreNotificationService";

export function useShoreNotifications() {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    shoreNotificationService.connect((newNotif) => {
      setUnreadCount((prev) => prev + 1);
      setNotifications((prev) => [newNotif, ...prev]);
    });

    return () => shoreNotificationService.disconnect();
  }, []);

  const onItemClick = useCallback(
    (item) => {
      setUnreadCount((prev) => Math.max(0, prev - 1));
      if (item.actionLink) navigate(item.actionLink);
    },
    [navigate],
  );

  return {
    notifications,
    unreadCount,
    onItemClick,
    title: "Thông báo Shore",
  };
}
