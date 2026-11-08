import { useCallback, useEffect, useRef, useState } from "react";

import chatMessageService from "../services/chatMessageService";
import chatSocketService from "../services/chatSocketService";

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

const useChatMessages = () => {
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

  const loadConversations = useCallback(async () => {
    setLoadingConversations(true);
    setError(null);

    try {
      const data = await chatMessageService.getConversations();
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
      console.error("LOAD CHAT CONVERSATIONS ERROR:", err);
      setError(err);
      setConversations([]);
      setSelectedConversationId(null);
    } finally {
      setLoadingConversations(false);
    }
  }, []);

  useEffect(() => {
    loadConversations();
  }, [loadConversations]);

  const loadConversationMessages = useCallback(async (conversationId) => {
    if (!conversationId) return;

    setLoadingMessages(true);
    setError(null);

    try {
      const [participantData, messageData] = await Promise.all([
        chatMessageService.getParticipants(conversationId),
        chatMessageService.getMessages(conversationId),
      ]);

      if (String(selectedIdRef.current) !== String(conversationId)) {
        return;
      }

      setParticipants(normalizeList(participantData));
      setMessages(sortMessagesOldestFirst(messageData));
    } catch (err) {
      console.error("LOAD CHAT MESSAGES ERROR:", err);

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

  const sendMessage = useCallback(
    async (content) => {
      if (!selectedConversationId || !content.trim() || sending) {
        return;
      }

      setSending(true);
      setError(null);

      try {
        const newMessage = await chatMessageService.sendMessage(
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
      (conversation) =>
        String(conversation.id) === String(selectedConversationId),
    ) || null;

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

export default useChatMessages;
