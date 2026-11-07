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
 * isMine = true  -> tin của Shore (hiển thị bên PHẢI)
 * isMine = false -> tin của hành khách (hiển thị bên TRÁI)
 * isFirstInGroup / isLastInGroup: dùng để gộp các tin liên tiếp của cùng một người.
 */
const MessageBubble = ({
  message,
  isMine,
  isFirstInGroup = true,
  isLastInGroup = true,
}) => {
  if (message.messageType === "SYSTEM") {
    return <div className="shore-message-system">{message.content}</div>;
  }

  const isRead = message.messageStatus === "READ";
  const isFile = message.messageType === "FILE";

  const rowClass = [
    "shore-message-row",
    isMine ? "mine" : "theirs",
    isFirstInGroup ? "first" : "",
    isLastInGroup ? "last" : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <div className={rowClass}>
      {!isMine && (
        <div className="shore-message-avatar-slot">
          {isLastInGroup && (
            <div className="shore-message-avatar passenger">
              <UserRound size={16} />
            </div>
          )}
        </div>
      )}

      <div className="shore-message-content">
        {isFirstInGroup && (
          <div className="shore-message-sender">
            {isMine ? "Bạn" : "Hành khách"}
          </div>
        )}

        <div className={`shore-message-bubble ${isFile ? "file" : ""}`}>
          {isFile && (
            <span className="shore-message-file-icon">
              <FileText size={16} />
            </span>
          )}

          <span>{message.content}</span>
        </div>

        {isLastInGroup && (
          <div className="shore-message-meta">
            <time>{formatTime(message.sentAt)}</time>

            {isMine && (
              <span className={`shore-message-status ${isRead ? "read" : ""}`}>
                {isRead ? <CheckCheck size={14} /> : <Check size={14} />}
                {isRead && <em>Đã xem</em>}
              </span>
            )}
          </div>
        )}
      </div>

      {isMine && (
        <div className="shore-message-avatar-slot">
          {isLastInGroup && (
            <div className="shore-message-avatar shore">
              <Headset size={16} />
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default MessageBubble;
