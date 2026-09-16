// src/modules/admin/hooks/useNfcCard.js
import { useCallback, useEffect, useState } from "react";

import nfcCardService from "../services/nfcCardService";

export default function useNfcCard() {
  const [cards, setCards] = useState([]);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // LOAD CARDS
  // =====================================================

  const loadCards = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await nfcCardService.getCards();

      setCards(Array.isArray(data) ? data : []);

      return Array.isArray(data) ? data : [];
    } catch (err) {
      console.error("🔥 LOAD NFC CARDS ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể tải danh sách thẻ NFC.";

      setError(message);

      return [];
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadCards();
  }, [loadCards]);

  // =====================================================
  // CREATE
  // =====================================================

  const createCard = useCallback(async (data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const createdCard = await nfcCardService.createCard(data);

      setCards((prev) => {
        const exists = prev.some((card) => card.id === createdCard.id);

        if (exists) {
          return prev;
        }

        return [...prev, createdCard].sort((a, b) =>
          (a.cardUid || "").localeCompare(b.cardUid || "", "vi"),
        );
      });

      setSuccess("Tạo thẻ NFC thành công.");

      return createdCard;
    } catch (err) {
      console.error("🔥 CREATE NFC CARD ERROR:", err);

      const message = err?.response?.data?.message || "Không thể tạo thẻ NFC.";

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // UPDATE
  // =====================================================

  const updateCard = useCallback(async (id, data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const updatedCard = await nfcCardService.updateCard(id, data);

      setCards((prev) =>
        prev
          .map((card) => (card.id === id ? updatedCard : card))
          .sort((a, b) =>
            (a.cardUid || "").localeCompare(b.cardUid || "", "vi"),
          ),
      );

      setSuccess("Cập nhật thẻ NFC thành công.");

      return updatedCard;
    } catch (err) {
      console.error("🔥 UPDATE NFC CARD ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể cập nhật thẻ NFC.";

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // DELETE
  // =====================================================

  const deleteCard = useCallback(async (id) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await nfcCardService.deleteCard(id);

      setCards((prev) => prev.filter((card) => card.id !== id));

      setSuccess("Đã xóa thẻ NFC.");

      return true;
    } catch (err) {
      console.error("🔥 DELETE NFC CARD ERROR:", err);

      const message = err?.response?.data?.message || "Không thể xóa thẻ NFC.";

      setError(message);

      return false;
    } finally {
      setSaving(false);
    }
  }, []);

  return {
    cards,

    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadCards,
    createCard,
    updateCard,
    deleteCard,
  };
}
