import { Check, CheckCheck, FileText, Headset, UserRound } from "lucide-react";

import "./MessageBubble.css";

const formatTime = (value) => {
  if (!value) return "";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return "";

  return date.toLocaleTimeString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
  });
};

/**
 * isMine = true  -> tin của nhân viên Onboard (bên PHẢI)
 * isMine = false -> tin của hành khách (bên TRÁI)
 *
 * isFirstInGroup / isLastInGroup:
 * dùng để gộp các tin liên tiếp của cùng một người.
 */
const MessageBubble = ({
  message,
  isMine,
  isFirstInGroup = true,
  isLastInGroup = true,
}) => {
  if (message.messageType === "SYSTEM") {
    return <div className="onboard-message-system">{message.content}</div>;
  }

  const isRead = message.messageStatus === "READ";
  const isFile = message.messageType === "FILE";

  const rowClass = [
    "onboard-message-row",
    isMine ? "mine" : "theirs",
    isFirstInGroup ? "first" : "",
    isLastInGroup ? "last" : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <div className={rowClass}>
      {!isMine && (
        <div className="onboard-message-avatar-slot">
          {isLastInGroup && (
            <div className="onboard-message-avatar passenger">
              <UserRound size={16} />
            </div>
          )}
        </div>
      )}

      <div className="onboard-message-content">
        {isFirstInGroup && (
          <div className="onboard-message-sender">
            {isMine ? "Bạn" : "Hành khách"}
          </div>
        )}

        <div className={`onboard-message-bubble ${isFile ? "file" : ""}`}>
          {isFile && (
            <span className="onboard-message-file-icon">
              <FileText size={16} />
            </span>
          )}

          <span>{message.content}</span>
        </div>

        {isLastInGroup && (
          <div className="onboard-message-meta">
            <time>{formatTime(message.sentAt)}</time>

            {isMine && (
              <span
                className={`onboard-message-status ${isRead ? "read" : ""}`}
              >
                {isRead ? <CheckCheck size={14} /> : <Check size={14} />}

                {isRead && <em>Đã xem</em>}
              </span>
            )}
          </div>
        )}
      </div>

      {isMine && (
        <div className="onboard-message-avatar-slot">
          {isLastInGroup && (
            <div className="onboard-message-avatar onboard">
              <Headset size={16} />
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default MessageBubble;
