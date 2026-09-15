// src/modules/admin/services/nfcCardService.js

import api from "../../../api/axios";

const NFC_CARD_BASE_URL = "/admin/nfc-cards";

const nfcCardService = {
  // =====================================================
  // GET ALL CARDS
  // =====================================================
  async getCards() {
    console.log("📡 [NFC Service] Calling GET: ", NFC_CARD_BASE_URL);
    try {
      const response = await api.get(NFC_CARD_BASE_URL);
      console.log("📥 [NFC Service] GET Response:", response.data);
      return response.data;
    } catch (error) {
      console.error("🔥 [NFC Service] GET Error:", error);
      throw error;
    }
  },

  // =====================================================
  // GET CARD BY ID
  // =====================================================
  async getCardById(id) {
    console.log(
      `📡 [NFC Service] Calling GET by ID: ${NFC_CARD_BASE_URL}/${id}`,
    );
    try {
      const response = await api.get(`${NFC_CARD_BASE_URL}/${id}`);
      console.log("📥 [NFC Service] GET by ID Response:", response.data);
      return response.data;
    } catch (error) {
      console.error(`🔥 [NFC Service] GET by ID Error (${id}):`, error);
      throw error;
    }
  },

  // =====================================================
  // CREATE CARD
  // =====================================================
  async createCard(data) {
    console.log(`📡 [NFC Service] Calling POST: ${NFC_CARD_BASE_URL}`, data);
    try {
      const response = await api.post(NFC_CARD_BASE_URL, data);
      console.log("📥 [NFC Service] POST Response:", response.data);
      return response.data;
    } catch (error) {
      console.error("🔥 [NFC Service] POST Error:", error);
      throw error;
    }
  },

  // =====================================================
  // UPDATE CARD
  // =====================================================
  async updateCard(id, data) {
    console.log(
      `📡 [NFC Service] Calling PATCH: ${NFC_CARD_BASE_URL}/${id}`,
      data,
    );
    try {
      const response = await api.patch(`${NFC_CARD_BASE_URL}/${id}`, data);
      console.log("📥 [NFC Service] PATCH Response:", response.data);
      return response.data;
    } catch (error) {
      console.error(`🔥 [NFC Service] PATCH Error (${id}):`, error);
      throw error;
    }
  },

  // =====================================================
  // DELETE CARD
  // =====================================================
  async deleteCard(id) {
    console.log(`📡 [NFC Service] Calling DELETE: ${NFC_CARD_BASE_URL}/${id}`);
    try {
      const response = await api.delete(`${NFC_CARD_BASE_URL}/${id}`);
      console.log(`📥 [NFC Service] DELETE Success (${id})`);
      return response.data;
    } catch (error) {
      console.error(`🔥 [NFC Service] DELETE Error (${id}):`, error);
      throw error;
    }
  },
};

export default nfcCardService;
