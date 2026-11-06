// src/modules/passenger/services/passengerMessageService.js

import api from "../../../api/axios";

const BASE_URL = "/message";

const passengerMessageService = {
  async getConversations() {
    const response = await api.get(`${BASE_URL}/conversations`);
    return response.data?.data ?? response.data;
  },

  async createConversation(payload) {
    const response = await api.post(`${BASE_URL}/conversations`, payload);
    return response.data?.data ?? response.data;
  },

  async getConversation(conversationId) {
    const response = await api.get(
      `${BASE_URL}/conversations/${conversationId}`,
    );
    return response.data?.data ?? response.data;
  },

  async getParticipants(conversationId) {
    const response = await api.get(
      `${BASE_URL}/conversations/${conversationId}/participants`,
    );
    return response.data?.data ?? response.data;
  },

  async getMessages(conversationId) {
    const response = await api.get(
      `${BASE_URL}/conversations/${conversationId}/messages`,
    );
    return response.data?.data ?? response.data;
  },

  async sendMessage(conversationId, content) {
    const response = await api.post(
      `${BASE_URL}/conversations/${conversationId}/messages`,
      { content },
    );
    return response.data?.data ?? response.data;
  },
};

export default passengerMessageService;
