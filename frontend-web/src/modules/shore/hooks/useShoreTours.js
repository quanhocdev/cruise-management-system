// src/modules/shore/hooks/useShoreTours.js

import { useCallback, useEffect, useState } from "react";

import shoreTourService from "../services/shoreTourService";

const useShoreTours = () => {
  const [tours, setTours] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadTours = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await shoreTourService.getAvailableTours();

      setTours(data || []);
    } catch (err) {
      console.error("🔥 LOAD SHORE TOURS ERROR:", err);

      setError(err);
      setTours([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTours();
  }, [loadTours]);

  return {
    tours,
    loading,
    error,
    reload: loadTours,
  };
};

export default useShoreTours;
