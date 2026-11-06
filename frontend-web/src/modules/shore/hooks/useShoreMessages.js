import { useCallback, useEffect, useRef, useState } from "react";

import shoreMessageService from "../services/shoreMessageService";
import chatSocketService from "../services/chatSocketService";

const normalizeList = (data) => {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(data?.data)) return data.data;

  return [];
};

const useShoreMessages = () => {
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
      const exists = previous.some((message) => message.id === newMessage.id);

      if (exists) return previous;

      return [...previous, newMessage];
    });
  }, []);

  // Lấy danh sách conversation.
  const loadConversations = useCallback(async () => {
    setLoadingConversations(true);
    setError(null);

    try {
      const data = await shoreMessageService.getConversations();
      const result = normalizeList(data);

      setConversations(result);

      setSelectedConversationId((currentId) => {
        if (currentId && result.some((item) => item.id === currentId)) {
          return currentId;
        }

        return result[0]?.id || null;
      });
    } catch (err) {
      console.error("LOAD SHORE CONVERSATIONS ERROR:", err);
      setError(err);
      setConversations([]);
    } finally {
      setLoadingConversations(false);
    }
  }, []);

  useEffect(() => {
    loadConversations();
  }, [loadConversations]);

  // Lấy chi tiết conversation, participants và lịch sử tin nhắn.
  const loadConversationMessages = useCallback(async (conversationId) => {
    if (!conversationId) return;

    setLoadingMessages(true);
    setError(null);

    try {
      const [participantData, messageData] = await Promise.all([
        shoreMessageService.getParticipants(conversationId),
        shoreMessageService.getMessages(conversationId),
      ]);

      if (selectedIdRef.current !== conversationId) return;

      setParticipants(normalizeList(participantData));
      setMessages(normalizeList(messageData));
    } catch (err) {
      console.error("LOAD CHAT MESSAGES ERROR:", err);
      setError(err);
      setParticipants([]);
      setMessages([]);
    } finally {
      if (selectedIdRef.current === conversationId) {
        setLoadingMessages(false);
      }
    }
  }, []);

  useEffect(() => {
    if (!selectedConversationId) {
      setMessages([]);
      setParticipants([]);
      return;
    }

    setMessages([]);
    setParticipants([]);

    loadConversationMessages(selectedConversationId);
  }, [selectedConversationId, loadConversationMessages]);

  // Kết nối WebSocket và subscribe conversation đang chọn.
  useEffect(() => {
    if (!selectedConversationId) return;

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

        if (newMessage.conversationId !== selectedConversationId) {
          return;
        }

        appendMessage(newMessage);

        // Cập nhật thời gian hội thoại khi có tin mới.
        setConversations((previous) =>
          previous
            .map((conversation) =>
              conversation.id === selectedConversationId
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
        setSocketConnected(false);
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
        const newMessage = await shoreMessageService.sendMessage(
          selectedConversationId,
          content.trim(),
        );

        appendMessage(newMessage);

        return newMessage;
      } catch (err) {
        console.error("SEND CHAT MESSAGE ERROR:", err);
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
      (conversation) => conversation.id === selectedConversationId,
    ) || null;

  // Manager participant là chính Manager đang đăng nhập
  // trong conversation của SHORE.
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

export default useShoreMessages;
