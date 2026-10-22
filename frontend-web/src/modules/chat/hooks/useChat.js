// src/modules/chat/hooks/useChat.js

import { useCallback, useRef, useState } from "react";
import chatService from "../services/chatService";

const createSessionId = () => {
  if (globalThis.crypto?.randomUUID) {
    return globalThis.crypto.randomUUID();
  }

  return `chat-${Date.now()}-${Math.random().toString(36).slice(2, 12)}`;
};

const createMessage = (text, isUser, suggestions = []) => ({
  id: `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`,
  text,
  isUser,
  suggestions,
});

export default function useChat() {
  const sessionIdRef = useRef(createSessionId());
  const welcomeLoadedRef = useRef(false);
  const welcomeRequestRef = useRef(null);
  const messageRequestRef = useRef(false);

  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [isLoadingWelcome, setIsLoadingWelcome] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const [error, setError] = useState("");

  const openChat = useCallback(async () => {
    if (welcomeLoadedRef.current) return;

    // Tránh gọi welcome nhiều lần khi người dùng bấm nhanh.
    if (welcomeRequestRef.current) {
      return welcomeRequestRef.current;
    }

    setError("");
    setIsLoadingWelcome(true);

    const request = (async () => {
      try {
        const response = await chatService.openChat(sessionIdRef.current);

        if (!response?.message) {
          throw new Error("Backend chưa trả về lời chào.");
        }

        sessionIdRef.current = response.sessionId || sessionIdRef.current;

        setMessages((current) => [
          ...current,
          createMessage(
            response.message,
            false,
            Array.isArray(response.suggestions) ? response.suggestions : [],
          ),
        ]);

        welcomeLoadedRef.current = true;
      } catch (err) {
        setError(
          err.response?.status === 401
            ? "Phiên đăng nhập không hợp lệ hoặc đã hết hạn. Vui lòng đăng nhập lại."
            : "Chưa thể kết nối với trợ lý du lịch. Vui lòng thử mở lại khung chat.",
        );
      } finally {
        welcomeRequestRef.current = null;
        setIsLoadingWelcome(false);
      }
    })();

    welcomeRequestRef.current = request;
    return request;
  }, []);

  const sendMessage = useCallback(async (value) => {
    const message = value.trim();

    if (!message || messageRequestRef.current) return;

    messageRequestRef.current = true;
    setIsSending(true);
    setError("");

    setMessages((current) => [...current, createMessage(message, true)]);
    setInput("");

    try {
      const response = await chatService.sendMessage(
        message,
        sessionIdRef.current,
      );

      sessionIdRef.current = response.sessionId || sessionIdRef.current;

      setMessages((current) => [
        ...current,
        createMessage(
          response.message || "Xin lỗi, tôi chưa có câu trả lời phù hợp.",
          false,
        ),
      ]);
    } catch (err) {
      const errorMessage =
        err.response?.status === 401
          ? "Phiên đăng nhập không hợp lệ hoặc đã hết hạn. Vui lòng đăng nhập lại."
          : "Hiện tại chưa thể kết nối với trợ lý du lịch. Bạn vui lòng thử lại nhé.";

      setMessages((current) => [
        ...current,
        createMessage(errorMessage, false),
      ]);
    } finally {
      messageRequestRef.current = false;
      setIsSending(false);
    }
  }, []);

  const retryWelcome = useCallback(() => {
    return openChat();
  }, [openChat]);

  return {
    messages,
    input,
    setInput,
    isLoadingWelcome,
    isSending,
    error,
    openChat,
    sendMessage,
    retryWelcome,
  };
}
