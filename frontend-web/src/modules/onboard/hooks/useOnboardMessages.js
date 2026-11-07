import { useCallback, useEffect, useRef, useState } from "react";

import onboardMessageService from "../services/onboardMessageService";
import chatSocketService from "../../shore/services/chatSocketService";

const normalizeList = (data) => {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(data?.data)) return data.data;

  return [];
};

const sortMessagesOldestFirst = (data) => {
  return normalizeList(data).sort(
    (a, b) => new Date(a.sentAt).getTime() - new Date(b.sentAt).getTime(),
  );
};

const useOnboardMessages = () => {
  const [conversations, setConversations] = useState([]);
  const [selectedConversationId, setSelectedConversationId] = useState(null);

  const [participants, setParticipants] = useState([]);
  const [messages, setMessages] = useState([]);

  const [loadingConversations, setLoadingConversations] = useState(false);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [sending, setSending] = useState(false);

  const [error, setError] = useState(null);
  const [socketConnected, setSocketConnected] = useState(false);

  const selectedIdRef = useRef(null);

  useEffect(() => {
    selectedIdRef.current = selectedConversationId;
  }, [selectedConversationId]);

  // Thêm tin nhắn nếu chưa tồn tại.
  const appendMessage = useCallback((newMessage) => {
    if (!newMessage?.id) return;

    setMessages((previous) => {
      const exists = previous.some(
        (message) => String(message.id) === String(newMessage.id),
      );

      if (exists) return previous;

      return sortMessagesOldestFirst([...previous, newMessage]);
    });
  }, []);

  // Lấy danh sách conversation.
  const loadConversations = useCallback(async () => {
    setLoadingConversations(true);
    setError(null);

    try {
      const data = await onboardMessageService.getConversations();
      const result = normalizeList(data);

      setConversations(result);

      setSelectedConversationId((currentId) => {
        if (
          currentId &&
          result.some((item) => String(item.id) === String(currentId))
        ) {
          return currentId;
        }

        return result[0]?.id || null;
      });
    } catch (err) {
      console.error("LOAD ONBOARD CONVERSATIONS ERROR:", err);
      setError(err);
      setConversations([]);
    } finally {
      setLoadingConversations(false);
    }
  }, []);

  useEffect(() => {
    loadConversations();
  }, [loadConversations]);

  // Lấy participants và lịch sử tin nhắn.
  const loadConversationMessages = useCallback(async (conversationId) => {
    if (!conversationId) return;

    setLoadingMessages(true);
    setError(null);

    try {
      const [participantData, messageData] = await Promise.all([
        onboardMessageService.getParticipants(conversationId),
        onboardMessageService.getMessages(conversationId),
      ]);

      // Tránh response cũ ghi đè conversation mới được chọn.
      if (String(selectedIdRef.current) !== String(conversationId)) {
        return;
      }

      setParticipants(normalizeList(participantData));
      setMessages(sortMessagesOldestFirst(messageData));
    } catch (err) {
      console.error("LOAD ONBOARD CHAT MESSAGES ERROR:", err);

      if (String(selectedIdRef.current) === String(conversationId)) {
        setError(err);
        setParticipants([]);
        setMessages([]);
      }
    } finally {
      if (String(selectedIdRef.current) === String(conversationId)) {
        setLoadingMessages(false);
      }
    }
  }, []);

  useEffect(() => {
    if (!selectedConversationId) {
      setMessages([]);
      setParticipants([]);
      setLoadingMessages(false);
      return;
    }

    setMessages([]);
    setParticipants([]);

    loadConversationMessages(selectedConversationId);
  }, [selectedConversationId, loadConversationMessages]);

  // Kết nối WebSocket và subscribe conversation đang chọn.
  useEffect(() => {
    if (!selectedConversationId) {
      setSocketConnected(false);
      return;
    }

    let subscription = null;
    let active = true;

    const socket = chatSocketService.createClient(
      () => {
        if (!active) return;

        setSocketConnected(true);
        subscription = socket.subscribe(selectedConversationId);
      },
      (newMessage) => {
        if (!active) return;

        if (
          String(newMessage.conversationId) !== String(selectedConversationId)
        ) {
          return;
        }

        appendMessage(newMessage);

        // Cập nhật thời gian hội thoại khi có tin nhắn mới.
        setConversations((previous) =>
          previous
            .map((conversation) =>
              String(conversation.id) === String(selectedConversationId)
                ? {
                    ...conversation,
                    updatedAt: newMessage.sentAt,
                  }
                : conversation,
            )
            .sort(
              (a, b) =>
                new Date(b.updatedAt || b.createdAt) -
                new Date(a.updatedAt || a.createdAt),
            ),
        );
      },
      () => {
        if (active) {
          setSocketConnected(false);
        }
      },
    );

    return () => {
      active = false;
      subscription?.unsubscribe();
      socket.client.deactivate();
      setSocketConnected(false);
    };
  }, [selectedConversationId, appendMessage]);

  // Gửi tin nhắn.
  const sendMessage = useCallback(
    async (content) => {
      if (!selectedConversationId || !content.trim() || sending) {
        return;
      }

      setSending(true);
      setError(null);

      try {
        const newMessage = await onboardMessageService.sendMessage(
          selectedConversationId,
          content.trim(),
        );

        appendMessage(newMessage);

        return newMessage;
      } catch (err) {
        console.error("SEND ONBOARD CHAT MESSAGE ERROR:", err);
        setError(err);
        throw err;
      } finally {
        setSending(false);
      }
    },
    [selectedConversationId, sending, appendMessage],
  );

  const selectedConversation =
    conversations.find(
      (conversation) =>
        String(conversation.id) === String(selectedConversationId),
    ) || null;

  // Participant có vai trò MANAGER là nhân viên quản lý hội thoại.
  // Dùng ID này để ChatWindow phân biệt tin nhắn bên phải và bên trái.
  const currentManagerId =
    participants.find(
      (participant) => participant.participantRole === "MANAGER",
    )?.userId || null;

  return {
    conversations,
    selectedConversation,
    selectedConversationId,
    setSelectedConversationId,

    participants,
    messages,
    currentManagerId,

    loadingConversations,
    loadingMessages,
    sending,

    error,
    socketConnected,

    loadConversations,
    loadConversationMessages,
    sendMessage,
  };
};

export default useOnboardMessages;
