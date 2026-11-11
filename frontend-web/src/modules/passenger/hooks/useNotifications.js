import { useState, useEffect, useCallback } from "react";

import { useAuth } from "../../../context/AuthContext";
import notificationService from "../services/notificationService";
import notificationSocketService from "../services/notificationSocketService";

const useNotifications = () => {
  const { user, loading: authLoading } = useAuth();

  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  const [loading, setLoading] = useState(false);
  const [socketConnected, setSocketConnected] = useState(false);
  const [error, setError] = useState(null);

  const isPassenger = !authLoading && user?.role === "PASSENGER";

  // ==========================
  // Load notifications
  // ==========================
  const loadNotifications = useCallback(async () => {
    if (!isPassenger) return;

    setLoading(true);
    setError(null);

    try {
      const [notificationData, unreadData] = await Promise.all([
        notificationService.getNotifications(),
        notificationService.getUnreadCount(),
      ]);

      setNotifications(Array.isArray(notificationData) ? notificationData : []);

      setUnreadCount(Number(unreadData) || 0);
    } catch (err) {
      console.error("Không thể tải thông báo:", err);
      setError("Không thể tải danh sách thông báo.");
    } finally {
      setLoading(false);
    }
  }, [isPassenger]);

  // ==========================
  // Handle realtime notification
  // ==========================
  const handleRealtimeMessage = useCallback((payload) => {
    if (!payload) return;

    const { eventType, notification, unreadCount: latestUnreadCount } = payload;

    if (typeof latestUnreadCount === "number") {
      setUnreadCount(latestUnreadCount);
    }

    if (eventType === "NEW_NOTIFICATION" && notification) {
      setNotifications((previous) => {
        const existed = previous.some((item) => item.id === notification.id);

        if (existed) return previous;

        return [notification, ...previous];
      });
    }
  }, []);

  // ==========================
  // Connect notification socket
  // ==========================
  useEffect(() => {
    if (authLoading || !isPassenger) {
      setSocketConnected(false);
      setNotifications([]);
      setUnreadCount(0);
      return;
    }

    let socketConnection = null;

    socketConnection = notificationSocketService.createClient(
      () => {
        socketConnection?.subscribe();
        setSocketConnected(true);
      },
      handleRealtimeMessage,
      () => {
        setSocketConnected(false);
      },
    );

    loadNotifications();

    return () => {
      socketConnection?.disconnect();
      socketConnection = null;
      setSocketConnected(false);
    };
  }, [
    authLoading,
    isPassenger,
    user?.username,
    loadNotifications,
    handleRealtimeMessage,
  ]);

  // ==========================
  // Mark one notification as read
  // ==========================
  const markAsRead = useCallback(async (notificationId) => {
    try {
      const updatedNotification =
        await notificationService.markAsRead(notificationId);

      setNotifications((previous) =>
        previous.map((item) =>
          item.id === notificationId ? updatedNotification : item,
        ),
      );

      setUnreadCount((previous) => Math.max(0, previous - 1));

      return true;
    } catch (err) {
      console.error("Không thể đánh dấu thông báo đã đọc:", err);
      setError("Không thể cập nhật trạng thái thông báo.");
      return false;
    }
  }, []);

  // ==========================
  // Mark all notifications as read
  // ==========================
  const markAllAsRead = useCallback(async () => {
    try {
      await notificationService.markAllAsRead();

      setNotifications((previous) =>
        previous.map((item) => ({
          ...item,
          isRead: true,
        })),
      );

      setUnreadCount(0);

      return true;
    } catch (err) {
      console.error("Không thể đánh dấu tất cả đã đọc:", err);
      setError("Không thể cập nhật trạng thái thông báo.");
      return false;
    }
  }, []);

  return {
    notifications,
    unreadCount,

    loading,
    authLoading,
    socketConnected,
    error,

    isPassenger,

    loadNotifications,
    markAsRead,
    markAllAsRead,
  };
};

export default useNotifications;
