import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const SOCKET_URL = "http://localhost:8080/ws-notification";

class ShoreNotificationService {
  constructor() {
    this.stompClient = null;
  }

  connect(onNotificationReceived) {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(SOCKET_URL),
      reconnectDelay: 5000,
      onConnect: () => {
        // Shore chỉ lắng nghe kênh điểm dừng chân / tham quan bờ (activity_visit)
        this.stompClient.subscribe(
          "/topic/notifications/activity_visit",
          (message) => {
            const notification = JSON.parse(message.body);
            onNotificationReceived(notification);
          },
        );
      },
      onStompError: (frame) => {
        console.error(
          "==> [WebSocket Error] Shore: ",
          frame.headers["message"],
        );
      },
    });

    this.stompClient.activate();
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }
}

export default new ShoreNotificationService();
