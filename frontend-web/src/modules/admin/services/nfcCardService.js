// src/modules/admin/services/nfcCardService.js
import api from "../../../api/axios";

const NFC_CARD_BASE_URL = "/admin/nfc-cards";

const nfcCardService = {
  // =====================================================
  // GET ALL NFC CARDS
  // GET /api/admin/nfc-cards
  // =====================================================

  async getCards() {
    const response = await api.get(NFC_CARD_BASE_URL);

    return response.data;
  },

  // =====================================================
  // GET NFC CARD BY ID
  // GET /api/admin/nfc-cards/{id}
  // =====================================================

  async getCardById(id) {
    const response = await api.get(`${NFC_CARD_BASE_URL}/${id}`);

    return response.data;
  },

  // =====================================================
  // CREATE NFC CARD
  // POST /api/admin/nfc-cards
  // =====================================================

  async createCard(data) {
    const response = await api.post(NFC_CARD_BASE_URL, data);

    return response.data;
  },

  // =====================================================
  // UPDATE NFC CARD
  // PATCH /api/admin/nfc-cards/{id}
  // =====================================================

  async updateCard(id, data) {
    const response = await api.patch(`${NFC_CARD_BASE_URL}/${id}`, data);

    return response.data;
  },

  // =====================================================
  // DELETE NFC CARD
  // DELETE /api/admin/nfc-cards/{id}
  // =====================================================

  async deleteCard(id) {
    await api.delete(`${NFC_CARD_BASE_URL}/${id}`);
  },
};

export default nfcCardService;
