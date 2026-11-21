// src/modules/chat/pages/PublicTourChatFloatingWidget.jsx

import { useEffect, useRef, useState } from "react";
import useChat from "../hooks/useChat";
import "./PublicTourChatFloatingWidget.css";

export default function PublicTourChatFloatingWidget() {
  const [isOpen, setIsOpen] = useState(false);
  const bottomRef = useRef(null);

  const {
    messages,
    input,
    setInput,
    isLoadingWelcome,
    isSending,
    error,
    openChat,
    sendMessage,
    retryWelcome,
  } = useChat();

  useEffect(() => {
    if (isOpen) {
      openChat();
    }
  }, [isOpen, openChat]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({
      behavior: "smooth",
      block: "end",
    });
  }, [messages, isSending, isLoadingWelcome]);

  const handleSubmit = (event) => {
    event.preventDefault();

    if (isSending || isLoadingWelcome) return;

    sendMessage(input);
  };

  const handleSuggestionClick = (suggestion) => {
    if (isSending || isLoadingWelcome) return;

    sendMessage(suggestion);
  };

  return (
    <div className="tour-chat">
      {isOpen && (
        <section className="tour-chat__window" aria-label="Trợ lý du lịch">
          <header className="tour-chat__header">
            <div className="tour-chat__header-info">
              <div className="tour-chat__avatar">✦</div>

              <div>
                <div className="tour-chat__title">Trợ lý du lịch</div>
                <div className="tour-chat__subtitle">
                  Hỗ trợ tìm hiểu và đặt tour
                </div>
              </div>
            </div>

            <button
              type="button"
              className="tour-chat__close"
              onClick={() => setIsOpen(false)}
              aria-label="Đóng khung chat"
              title="Đóng"
            >
              ×
            </button>
          </header>

          <div className="tour-chat__messages">
            {messages.map((message) => (
              <div
                key={message.id}
                className={`tour-chat__message-row ${
                  message.isUser
                    ? "tour-chat__message-row--user"
                    : "tour-chat__message-row--bot"
                }`}
              >
                {!message.isUser && (
                  <div className="tour-chat__small-avatar">✦</div>
                )}

                <div className="tour-chat__message-content">
                  <div
                    className={`tour-chat__bubble ${
                      message.isUser
                        ? "tour-chat__bubble--user"
                        : "tour-chat__bubble--bot"
                    }`}
                  >
                    {message.text}
                  </div>

                  {message.suggestions?.length > 0 && (
                    <div className="tour-chat__suggestions">
                      {message.suggestions.map((suggestion, index) => (
                        <button
                          key={`${suggestion}-${index}`}
                          type="button"
                          className="tour-chat__suggestion"
                          disabled={isSending || isLoadingWelcome}
                          onClick={() => handleSuggestionClick(suggestion)}
                        >
                          {suggestion}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}

            {(isLoadingWelcome || isSending) && (
              <div className="tour-chat__typing">
                <span />
                <span />
                <span />
                <span className="tour-chat__typing-text">
                  Trợ lý đang trả lời...
                </span>
              </div>
            )}

            {error && (
              <div className="tour-chat__error">
                <span>{error}</span>

                {!messages.length && (
                  <button
                    type="button"
                    onClick={retryWelcome}
                    disabled={isLoadingWelcome}
                  >
                    Thử lại
                  </button>
                )}
              </div>
            )}

            <div ref={bottomRef} />
          </div>

          <form className="tour-chat__input-area" onSubmit={handleSubmit}>
            <input
              type="text"
              value={input}
              onChange={(event) => setInput(event.target.value)}
              placeholder="Nhập câu hỏi của bạn..."
              aria-label="Nhập câu hỏi"
              maxLength={2000}
              disabled={isLoadingWelcome || isSending}
            />

            <button
              type="submit"
              className="tour-chat__send"
              disabled={!input.trim() || isLoadingWelcome || isSending}
              aria-label="Gửi tin nhắn"
              title="Gửi"
            >
              ➤
            </button>
          </form>

          <div className="tour-chat__footer">
            Trợ lý du lịch · Cruise Management System
          </div>
        </section>
      )}

      <button
        type="button"
        className={`tour-chat__launcher ${
          isOpen ? "tour-chat__launcher--open" : ""
        }`}
        onClick={() => setIsOpen((current) => !current)}
        aria-label={isOpen ? "Đóng chatbot" : "Mở chatbot"}
        title={isOpen ? "Đóng chat" : "Chat với trợ lý du lịch"}
      >
        {isOpen ? (
          <span className="tour-chat__launcher-close">×</span>
        ) : (
          <svg
            viewBox="0 0 24 24"
            width="29"
            height="29"
            fill="none"
            aria-hidden="true"
          >
            {/* Ăng-ten */}
            <path
              d="M12 3v3M9.5 3h5"
              stroke="currentColor"
              strokeWidth="1.8"
              strokeLinecap="round"
            />
            {/* Khung đầu robot */}
            <rect
              x="5"
              y="6.5"
              width="14"
              height="11.5"
              rx="3.2"
              stroke="currentColor"
              strokeWidth="1.8"
            />
            {/* Tai trái / phải */}
            <path
              d="M3 11v2.5M21 11v2.5"
              stroke="currentColor"
              strokeWidth="1.8"
              strokeLinecap="round"
            />
            {/* Mắt robot */}
            <circle cx="9.5" cy="11.8" r="1.2" fill="currentColor" />
            <circle cx="14.5" cy="11.8" r="1.2" fill="currentColor" />
            {/* Miệng / đèn trạng thái */}
            <path
              d="M9.5 15.2h5"
              stroke="currentColor"
              strokeWidth="1.8"
              strokeLinecap="round"
            />
          </svg>
        )}
      </button>
    </div>
  );
}
