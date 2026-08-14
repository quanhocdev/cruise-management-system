import { useCallback, useEffect, useState } from "react";
import cruiseService from "../services/cruiseService";

export default function useCruiseDecks(cruiseId) {
  const [decks, setDecks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadDecks = useCallback(async () => {
    if (!cruiseId) {
      setDecks([]);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await cruiseService.getDecks(cruiseId);

      setDecks(response.data);
    } catch (err) {
      setError(err.response?.data?.message || "Không thể tải danh sách tầng.");
    } finally {
      setLoading(false);
    }
  }, [cruiseId]);

  useEffect(() => {
    loadDecks();
  }, [loadDecks]);

  const createDeck = async (data) => {
    setError("");
    setSuccess("");

    try {
      const response = await cruiseService.createDeck(cruiseId, data);

      setDecks((previous) => [...previous, response.data]);

      setDecks((previous) =>
        [...previous].sort((a, b) => a.deckNumber - b.deckNumber),
      );

      setSuccess("Tạo tầng thành công.");

      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể tạo tầng.");

      return null;
    }
  };

  const updateDeck = async (deckId, data) => {
    setError("");
    setSuccess("");

    try {
      const response = await cruiseService.updateDeck(cruiseId, deckId, data);

      setDecks((previous) =>
        previous
          .map((deck) => (deck.id === deckId ? response.data : deck))
          .sort((a, b) => a.deckNumber - b.deckNumber),
      );

      setSuccess("Cập nhật tầng thành công.");

      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể cập nhật tầng.");

      return null;
    }
  };

  const deleteDeck = async (deckId) => {
    setError("");
    setSuccess("");

    try {
      await cruiseService.deleteDeck(cruiseId, deckId);

      setDecks((previous) => previous.filter((deck) => deck.id !== deckId));

      setSuccess("Xóa tầng thành công.");

      return true;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể xóa tầng.");

      return false;
    }
  };

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
