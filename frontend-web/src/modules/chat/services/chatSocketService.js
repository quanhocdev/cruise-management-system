import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const getSocketUrl = () => {
  const apiBaseUrl =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

  const gatewayUrl = apiBaseUrl.replace(/\/api\/?$/, "").replace(/\/$/, "");

  return `${gatewayUrl}/ws-chat`;
};

const chatSocketService = {
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
        console.log("Chat WebSocket connected");
        onConnected?.();
      },

      onStompError: (frame) => {
        console.error("Chat STOMP error:", frame.headers.message);
        onError?.(frame);
      },

      onWebSocketError: (error) => {
        console.error("Chat WebSocket error:", error);
        onError?.(error);
      },
    });

    client.activate();

    return {
      client,

      subscribe: (conversationId) => {
        if (!client.connected) return null;

        return client.subscribe(
          `/topic/conversations/${conversationId}`,
          (frame) => {
            try {
              const message = JSON.parse(frame.body);
              onMessage?.(message);
            } catch (error) {
              console.error("Cannot parse chat message:", error);
            }
          },
        );
      },
    };
  },
};

export default chatSocketService;
