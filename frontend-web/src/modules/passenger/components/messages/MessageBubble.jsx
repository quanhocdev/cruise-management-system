// src/modules/passenger/components/messages/MessageBubble.jsx

import "./MessageBubble.css";

const formatTime = (value) => {
  if (!value) return "";

  return new Date(value).toLocaleTimeString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
  });
};

export default function MessageBubble({ message, isMine }) {
  if (message.messageType === "SYSTEM") {
    return <div className="passenger-message-system">{message.content}</div>;
  }

  return (
    <div className={`passenger-message-row ${isMine ? "mine" : "theirs"}`}>
      <div className="passenger-message-bubble">
        <p>{message.content}</p>

        <span>{formatTime(message.sentAt)}</span>
      </div>
    </div>
  );
}
