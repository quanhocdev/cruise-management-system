// src/modules/passenger/pages/PassengerMessages.jsx

import { Alert, Container, Spinner } from "react-bootstrap";
import { useAuth } from "../../../context/AuthContext";

import usePassengerMessages from "../hooks/usePassengerMessages";

import ConversationList from "../components/messages/ConversationList";
import ChatWindow from "../components/messages/ChatWindow";

import "./PassengerMessages.css";

export default function PassengerMessages() {
  const { user } = useAuth();

  const {
    bookings,
    bookingsLoading,
    bookingsError,

    conversations,
    selectedConversation,
    selectedConversationId,
    messages,

    loading,
    messagesLoading,
    creating,
    sending,
    error,

    createConversation,
    selectConversation,
    sendMessage,
    socketConnected,
  } = usePassengerMessages();

  const handleCreateConversation = async (bookingId, department) => {
    try {
      await createConversation(bookingId, department);
    } catch {
      // Lỗi đã được lưu trong state của hook.
    }
  };

  const handleSendMessage = async (content) => {
    try {
      await sendMessage(content);
    } catch {
      // Lỗi đã được lưu trong state của hook.
    }
  };

  return (
    <div className="passenger-messages-page">
      <Container fluid="lg" className="passenger-messages-container">
        <div className="passenger-messages-title">
          <div>
            <h3>Tin nhắn hỗ trợ</h3>
            <p>
              Liên hệ với các phòng ban để được hỗ trợ trong chuyến đi của bạn.
            </p>
            <small
              className={socketConnected ? "text-success" : "text-secondary"}
            >
              {socketConnected ? "● Đã kết nối" : "○ Đang kết nối..."}
            </small>
          </div>
        </div>

        {bookingsError && <Alert variant="danger">{bookingsError}</Alert>}

        {error && <Alert variant="danger">{error}</Alert>}

        {bookingsLoading && bookings.length === 0 ? (
          <div className="passenger-messages-loading">
            <Spinner animation="border" variant="primary" />
            <p>Đang tải dữ liệu...</p>
          </div>
        ) : (
          <div className="passenger-messages-panel">
            <ConversationList
              bookings={bookings}
              conversations={conversations}
              selectedConversationId={selectedConversationId}
              bookingsLoading={bookingsLoading}
              loading={loading}
              creating={creating}
              onSelect={selectConversation}
              onCreate={handleCreateConversation}
            />

            <ChatWindow
              conversation={selectedConversation}
              messages={messages}
              currentUserId={user?.id}
              loading={messagesLoading}
              sending={sending}
              error={null}
              onSend={handleSendMessage}
            />
          </div>
        )}
      </Container>
    </div>
  );
}
