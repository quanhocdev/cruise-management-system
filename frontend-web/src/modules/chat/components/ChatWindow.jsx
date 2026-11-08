import { useEffect, useRef } from "react";
import { MessageCircle, UserRound } from "lucide-react";

import MessageBubble from "./MessageBubble";
import MessageComposer from "./MessageComposer";

import "./ChatWindow.css";

const checkIsMine = (message, currentManagerId) => {
  const senderUserId = message.senderUserId || message.senderId;

  if (currentManagerId && senderUserId) {
    return String(senderUserId) === String(currentManagerId);
  }

  const role = String(
    message.senderRole || message.senderType || "",
  ).toUpperCase();

  if (role.includes("PASSENGER") || role.includes("GUEST")) {
    return false;
  }

  return role.length > 0;
};

const formatDate = (value) => {
  if (!value) return "";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return "";

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
  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({
      behavior: "smooth",
    });
  }, [messages]);

  if (!conversation) {
    return (
      <div className="chat-window chat-window-empty">
        <MessageCircle size={48} />
        <h3>Chưa chọn hội thoại</h3>
        <p>Chọn một booking bên trái để bắt đầu trao đổi.</p>
      </div>
    );
  }

  const isClosed = conversation.status === "CLOSED";

  let previousDate = "";

  return (
    <div className="chat-window">
      <div className="chat-window-header">
        <div className="chat-window-user">
          <div className="chat-window-avatar">
            <UserRound size={22} />
          </div>

          <div>
            <h3>
              {conversation.bookingCode
                ? `Booking ${conversation.bookingCode}`
                : `Booking #${conversation.bookingId}`}
            </h3>

            <span>{conversation.department || "Hội thoại hành khách"}</span>
          </div>
        </div>

        <div
          className={`chat-connection-status ${
            socketConnected ? "connected" : "disconnected"
          }`}
        >
          <span />
          {socketConnected ? "Đang kết nối" : "Đang kết nối lại..."}
        </div>
      </div>

      <div className="chat-window-messages">
        {loading ? (
          <div className="chat-window-loading">
            <div className="chat-conversation-spinner" />
            <span>Đang tải tin nhắn...</span>
          </div>
        ) : messages.length === 0 ? (
          <div className="chat-window-no-messages">
            <MessageCircle size={35} />
            <p>Chưa có tin nhắn. Hãy gửi lời chào đến hành khách.</p>
          </div>
        ) : (
          messages.map((message) => {
            const currentDate = formatDate(message.sentAt);
            const showDate = currentDate && currentDate !== previousDate;

            previousDate = currentDate;

            return (
              <div key={message.id}>
                {showDate && (
                  <div className="chat-message-date">
                    <span>{currentDate}</span>
                  </div>
                )}

                <MessageBubble
                  message={message}
                  isMine={checkIsMine(message, currentManagerId)}
                />
              </div>
            );
          })
        )}

        <div ref={bottomRef} />
      </div>

      {isClosed ? (
        <div className="chat-window-closed">
          Cuộc hội thoại này đã đóng. Bạn không thể gửi tin nhắn mới.
        </div>
      ) : (
        <MessageComposer onSend={onSend} sending={sending} />
      )}
    </div>
  );
};

export default ChatWindow;
