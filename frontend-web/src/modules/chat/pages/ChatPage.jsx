import { Alert } from "react-bootstrap";

import useChatMessages from "../hooks/useChatMessages";

import ConversationList from "../components/ConversationList";
import ChatWindow from "../components/ChatWindow";

import "./ChatPage.css";

const ChatPage = () => {
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
  } = useChatMessages();

  return (
    <div className="chat-page">
      <div className="chat-page-title">
        <div>
          <h1>Tin nhắn hành khách</h1>
          <p>
            Quản lý và hỗ trợ hành khách trong các hoạt động trên du thuyền.
          </p>
        </div>
      </div>

      {error && (
        <Alert variant="danger" dismissible className="chat-page-error">
          Không thể xử lý yêu cầu. Vui lòng thử lại.
        </Alert>
      )}

      <div className="chat-page-layout">
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

export default ChatPage;
