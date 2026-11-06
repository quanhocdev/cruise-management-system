import { MessageCircle, Search, UserRound } from "lucide-react";
import { useState } from "react";

import "./ConversationList.css";

const formatTime = (value) => {
  if (!value) return "";

  return new Date(value).toLocaleString("vi-VN", {
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

  const filteredConversations = conversations.filter((conversation) => {
    const searchValue = `${conversation.bookingCode || ""} ${
      conversation.bookingId || ""
    }`.toLowerCase();

    return searchValue.includes(keyword.trim().toLowerCase());
  });

  return (
    <aside className="shore-conversation-list">
      <div className="shore-conversation-header">
        <div>
          <h2>Tin nhắn</h2>
          <p>{conversations.length} cuộc hội thoại</p>
        </div>

        <span className="shore-conversation-header-icon">
          <MessageCircle size={21} />
        </span>
      </div>

      <div className="shore-conversation-search">
        <Search size={17} />

        <input
          type="text"
          placeholder="Tìm theo mã booking..."
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
        />
      </div>

      <div className="shore-conversation-items">
        {loading ? (
          <div className="shore-conversation-empty">Đang tải hội thoại...</div>
        ) : filteredConversations.length === 0 ? (
          <div className="shore-conversation-empty">
            <MessageCircle size={28} />
            <span>
              {keyword
                ? "Không tìm thấy cuộc hội thoại"
                : "Chưa có cuộc hội thoại nào"}
            </span>
          </div>
        ) : (
          filteredConversations.map((conversation) => {
            const selected = conversation.id === selectedConversationId;

            return (
              <button
                key={conversation.id}
                type="button"
                className={`shore-conversation-item ${
                  selected ? "active" : ""
                }`}
                onClick={() => onSelect(conversation.id)}
              >
                <div className="shore-conversation-avatar">
                  <UserRound size={21} />
                </div>

                <div className="shore-conversation-info">
                  <div className="shore-conversation-top">
                    <strong>
                      Booking #
                      {conversation.bookingCode || conversation.bookingId}
                    </strong>

                    <time>
                      {formatTime(
                        conversation.updatedAt || conversation.createdAt,
                      )}
                    </time>
                  </div>

                  <div className="shore-conversation-bottom">
                    <span>{conversation.department || "SHORE"}</span>

                    <span className="shore-conversation-status">
                      {conversation.status === "ACTIVE"
                        ? "Đang hoạt động"
                        : "Đã đóng"}
                    </span>
                  </div>
                </div>
              </button>
            );
          })
        )}
      </div>
    </aside>
  );
};

export default ConversationList;
