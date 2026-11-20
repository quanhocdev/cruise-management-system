import { useCallback, useEffect, useRef, useState } from "react";
import bookingQueueSocketService from "../services/bookingQueueSocketService";

export default function useBookingQueue(enabled = true) {
  const [socketConnected, setSocketConnected] = useState(false);
  const [queueStatus, setQueueStatus] = useState(null);
  const [socketError, setSocketError] = useState(null);

  const socketRef = useRef(null);

  const connect = useCallback(() => {
    if (socketRef.current?.client?.active) {
      return;
    }

    setSocketError(null);

    socketRef.current = bookingQueueSocketService.createClient(
      () => {
        console.log("[BOOKING QUEUE] Socket ready");
        setSocketConnected(true);
      },

      (event) => {
        console.log("[BOOKING QUEUE] Received event:", event);
        setQueueStatus(event);
      },

      (error) => {
        console.error("[BOOKING QUEUE] Socket error:", error);
        setSocketError(error);
        setSocketConnected(false);
      },
    );
  }, []);

  const disconnect = useCallback(async () => {
    const socket = socketRef.current;

    socketRef.current = null;

    if (socket) {
      await socket.disconnect();
    }

    setSocketConnected(false);
  }, []);

  useEffect(() => {
    if (!enabled) {
      return;
    }

    connect();

    return () => {
      disconnect();
    };
  }, [enabled, connect, disconnect]);

  const clearQueueStatus = useCallback(() => {
    setQueueStatus(null);
  }, []);

  return {
    socketConnected,
    queueStatus,
    socketError,
    connect,
    disconnect,
    clearQueueStatus,
  };
}
