import { useMemo, useState } from "react";
import { MessageCircle, Search, UserRound } from "lucide-react";

import "./ConversationList.css";

const formatTime = (value) => {
  if (!value) return "";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return "";

  return date.toLocaleString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
    day: "2-digit",
    month: "2-digit",
  });
};

const ConversationList = ({
  conversations = [],
  selectedConversationId,
  onSelect,
  loading,
}) => {
  const [keyword, setKeyword] = useState("");

  const filteredConversations = useMemo(() => {
    const normalizedKeyword = keyword.trim().toLowerCase();

    if (!normalizedKeyword) return conversations;

    return conversations.filter((conversation) => {
      const bookingCode = String(conversation.bookingCode || "").toLowerCase();

      const bookingId = String(conversation.bookingId || "").toLowerCase();

      const passengerName = String(
        conversation.passengerName || conversation.customerName || "",
      ).toLowerCase();

      return (
        bookingCode.includes(normalizedKeyword) ||
        bookingId.includes(normalizedKeyword) ||
        passengerName.includes(normalizedKeyword)
      );
    });
  }, [conversations, keyword]);

  return (
    <div className="chat-conversation-list">
      <div className="chat-conversation-header">
        <div>
          <h2>Hội thoại</h2>
          <span>{conversations.length} cuộc trò chuyện</span>
        </div>

        <div className="chat-conversation-header-icon">
          <MessageCircle size={20} />
        </div>
      </div>

      <div className="chat-conversation-search">
        <Search size={17} />

        <input
          type="text"
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
          placeholder="Tìm theo mã booking..."
        />
      </div>

      <div className="chat-conversation-items">
        {loading ? (
          <div className="chat-conversation-state">
            <div className="chat-conversation-spinner" />
            <span>Đang tải hội thoại...</span>
          </div>
        ) : filteredConversations.length === 0 ? (
          <div className="chat-conversation-state">
            <MessageCircle size={30} />
            <span>
              {keyword
                ? "Không tìm thấy hội thoại phù hợp."
                : "Chưa có hội thoại nào."}
            </span>
          </div>
        ) : (
          filteredConversations.map((conversation) => {
            const isSelected =
              String(conversation.id) === String(selectedConversationId);

            const isClosed = conversation.status === "CLOSED";

            return (
              <button
                type="button"
                key={conversation.id}
                className={`chat-conversation-item ${
                  isSelected ? "selected" : ""
                }`}
                onClick={() => onSelect(conversation.id)}
              >
                <div className="chat-conversation-avatar">
                  <UserRound size={21} />
                </div>

                <div className="chat-conversation-info">
                  <div className="chat-conversation-top">
                    <strong>
                      {conversation.bookingCode
                        ? `Booking ${conversation.bookingCode}`
                        : `Booking #${conversation.bookingId || conversation.id}`}
                    </strong>

                    <span className="chat-conversation-time">
                      {formatTime(
                        conversation.updatedAt || conversation.createdAt,
                      )}
                    </span>
                  </div>

                  <div className="chat-conversation-bottom">
                    <span className="chat-conversation-department">
                      {conversation.department || ""}
                    </span>

                    <span
                      className={`chat-conversation-status ${
                        isClosed ? "closed" : "active"
                      }`}
                    >
                      {isClosed ? "Đã đóng" : "Đang mở"}
                    </span>
                  </div>
                </div>
              </button>
            );
          })
        )}
      </div>
    </div>
  );
};

export default ConversationList;
