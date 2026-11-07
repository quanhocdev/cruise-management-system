import { Fragment, useEffect, useRef } from "react";
import { MessageCircle, UserRound } from "lucide-react";

import MessageBubble from "./MessageBubble";
import MessageComposer from "./MessageComposer";

import "./ChatWindow.css";

const PASSENGER_ROLES = ["PASSENGER", "CUSTOMER", "USER", "CLIENT"];

const checkIsMine = (message, currentManagerId) => {
  if (currentManagerId && message.senderUserId != null) {
    return String(message.senderUserId) === String(currentManagerId);
  }

  const senderRole = String(
    message.senderRole || message.senderType || "",
  ).toUpperCase();

  if (PASSENGER_ROLES.includes(senderRole)) {
    return false;
  }

  if (senderRole) {
    return true;
  }

  return false;
};

const getDayKey = (value) => {
  if (!value) return "unknown";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return "unknown";

  return `${date.getFullYear()}-${date.getMonth()}-${date.getDate()}`;
};

const formatDayLabel = (value) => {
  if (!value) return "";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return "";

  const today = new Date();
  const yesterday = new Date();

  yesterday.setDate(today.getDate() - 1);

  if (getDayKey(date) === getDayKey(today)) {
    return "Hôm nay";
  }

  if (getDayKey(date) === getDayKey(yesterday)) {
    return "Hôm qua";
  }

  return date.toLocaleDateString("vi-VN", {
    weekday: "long",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

const ChatWindow = ({
  conversation,
  messages = [],
  currentManagerId,
  loading,
  sending,
  socketConnected,
  onSend,
}) => {
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({
      behavior: "smooth",
      block: "end",
    });
  }, [messages, loading]);

  if (!conversation) {
    return (
      <div className="onboard-chat-empty">
        <div className="onboard-chat-empty-icon">
          <MessageCircle size={34} />
        </div>

        <h3>Chưa chọn hội thoại</h3>

        <p>Chọn một cuộc trò chuyện bên trái để bắt đầu hỗ trợ hành khách.</p>
      </div>
    );
  }

  const isClosed = conversation.status === "CLOSED";

  const renderMessages = () => {
    return messages.map((message, index) => {
      const isMine = checkIsMine(message, currentManagerId);
      const previousMessage = messages[index - 1];
      const nextMessage = messages[index + 1];

      const previousIsMine = previousMessage
        ? checkIsMine(previousMessage, currentManagerId)
        : null;

      const nextIsMine = nextMessage
        ? checkIsMine(nextMessage, currentManagerId)
        : null;

      const currentDay = getDayKey(message.sentAt);
      const previousDay = previousMessage
        ? getDayKey(previousMessage.sentAt)
        : null;

      const isFirstInGroup =
        !previousMessage ||
        previousIsMine !== isMine ||
        previousDay !== currentDay;

      const isLastInGroup =
        !nextMessage ||
        nextIsMine !== isMine ||
        getDayKey(nextMessage.sentAt) !== currentDay;

      const showDayDivider = !previousMessage || previousDay !== currentDay;

      return (
        <Fragment key={message.id}>
          {showDayDivider && (
            <div className="onboard-chat-day">
              <span>{formatDayLabel(message.sentAt)}</span>
            </div>
          )}

          <MessageBubble
            message={message}
            isMine={isMine}
            isFirstInGroup={isFirstInGroup}
            isLastInGroup={isLastInGroup}
          />
        </Fragment>
      );
    });
  };

  return (
    <div className="onboard-chat-window">
      <div className="onboard-chat-header">
        <div className="onboard-chat-user">
          <div className="onboard-chat-avatar">
            <UserRound size={21} />
            <span className="onboard-chat-avatar-dot" />
          </div>

          <div className="onboard-chat-user-info">
            <strong>
              {conversation.bookingCode
                ? `Booking ${conversation.bookingCode}`
                : `Booking #${conversation.bookingId || conversation.id}`}
            </strong>

            <span>
              Hành khách
              {" · "}
              {conversation.department || "ONBOARD"}
            </span>
          </div>
        </div>

        <div className="onboard-chat-connection">
          <span
            className={`onboard-chat-connection-dot ${
              socketConnected ? "connected" : ""
            }`}
          />

          {socketConnected ? "Realtime đã kết nối" : "Đang kết nối..."}
        </div>
      </div>

      <div className="onboard-chat-messages">
        {loading ? (
          <div className="onboard-chat-loading">
            <div className="onboard-chat-spinner" />
            <span>Đang tải tin nhắn...</span>
          </div>
        ) : messages.length === 0 ? (
          <div className="onboard-chat-no-messages">
            <MessageCircle size={32} />

            <strong>Chưa có tin nhắn</strong>

            <span>
              Hãy gửi lời chào để bắt đầu cuộc trò chuyện với hành khách.
            </span>
          </div>
        ) : (
          renderMessages()
        )}

        <div ref={messagesEndRef} />
      </div>

      {isClosed && (
        <div className="onboard-chat-closed">
          Cuộc hội thoại này đã đóng. Bạn không thể gửi thêm tin nhắn.
        </div>
      )}

      <MessageComposer onSend={onSend} sending={sending} disabled={isClosed} />
    </div>
  );
};

export default ChatWindow;
