// src/modules/passenger/components/messages/ChatWindow.jsx

import { Fragment, useEffect, useRef } from "react";
import { Alert, Spinner } from "react-bootstrap";
import { Headset, MessageCircle } from "lucide-react";

import MessageBubble from "./MessageBubble";
import MessageComposer from "./MessageComposer";
import "./ChatWindow.css";

const DEPARTMENT_LABELS = {
  ONBOARD: "Nhân viên trên du thuyền",
  CONVENIENCE: "Dịch vụ tiện ích",
  FINANCE: "Tài chính",
  SHORE: "Hoạt động bờ",
  SCHEDULER: "Điều phối",
};

// Tin của hành khách đang đăng nhập -> bên phải.
// Ép về string để tránh lệch number/string giữa user.id và senderUserId.
const checkIsMine = (message, currentUserId) =>
  currentUserId != null &&
  message.senderUserId != null &&
  String(message.senderUserId) === String(currentUserId);

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

export default function ChatWindow({
  conversation,
  messages = [],
  currentUserId,
  loading,
  sending,
  error,
  onSend,
}) {
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({
      behavior: "smooth",
    });
  }, [messages]);

  if (!conversation) {
    return (
      <div className="passenger-chat-placeholder">
        <div className="passenger-chat-placeholder-icon">
          <MessageCircle size={40} />
        </div>
        <h5>Chọn cuộc trò chuyện</h5>
        <p>
          Chọn một cuộc trò chuyện hoặc bắt đầu trao đổi với phòng ban liên quan
          đến booking của bạn.
        </p>
      </div>
    );
  }

  const isClosed = conversation.status === "CLOSED";
  const departmentLabel =
    DEPARTMENT_LABELS[conversation.department] || conversation.department;

  const renderMessages = () =>
    messages.map((message, index) => {
      const prev = messages[index - 1];
      const next = messages[index + 1];

      const isSystem = message.messageType === "SYSTEM";
      const isMine = checkIsMine(message, currentUserId);

      const sameGroup = (other) =>
        other &&
        other.messageType !== "SYSTEM" &&
        checkIsMine(other, currentUserId) === isMine &&
        getDayKey(other.sentAt) === getDayKey(message.sentAt);

      const isFirstInGroup = !sameGroup(prev);
      const isLastInGroup = !sameGroup(next);
      const showDay = getDayKey(prev?.sentAt) !== getDayKey(message.sentAt);

      return (
        <Fragment key={message.id}>
          {showDay && message.sentAt && (
            <div className="passenger-chat-day">
              <span>{formatDayLabel(message.sentAt)}</span>
            </div>
          )}

          <MessageBubble
            message={message}
            isMine={isMine}
            senderLabel={departmentLabel}
            isFirstInGroup={isSystem || isFirstInGroup}
            isLastInGroup={isSystem || isLastInGroup}
          />
        </Fragment>
      );
    });

  return (
    <section className="passenger-chat-window">
      <header className="passenger-chat-header">
        <div className="passenger-chat-header-avatar">
          <Headset size={22} />
        </div>

        <div>
          <h6>{departmentLabel}</h6>

          <span>
            {conversation.bookingCode || `Booking #${conversation.bookingId}`}
          </span>
        </div>

        <span
          className={`passenger-chat-status ${isClosed ? "closed" : "active"}`}
        >
          {isClosed ? "Đã đóng" : "Đang hoạt động"}
        </span>
      </header>

      {error && (
        <Alert variant="danger" className="m-3">
          {error}
        </Alert>
      )}

      <div className="passenger-chat-messages">
        {loading ? (
          <div className="passenger-chat-loading">
            <Spinner animation="border" variant="primary" />
            <span>Đang tải tin nhắn...</span>
          </div>
        ) : messages.length === 0 ? (
          <div className="passenger-chat-empty">
            <MessageCircle size={34} />
            <p>Chưa có tin nhắn</p>
            <small>Hãy gửi lời chào để bắt đầu cuộc trò chuyện.</small>
          </div>
        ) : (
          renderMessages()
        )}

        <div ref={messagesEndRef} />
      </div>

      {isClosed && (
        <div className="passenger-chat-closed-notice">
          Cuộc trò chuyện này đã đóng. Bạn có thể bắt đầu cuộc trò chuyện mới
          nếu cần hỗ trợ thêm.
        </div>
      )}

      <MessageComposer
        onSend={onSend}
        sending={sending}
        disabled={isClosed || loading}
      />
    </section>
  );
}
