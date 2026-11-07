// src/modules/passenger/hooks/usePassengerMessages.js

import { useCallback, useEffect, useState } from "react";
import passengerMessageService from "../services/passengerMessageService";
import chatSocketService from "../../shore/services/chatSocketService";

const normalizeList = (data) => {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(data?.items)) return data.items;
  if (Array.isArray(data?.data)) return data.data;
  return [];
};

export default function usePassengerMessages() {
  // Booking đã xác nhận thanh toán, lấy từ Chat Service
  const [bookings, setBookings] = useState([]);
  const [bookingsLoading, setBookingsLoading] = useState(false);
  const [bookingsError, setBookingsError] = useState("");

  const [conversations, setConversations] = useState([]);
  const [selectedConversationId, setSelectedConversationId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [participants, setParticipants] = useState([]);

  const [loading, setLoading] = useState(false);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [creating, setCreating] = useState(false);
  const [sending, setSending] = useState(false);
  const [socketConnected, setSocketConnected] = useState(false);
  const [error, setError] = useState("");

  const selectedConversation =
    conversations.find((item) => item.id === selectedConversationId) || null;

  // Load booking đã xác nhận thanh toán từ Chat Service
  const loadConfirmedBookings = useCallback(async () => {
    setBookingsLoading(true);
    setBookingsError("");

    try {
      const data = await passengerMessageService.getConfirmedBookings();
      const list = normalizeList(data);

      setBookings(list);
    } catch (err) {
      setBookings([]);

      setBookingsError(
        err.response?.data?.message ||
          "Không thể tải danh sách booking đã xác nhận.",
      );
    } finally {
      setBookingsLoading(false);
    }
  }, []);

  // Load danh sách hội thoại
  const loadConversations = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await passengerMessageService.getConversations();
      const list = normalizeList(data);

      setConversations(list);

      setSelectedConversationId((currentId) => {
        if (currentId && list.some((item) => item.id === currentId)) {
          return currentId;
        }

        return list[0]?.id ?? null;
      });
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách cuộc trò chuyện.",
      );
      setConversations([]);
    } finally {
      setLoading(false);
    }
  }, []);

  // Tạo hội thoại mới hoặc mở hội thoại tương ứng
  const createConversation = useCallback(async (bookingId, department) => {
    if (!bookingId || !department) {
      throw new Error("Vui lòng chọn booking và phòng ban.");
    }

    setCreating(true);
    setError("");

    try {
      const conversation = await passengerMessageService.createConversation({
        bookingId: Number(bookingId),
        department,
      });

      setConversations((current) => {
        const filtered = current.filter((item) => item.id !== conversation.id);

        return [conversation, ...filtered];
      });

      setSelectedConversationId(conversation.id);

      return conversation;
    } catch (err) {
      const message =
        err.response?.data?.message || "Không thể bắt đầu cuộc trò chuyện.";

      setError(message);
      throw err;
    } finally {
      setCreating(false);
    }
  }, []);

  // Chọn hội thoại
  const selectConversation = useCallback((conversationId) => {
    setSelectedConversationId(conversationId);
    setMessages([]);
    setParticipants([]);
    setError("");
  }, []);

  // Tải lịch sử tin nhắn và danh sách thành viên
  const loadConversationDetails = useCallback(async (conversationId) => {
    if (!conversationId) return;

    setMessagesLoading(true);
    setError("");

    try {
      const [messageData, participantData] = await Promise.all([
        passengerMessageService.getMessages(conversationId),
        passengerMessageService.getParticipants(conversationId),
      ]);

      setMessages(normalizeList(messageData));
      setParticipants(normalizeList(participantData));
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Không thể tải nội dung cuộc trò chuyện.",
      );
      setMessages([]);
      setParticipants([]);
    } finally {
      setMessagesLoading(false);
    }
  }, []);

  // Kết nối WebSocket theo hội thoại đang chọn
  useEffect(() => {
    if (!selectedConversationId) {
      setSocketConnected(false);
      return undefined;
    }

    let socketConnection = null;
    let subscription = null;
    let isActive = true;

    setSocketConnected(false);

    socketConnection = chatSocketService.createClient(
      // STOMP đã kết nối
      () => {
        if (!isActive) return;

        setSocketConnected(true);

        subscription = socketConnection?.subscribe(selectedConversationId);
      },

      // Nhận tin nhắn realtime
      (incomingMessage) => {
        if (!isActive) return;

        setMessages((current) => {
          const existed = current.some(
            (item) => item.id === incomingMessage.id,
          );

          if (existed) return current;

          return [...current, incomingMessage];
        });

        setConversations((current) =>
          current.map((conversation) =>
            conversation.id === selectedConversationId
              ? {
                  ...conversation,
                  updatedAt: incomingMessage.sentAt || conversation.updatedAt,
                }
              : conversation,
          ),
        );
      },

      // Lỗi kết nối
      (socketError) => {
        if (!isActive) return;

        console.error("Passenger chat socket error:", socketError);
        setSocketConnected(false);
      },
    );

    return () => {
      isActive = false;

      subscription?.unsubscribe();

      setSocketConnected(false);

      socketConnection?.client?.deactivate();
    };
  }, [selectedConversationId]);

  // Tải lịch sử mỗi khi chọn hội thoại
  useEffect(() => {
    if (selectedConversationId) {
      loadConversationDetails(selectedConversationId);
    } else {
      setMessages([]);
      setParticipants([]);
    }
  }, [selectedConversationId, loadConversationDetails]);

  // Gửi tin nhắn qua REST
  const sendMessage = useCallback(
    async (content) => {
      const trimmedContent = content?.trim();

      if (!trimmedContent || !selectedConversationId || sending) return;

      setSending(true);
      setError("");

      try {
        const sentMessage = await passengerMessageService.sendMessage(
          selectedConversationId,
          trimmedContent,
        );

        // Tránh thêm trùng nếu WebSocket đã nhận tin nhắn
        setMessages((current) => {
          const existed = current.some((item) => item.id === sentMessage.id);

          if (existed) return current;

          return [...current, sentMessage];
        });

        setConversations((current) =>
          current.map((conversation) =>
            conversation.id === selectedConversationId
              ? {
                  ...conversation,
                  updatedAt: sentMessage.sentAt || conversation.updatedAt,
                }
              : conversation,
          ),
        );

        return sentMessage;
      } catch (err) {
        const message =
          err.response?.data?.message || "Không thể gửi tin nhắn.";

        setError(message);
        throw err;
      } finally {
        setSending(false);
      }
    },
    [selectedConversationId, sending],
  );

  // Khởi tạo dữ liệu
  useEffect(() => {
    loadConfirmedBookings();
    loadConversations();
  }, [loadConfirmedBookings, loadConversations]);

  return {
    bookings,
    bookingsLoading,
    bookingsError,

    conversations,
    selectedConversation,
    selectedConversationId,
    messages,
    participants,

    loading,
    messagesLoading,
    creating,
    sending,
    socketConnected,
    error,

    loadConfirmedBookings,
    loadConversations,
    createConversation,
    selectConversation,
    sendMessage,
  };
}
