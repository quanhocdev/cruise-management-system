// src/modules/convenience/services/convenienceNotificationService.js
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

// URL trỏ đến endpoint WebSocket của Backend đã cấu hình (ví dụ cổng 8080)
const SOCKET_URL = "http://localhost:8080/ws-notification";

class ConvenienceNotificationService {
  constructor() {
    this.stompClient = null;
  }

  // Kết nối và đăng ký lắng nghe 2 kênh product và service
  connect(onNotificationReceived) {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(SOCKET_URL),
      debug: (str) => {
        // console.log(str); // Bật nếu muốn debug log STOMP
      },
      reconnectDelay: 5000, // Tự động kết nối lại sau 5 giây nếu mất mạng
      onConnect: () => {
        console.log(
          "==> [WebSocket] Đã kết nối thành công tới Notification Server",
        );

        // Lắng nghe kênh Product
        this.stompClient.subscribe(
          "/topic/notifications/product",
          (message) => {
            const notification = JSON.parse(message.body);
            onNotificationReceived(notification);
          },
        );

        // Lắng nghe kênh Service
        this.stompClient.subscribe(
          "/topic/notifications/service",
          (message) => {
            const notification = JSON.parse(message.body);
            onNotificationReceived(notification);
          },
        );
      },
      onStompError: (frame) => {
        console.error(
          "==> [WebSocket Error] Lỗi kết nối STOMP: ",
          frame.headers["message"],
        );
      },
    });

    this.stompClient.activate();
  }

  // Ngắt kết nối khi component unmount
  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      console.log("==> [WebSocket] Đã ngắt kết nối");
    }
  }
}

export default new ConvenienceNotificationService();
