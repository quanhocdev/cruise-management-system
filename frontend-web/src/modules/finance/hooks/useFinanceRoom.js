// src/modules/finance/hooks/useFinanceRoom.js
import { useCallback, useEffect, useState } from "react";
import financeService from "../services/financeService";

const useFinanceRoom = (deckId) => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchRooms = useCallback(async () => {
    if (!deckId) {
      setRooms([]);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const data = await financeService.getRoomsByDeck(deckId);
      setRooms(data);
    } catch (err) {
      console.error("🔥 FETCH ROOMS ERROR:", err);
      setError(err);
      setRooms([]);
    } finally {
      setLoading(false);
    }
  }, [deckId]);

  useEffect(() => {
    fetchRooms();
  }, [fetchRooms]);

  return { rooms, loading, error, reload: fetchRooms };
};

export default useFinanceRoom;
