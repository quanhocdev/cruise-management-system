import { Fragment, useEffect, useRef } from "react";
import { MessageCircle, UserRound } from "lucide-react";

import MessageBubble from "./MessageBubble";
import MessageComposer from "./MessageComposer";

import "./ChatWindow.css";

const PASSENGER_ROLES = ["PASSENGER", "CUSTOMER", "USER", "CLIENT"];

/**
 * Xác định tin nhắn có phải của Shore (người đang đăng nhập) hay không.
 * Ưu tiên so sánh ID (ép về string để tránh lệch number/string),
 * nếu không có ID thì dựa vào role của người gửi.
 */
const checkIsMine = (message, currentManagerId) => {
  if (currentManagerId != null && message.senderUserId != null) {
    return String(message.senderUserId) === String(currentManagerId);
  }

  const role = String(
    message.senderRole || message.senderType || "",
  ).toUpperCase();

  if (role) return !PASSENGER_ROLES.includes(role);

  return false;
};

const getDayKey = (value) => (value ? new Date(value).toDateString() : "");

const formatDayLabel = (value) => {
  if (!value) return "";

  const date = new Date(value);
  const today = new Date();
  const yesterday = new Date();
  yesterday.setDate(today.getDate() - 1);

  if (date.toDateString() === today.toDateString()) return "Hôm nay";
  if (date.toDateString() === yesterday.toDateString()) return "Hôm qua";

  return date.toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

const ChatWindow = ({
  conversation,
  messages,
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
    });
  }, [messages]);

  if (!conversation) {
    return (
      <section className="shore-chat-empty">
        <div className="shore-chat-empty-icon">
          <MessageCircle size={40} />
        </div>
        <h3>Chào mừng đến với Tin nhắn</h3>
        <p>Chọn một cuộc hội thoại để bắt đầu trao đổi với hành khách.</p>
      </section>
    );
  }

  const isClosed = conversation.status === "CLOSED";

  const renderMessages = () =>
    messages.map((message, index) => {
      const prev = messages[index - 1];
      const next = messages[index + 1];

      const isSystem = message.messageType === "SYSTEM";
      const isMine = checkIsMine(message, currentManagerId);

      const sameGroup = (other) =>
        other &&
        other.messageType !== "SYSTEM" &&
        checkIsMine(other, currentManagerId) === isMine &&
        getDayKey(other.sentAt) === getDayKey(message.sentAt);

      const isFirstInGroup = !sameGroup(prev);
      const isLastInGroup = !sameGroup(next);
      const showDay = getDayKey(prev?.sentAt) !== getDayKey(message.sentAt);

      return (
        <Fragment key={message.id}>
          {showDay && message.sentAt && (
            <div className="shore-chat-day">
              <span>{formatDayLabel(message.sentAt)}</span>
            </div>
          )}

          <MessageBubble
            message={message}
            isMine={isMine}
            isFirstInGroup={isSystem || isFirstInGroup}
            isLastInGroup={isSystem || isLastInGroup}
          />
        </Fragment>
      );
    });

  return (
    <section className="shore-chat-window">
      <header className="shore-chat-header">
        <div className="shore-chat-user">
          <div className="shore-chat-avatar">
            <UserRound size={22} />
            <span
              className={`shore-chat-avatar-dot ${
                socketConnected ? "connected" : ""
              }`}
            />
          </div>

          <div>
            <h3>
              Booking #{conversation.bookingCode || conversation.bookingId}
            </h3>

            <p>{conversation.department || "SHORE"} · Hành khách</p>
          </div>
        </div>

        <div
          className={`shore-chat-connection ${
            socketConnected ? "connected" : ""
          }`}
        >
          <span
            className={`shore-chat-connection-dot ${
              socketConnected ? "connected" : ""
            }`}
          />

          {socketConnected ? "Realtime" : "Đang kết nối..."}
        </div>
      </header>

      <div className="shore-chat-messages">
        {loading ? (
          <div className="shore-chat-loading">
            <span className="shore-chat-spinner" />
            Đang tải tin nhắn...
          </div>
        ) : messages.length === 0 ? (
          <div className="shore-chat-no-messages">
            <MessageCircle size={32} />
            <span>Chưa có tin nhắn. Hãy bắt đầu cuộc trò chuyện!</span>
          </div>
        ) : (
          renderMessages()
        )}

        <div ref={messagesEndRef} />
      </div>

      {isClosed && (
        <div className="shore-chat-closed">
          Cuộc hội thoại đã đóng. Bạn không thể gửi tin nhắn.
        </div>
      )}

      <MessageComposer onSend={onSend} sending={sending} disabled={isClosed} />
    </section>
  );
};

export default ChatWindow;
