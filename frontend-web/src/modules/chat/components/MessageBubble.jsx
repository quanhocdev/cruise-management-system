import { FileText } from "lucide-react";

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

const MessageBubble = ({ message, isMine }) => {
  const isSystem = message.messageType === "SYSTEM";
  const isFile = message.messageType === "FILE" || !!message.fileUrl;

  if (isSystem) {
    return (
      <div className="chat-message-system">
        <span>{message.content}</span>
        <small>{formatTime(message.sentAt)}</small>
      </div>
    );
  }

  return (
    <div className={`chat-message-row ${isMine ? "mine" : "theirs"}`}>
      <div className={`chat-message-bubble ${isMine ? "mine" : "theirs"}`}>
        {!isMine && (
          <div className="chat-message-sender">
            {message.senderName || "Hành khách"}
          </div>
        )}

        {isFile ? (
          <a
            className="chat-message-file"
            href={message.fileUrl}
            target="_blank"
            rel="noreferrer"
          >
            <FileText size={19} />
            <span>{message.fileName || "Tệp đính kèm"}</span>
          </a>
        ) : (
          <div className="chat-message-content">{message.content}</div>
        )}

        <div className="chat-message-meta">
          <span>{formatTime(message.sentAt)}</span>

          {isMine && message.status && (
            <span className="chat-message-status">
              {message.status === "READ" ? "Đã xem" : "Đã gửi"}
            </span>
          )}
        </div>
      </div>
    </div>
  );
};

export default MessageBubble;
