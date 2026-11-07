import { Alert } from "react-bootstrap";

import useOnboardMessages from "../hooks/useOnboardMessages";

import ConversationList from "../components/messages/ConversationList";
import ChatWindow from "../components/messages/ChatWindow";
import "./OnboardMessages.css";

const OnboardMessages = () => {
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
  } = useOnboardMessages();

  return (
    <div className="onboard-messages-page">
      <div className="onboard-messages-title">
        <div>
          <h1>Tin nhắn hành khách</h1>
          <p>
            Quản lý và hỗ trợ hành khách trong các hoạt động trên du thuyền.
          </p>
        </div>
      </div>

      {error && (
        <Alert variant="danger" dismissible className="onboard-messages-error">
          Không thể xử lý yêu cầu. Vui lòng thử lại.
        </Alert>
      )}

      <div className="onboard-messages-layout">
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

export default OnboardMessages;
