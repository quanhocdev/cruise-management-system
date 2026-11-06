import { Alert, Spinner } from "react-bootstrap";

import useShoreMessages from "../hooks/useShoreMessages";

import ConversationList from "../components/messages/ConversationList";
import ChatWindow from "../components/messages/ChatWindow";

import "./ShoreMessages.css";

const ShoreMessages = () => {
  const {
    conversations,
    selectedConversation,
    selectedConversationId,
    setSelectedConversationId,

    messages,
    currentManagerId,

    loadingConversations,
    loadingMessages,
    sending,

    error,
    socketConnected,

    sendMessage,
  } = useShoreMessages();

  return (
    <div className="shore-messages-page">
      <div className="shore-messages-title">
        <div>
          <h1>Tin nhắn hành khách</h1>
          <p>Quản lý và hỗ trợ hành khách trong các chuyến đi.</p>
        </div>
      </div>

      {error && (
        <Alert variant="danger" dismissible className="shore-messages-error">
          Không thể xử lý yêu cầu. Vui lòng thử lại.
        </Alert>
      )}

      <div className="shore-messages-layout">
        <ConversationList
          conversations={conversations}
          selectedConversationId={selectedConversationId}
          onSelect={setSelectedConversationId}
          loading={loadingConversations}
        />

        <ChatWindow
          conversation={selectedConversation}
          messages={messages}
          currentManagerId={currentManagerId}
          loading={loadingMessages}
          sending={sending}
          socketConnected={socketConnected}
          onSend={sendMessage}
        />
      </div>
    </div>
  );
};

export default ShoreMessages;
