// src/modules/chat/services/chatService.js

import api from "../../../api/axios";

const chatService = {
  async openChat(sessionId) {
    const response = await api.post("/chat/welcome", {
      sessionId,
    });

    return response.data;
  },

  async sendMessage(message, sessionId) {
    const response = await api.post("/chat/message", {
      message,
      sessionId,
    });

    return response.data;
  },
};

export default chatService;
