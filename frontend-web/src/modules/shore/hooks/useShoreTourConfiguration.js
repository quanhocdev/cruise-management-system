// src/modules/shore/hooks/useShoreTourConfiguration.js

import { useCallback, useEffect, useState } from "react";

import shoreTourService from "../services/shoreTourService";

const useShoreTourConfiguration = (tourId, status = null) => {
  const [configuration, setConfiguration] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadConfiguration = useCallback(async () => {
    if (!tourId) {
      setConfiguration(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const data = await shoreTourService.getConfiguration(tourId, status);

      setConfiguration(data);
    } catch (err) {
      console.error("🔥 LOAD SHORE TOUR CONFIGURATION ERROR:", err);

      setError(err);
      setConfiguration(null);
    } finally {
      setLoading(false);
    }
  }, [tourId, status]);

  useEffect(() => {
    loadConfiguration();
  }, [loadConfiguration]);

  return {
    configuration,
    loading,
    error,
    reload: loadConfiguration,
  };
};

export default useShoreTourConfiguration;
