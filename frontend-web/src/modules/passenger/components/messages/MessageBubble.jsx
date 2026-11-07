// src/modules/passenger/components/messages/MessageBubble.jsx

import { Check, CheckCheck, FileText, Headset, UserRound } from "lucide-react";

import "./MessageBubble.css";

const formatTime = (value) => {
  if (!value) return "";

  return new Date(value).toLocaleTimeString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
  });
};

/**
 * isMine = true  -> tin của hành khách đang đăng nhập (bên PHẢI)
 * isMine = false -> tin của nhân viên phòng ban (bên TRÁI)
 */
export default function MessageBubble({
  message,
  isMine,
  senderLabel = "Nhân viên hỗ trợ",
  isFirstInGroup = true,
  isLastInGroup = true,
}) {
  if (message.messageType === "SYSTEM") {
    return <div className="passenger-message-system">{message.content}</div>;
  }

  const isRead = message.messageStatus === "READ";
  const isFile = message.messageType === "FILE";

  const rowClass = [
    "passenger-message-row",
    isMine ? "mine" : "theirs",
    isFirstInGroup ? "first" : "",
    isLastInGroup ? "last" : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <div className={rowClass}>
      {!isMine && (
        <div className="passenger-message-avatar-slot">
          {isLastInGroup && (
            <div className="passenger-message-avatar staff">
              <Headset size={16} />
            </div>
          )}
        </div>
      )}

      <div className="passenger-message-content">
        {isFirstInGroup && (
          <div className="passenger-message-sender">
            {isMine ? "Bạn" : senderLabel}
          </div>
        )}

        <div className="passenger-message-bubble">
          {isFile && (
            <span className="passenger-message-file-icon">
              <FileText size={16} />
            </span>
          )}

          <p>{message.content}</p>
        </div>

        {isLastInGroup && (
          <div className="passenger-message-meta">
            <time>{formatTime(message.sentAt)}</time>

            {isMine && (
              <span
                className={`passenger-message-status ${isRead ? "read" : ""}`}
              >
                {isRead ? <CheckCheck size={14} /> : <Check size={14} />}
                {isRead && <em>Đã xem</em>}
              </span>
            )}
          </div>
        )}
      </div>

      {isMine && (
        <div className="passenger-message-avatar-slot">
          {isLastInGroup && (
            <div className="passenger-message-avatar me">
              <UserRound size={16} />
            </div>
          )}
        </div>
      )}
    </div>
  );
}
