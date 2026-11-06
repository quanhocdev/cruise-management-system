import { FileText } from "lucide-react";

import "./MessageBubble.css";

const formatTime = (value) => {
  if (!value) return "";

  return new Date(value).toLocaleTimeString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
  });
};

const MessageBubble = ({ message, isMine }) => {
  const isSystem = message.messageType === "SYSTEM";

  if (isSystem) {
    return <div className="shore-message-system">{message.content}</div>;
  }

  return (
    <div className={`shore-message-row ${isMine ? "mine" : "theirs"}`}>
      <div className="shore-message-content">
        <div className="shore-message-sender">
          {isMine ? "Bạn" : "Hành khách"}
        </div>

        <div className="shore-message-bubble">
          {message.messageType === "FILE" && <FileText size={17} />}

          <span>{message.content}</span>
        </div>

        <div className="shore-message-time">
          {formatTime(message.sentAt)}
          {isMine && message.messageStatus === "READ" && <span> · Đã xem</span>}
        </div>
      </div>
    </div>
  );
};

export default MessageBubble;
