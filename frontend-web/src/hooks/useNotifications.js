import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

export function useNotifications(service, title = "Thông báo") {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    service.connect((newNotif) => {
      setUnreadCount((prev) => prev + 1);
      setNotifications((prev) => [newNotif, ...prev]);
    });
    return () => service.disconnect();
  }, [service]);

  const onItemClick = useCallback(
    (item) => {
      setUnreadCount((prev) => Math.max(0, prev - 1));
      if (item.actionLink) navigate(item.actionLink);
    },
    [navigate],
  );

  return { notifications, unreadCount, onItemClick, title };
}
