import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export const useTourSocket = (tourId, onBookingScanned) => {
  const [socketStatus, setSocketStatus] = useState("Đang ngắt kết nối");

  useEffect(() => {
    // Nếu chưa có tourId thì không kết nối
    if (!tourId) {
      setSocketStatus("Vui lòng chọn Tour để kết nối");
      return;
    }

    setSocketStatus("Đang kết nối...");

    // Dùng thẳng port 8082 của booking-service (hoặc qua gateway nếu đã bỏ globalcors trùng)
    const socket = new SockJS("http://localhost:8082/ws-booking");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        setSocketStatus("🟢 Đã kết nối Real-time");

        stompClient.subscribe(`/topic/tour/${tourId}/scans`, (message) => {
          try {
            const notification = JSON.parse(message.body);
            console.log("[WS] Nhận tín hiệu quét từ POS:", notification);
            if (onBookingScanned) {
              onBookingScanned(notification);
            }
          } catch (err) {
            console.error("Lỗi parse tin nhắn socket:", err);
          }
        });
      },
      onStompError: (frame) => {
        setSocketStatus("🔴 Lỗi kết nối WebSocket");
        console.error("Broker error: " + frame.headers["message"]);
      },
      onWebSocketClose: () => {
        setSocketStatus("🟡 Mất kết nối WebSocket");
      },
    });

    stompClient.activate();

    // Cleanup function chỉ chạy khi tourId thay đổi hoặc component unmount thực sự
    return () => {
      if (stompClient.active) {
        stompClient.deactivate();
      }
      setSocketStatus("Đang ngắt kết nối");
    };
  }, [tourId]); // Chỉ phụ thuộc vào tourId, loại bỏ việc re-trigger do callback

  return { socketStatus };
};
