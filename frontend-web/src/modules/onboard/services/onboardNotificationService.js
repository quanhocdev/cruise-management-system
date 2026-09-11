import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const SOCKET_URL = "http://localhost:8080/ws-notification";

class OnboardNotificationService {
  constructor() {
    this.stompClient = null;
  }

  connect(onNotificationReceived) {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(SOCKET_URL),
      reconnectDelay: 5000,
      onConnect: () => {
        // Onboard chỉ lắng nghe kênh hoạt động trên tàu (activity_cruise)
        this.stompClient.subscribe(
          "/topic/notifications/activity_cruise",
          (message) => {
            const notification = JSON.parse(message.body);
            onNotificationReceived(notification);
          },
        );
      },
      onStompError: (frame) => {
        console.error(
          "==> [WebSocket Error] Onboard: ",
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

export default new OnboardNotificationService();
