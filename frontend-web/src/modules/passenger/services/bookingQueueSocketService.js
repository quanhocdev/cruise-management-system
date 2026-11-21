import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const getSocketUrl = () => {
  const apiBaseUrl =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

  const gatewayUrl = apiBaseUrl.replace(/\/api\/?$/, "").replace(/\/$/, "");

  return `${gatewayUrl}/ws-booking`;
};

const bookingQueueSocketService = {
  createClient: (onConnected, onStatus, onError) => {
    const client = new Client({
      webSocketFactory: () =>
        new SockJS(getSocketUrl(), null, {
          withCredentials: true,
        }),

      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,

      onConnect: () => {
        console.log("[BOOKING WS] Connected");

        onConnected?.();

        client.subscribe("/user/queue/booking-status", (frame) => {
          try {
            const event = JSON.parse(frame.body);

            console.log("[BOOKING WS] Status:", event);

            onStatus?.(event);
          } catch (error) {
            console.error("[BOOKING WS] Cannot parse status:", error);
          }
        });
      },

      onStompError: (frame) => {
        console.error("[BOOKING WS] STOMP error:", frame.headers.message);

        onError?.(frame);
      },

      onWebSocketError: (error) => {
        console.error("[BOOKING WS] WebSocket error:", error);

        onError?.(error);
      },

      onWebSocketClose: () => {
        console.warn("[BOOKING WS] Connection closed");
      },
    });

    client.activate();

    return {
      client,

      disconnect: () => {
        if (client.active) {
          return client.deactivate();
        }
      },
    };
  },
};

export default bookingQueueSocketService;
