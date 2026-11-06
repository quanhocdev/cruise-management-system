import api from "../../../api/axios";

const BASE_URL = "/message";

const shoreMessageService = {
  getConversations: async () => {
    const response = await api.get(`${BASE_URL}/conversations`);
    return response.data;
  },

  getParticipants: async (conversationId) => {
    const response = await api.get(
      `${BASE_URL}/conversations/${conversationId}/participants`,
    );

    return response.data;
  },

  getMessages: async (conversationId) => {
    const response = await api.get(
      `${BASE_URL}/conversations/${conversationId}/messages`,
    );

    return response.data;
  },

  sendMessage: async (conversationId, content) => {
    const response = await api.post(
      `${BASE_URL}/conversations/${conversationId}/messages`,
      { content },
    );

    return response.data;
  },
};

export default shoreMessageService;
