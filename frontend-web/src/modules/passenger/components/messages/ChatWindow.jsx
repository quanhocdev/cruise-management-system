// src/modules/passenger/components/messages/ChatWindow.jsx

import { useEffect, useRef } from "react";
import { Alert, Spinner } from "react-bootstrap";
import { MessageCircle } from "lucide-react";

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
        <MessageCircle size={54} />
        <h5>Chọn cuộc trò chuyện</h5>
        <p>
          Chọn một cuộc trò chuyện hoặc bắt đầu trao đổi với phòng ban liên quan
          đến booking của bạn.
        </p>
      </div>
    );
  }

  const isClosed = conversation.status === "CLOSED";

  return (
    <section className="passenger-chat-window">
      <header className="passenger-chat-header">
        <div className="passenger-chat-header-avatar">
          <MessageCircle size={22} />
        </div>

        <div>
          <h6>
            {DEPARTMENT_LABELS[conversation.department] ||
              conversation.department}
          </h6>

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
          messages.map((message) => (
            <MessageBubble
              key={message.id}
              message={message}
              isMine={message.senderUserId === currentUserId}
            />
          ))
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
