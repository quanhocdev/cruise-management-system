// src/modules/admin/hooks/useCruises.js
import { useCallback, useEffect, useState } from "react";
import cruiseService from "../services/cruiseService";

export default function useCruiseDecks(cruiseId) {
  const [decks, setDecks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // LOAD DECKS
  // =====================================================

  const loadDecks = useCallback(async () => {
    if (!cruiseId) {
      setDecks([]);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const data = await cruiseService.getDecks(cruiseId);

      setDecks(data);
    } catch (err) {
      console.error("🔥 LOAD DECKS ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể tải danh sách tầng.");
    } finally {
      setLoading(false);
    }
  }, [cruiseId]);

  useEffect(() => {
    loadDecks();
  }, [loadDecks]);

  // =====================================================
  // CREATE DECK
  // =====================================================

  const createDeck = async (data) => {
    setError("");
    setSuccess("");

    try {
      const createdDeck = await cruiseService.createDeck(cruiseId, data);

      setDecks((previous) =>
        [...previous, createdDeck].sort((a, b) => a.deckNumber - b.deckNumber),
      );

      setSuccess("Tạo tầng thành công.");

      return createdDeck;
    } catch (err) {
      console.error("🔥 CREATE DECK ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể tạo tầng.");

      return null;
    }
  };

  // =====================================================
  // UPDATE DECK
  // =====================================================

  const updateDeck = async (deckId, data) => {
    setError("");
    setSuccess("");

    try {
      const updatedDeck = await cruiseService.updateDeck(
        cruiseId,
        deckId,
        data,
      );

      setDecks((previous) =>
        previous
          .map((deck) => (deck.id === deckId ? updatedDeck : deck))
          .sort((a, b) => a.deckNumber - b.deckNumber),
      );

      setSuccess("Cập nhật tầng thành công.");

      return updatedDeck;
    } catch (err) {
      console.error("🔥 UPDATE DECK ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể cập nhật tầng.");

      return null;
    }
  };

  // =====================================================
  // DELETE DECK
  // =====================================================

  const deleteDeck = async (deckId) => {
    setError("");
    setSuccess("");

    try {
      await cruiseService.deleteDeck(cruiseId, deckId);

      setDecks((previous) => previous.filter((deck) => deck.id !== deckId));

      setSuccess("Xóa tầng thành công.");

      return true;
    } catch (err) {
      console.error("🔥 DELETE DECK ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể xóa tầng.");

      return false;
    }
  };

  // =====================================================
  // RETURN
  // =====================================================

  return {
    decks,
    loading,
    error,
    success,

    setError,
    setSuccess,

    loadDecks,
    createDeck,
    updateDeck,
    deleteDeck,
  };
}
