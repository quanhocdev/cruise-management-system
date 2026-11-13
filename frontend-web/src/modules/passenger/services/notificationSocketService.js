import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const getSocketUrl = () => {
  const apiBaseUrl =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

  const gatewayUrl = apiBaseUrl.replace(/\/api\/?$/, "").replace(/\/$/, "");

  return `${gatewayUrl}/ws-notification`;
};

const notificationSocketService = {
  createClient: (onConnected, onMessage, onError) => {
    const client = new Client({
      webSocketFactory: () =>
        new SockJS(getSocketUrl(), null, {
          withCredentials: true,
        }),

      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,

      onConnect: () => {
        console.log("Notification WebSocket connected");
        onConnected?.();
      },

      onStompError: (frame) => {
        console.error("Notification STOMP error:", frame.headers.message);
        onError?.(frame);
      },

      onWebSocketError: (error) => {
        console.error("Notification WebSocket error:", error);
        onError?.(error);
      },
    });

    client.activate();

    return {
      client,

      subscribe: () => {
        if (!client.connected) return null;

        return client.subscribe("/user/queue/notifications", (frame) => {
          try {
            const payload = JSON.parse(frame.body);
            onMessage?.(payload);
          } catch (error) {
            console.error("Cannot parse notification message:", error);
          }
        });
      },

      disconnect: () => {
        if (client.active) {
          client.deactivate();
        }
      },
    };
  },
};

export default notificationSocketService;
