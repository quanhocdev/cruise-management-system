// src/modules/passenger/components/messages/MessageComposer.jsx

import { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { Send } from "lucide-react";
import "./MessageComposer.css";

export default function MessageComposer({ onSend, sending, disabled }) {
  const [content, setContent] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();

    const value = content.trim();
    if (!value || sending || disabled) return;

    await onSend(value);
    setContent("");
  };

  const handleKeyDown = (event) => {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      handleSubmit(event);
    }
  };

  return (
    <Form className="passenger-message-composer" onSubmit={handleSubmit}>
      <Form.Control
        as="textarea"
        rows={1}
        value={content}
        onChange={(event) => setContent(event.target.value)}
        onKeyDown={handleKeyDown}
        placeholder={disabled ? "Cuộc trò chuyện đã đóng" : "Nhập tin nhắn..."}
        disabled={disabled || sending}
      />

      <Button
        type="submit"
        disabled={!content.trim() || sending || disabled}
        aria-label="Gửi tin nhắn"
      >
        <Send size={18} />
      </Button>
    </Form>
  );
}
