import { useState } from "react";
import { Send } from "lucide-react";

import "./MessageComposer.css";

const MessageComposer = ({ onSend, sending, disabled }) => {
  const [content, setContent] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();

    const value = content.trim();

    if (!value || sending || disabled) return;

    try {
      await onSend(value);
      setContent("");
    } catch {
      // Giữ nội dung để người dùng có thể gửi lại.
    }
  };

  const handleKeyDown = (event) => {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      handleSubmit(event);
    }
  };

  return (
    <form className="onboard-message-composer" onSubmit={handleSubmit}>
      <textarea
        value={content}
        onChange={(event) => setContent(event.target.value)}
        onKeyDown={handleKeyDown}
        placeholder={disabled ? "Cuộc hội thoại đã đóng" : "Nhập tin nhắn..."}
        rows={1}
        maxLength={5000}
        disabled={disabled || sending}
      />

      <button
        type="submit"
        disabled={!content.trim() || sending || disabled}
        title="Gửi tin nhắn"
      >
        <Send size={18} />
      </button>
    </form>
  );
};

export default MessageComposer;
