import { useState } from "react";
import { Send } from "lucide-react";

import "./MessageComposer.css";

const MessageComposer = ({ onSend, sending }) => {
  const [content, setContent] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();

    const value = content.trim();

    if (!value || sending) return;

    try {
      await onSend(value);
      setContent("");
    } catch (error) {
      console.error("CHAT COMPOSER SEND ERROR:", error);
    }
  };

  const handleKeyDown = (event) => {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      handleSubmit(event);
    }
  };

  return (
    <form className="chat-message-composer" onSubmit={handleSubmit}>
      <textarea
        value={content}
        onChange={(event) => setContent(event.target.value)}
        onKeyDown={handleKeyDown}
        placeholder="Nhập tin nhắn..."
        rows={1}
        disabled={sending}
      />

      <button
        type="submit"
        disabled={!content.trim() || sending}
        title="Gửi tin nhắn"
      >
        <Send size={18} />
      </button>
    </form>
  );
};

export default MessageComposer;
