// src/modules/finance/hooks/useFinanceNfc.js
import { useCallback, useEffect, useState } from "react";
import financeService from "../services/financeService";

const useFinanceNfc = () => {
  const [cards, setCards] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchCards = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await financeService.getNfcCards();
      setCards(data);
    } catch (err) {
      console.error("🔥 FETCH NFC CARDS ERROR:", err);
      setError(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchCards();
  }, [fetchCards]);

  return { cards, loading, error, reload: fetchCards };
};

export default useFinanceNfc;
