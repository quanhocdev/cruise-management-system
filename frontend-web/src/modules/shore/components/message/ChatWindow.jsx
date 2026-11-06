import { useEffect, useRef } from "react";
import { MessageCircle, UserRound } from "lucide-react";

import MessageBubble from "./MessageBubble";
import MessageComposer from "./MessageComposer";

import "./ChatWindow.css";

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
        <MessageCircle size={52} />
        <h3>Chào mừng đến với Tin nhắn</h3>
        <p>Chọn một cuộc hội thoại để bắt đầu trao đổi với hành khách.</p>
      </section>
    );
  }

  const isClosed = conversation.status === "CLOSED";

  return (
    <section className="shore-chat-window">
      <header className="shore-chat-header">
        <div className="shore-chat-user">
          <div className="shore-chat-avatar">
            <UserRound size={22} />
          </div>

          <div>
            <h3>
              Booking #{conversation.bookingCode || conversation.bookingId}
            </h3>

            <p>{conversation.department || "SHORE"} · Hành khách</p>
          </div>
        </div>

        <div className="shore-chat-connection">
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
          <div className="shore-chat-loading">Đang tải tin nhắn...</div>
        ) : messages.length === 0 ? (
          <div className="shore-chat-no-messages">
            <MessageCircle size={32} />
            <span>Chưa có tin nhắn. Hãy bắt đầu cuộc trò chuyện!</span>
          </div>
        ) : (
          messages.map((message) => (
            <MessageBubble
              key={message.id}
              message={message}
              isMine={
                currentManagerId != null &&
                message.senderUserId === currentManagerId
              }
            />
          ))
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
