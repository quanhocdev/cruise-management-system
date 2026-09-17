import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export const useTourSocket = (tourId, onBookingScanned) => {
  const [socketStatus, setSocketStatus] = useState("Đang ngắt kết nối");

  useEffect(() => {
    if (!tourId) return;

    const socket = new SockJS("http://localhost:8080/ws-booking");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        setSocketStatus("🟢 Đã kết nối Real-time");

        // Subscribe vào kênh của tour
        stompClient.subscribe(`/topic/tour/${tourId}/scans`, (message) => {
          try {
            const notification = JSON.parse(message.body);
            console.log("[WS] Nhận tín hiệu quét từ POS:", notification);

            // Gọi callback trả dữ liệu về cho Component sử dụng
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
      onDisconnect: () => {
        setSocketStatus("🟡 Mất kết nối WebSocket");
      },
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
      setSocketStatus("Đang ngắt kết nối");
    };
  }, [tourId, onBookingScanned]);

  return { socketStatus };
};
