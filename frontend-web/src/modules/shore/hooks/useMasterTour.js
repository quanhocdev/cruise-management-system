// src/modules/shore/hooks/useMasterTour.js
import { useCallback, useEffect, useState } from "react";
import visitTourService from "../services/visitTourService";

const useMasterTour = (tourId) => {
  const [masterTour, setMasterTour] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadMasterTour = useCallback(async () => {
    if (!tourId) {
      setMasterTour(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const data = await visitTourService.getMasterTour(tourId);
      setMasterTour(data);
    } catch (err) {
      console.error("🔥 LOAD MASTER TOUR ERROR:", err);
      setError(err);
      setMasterTour(null);
    } finally {
      setLoading(false);
    }
  }, [tourId]);

  useEffect(() => {
    loadMasterTour();
  }, [loadMasterTour]);

  return {
    masterTour,
    loading,
    error,
    reload: loadMasterTour,
  };
};

export default useMasterTour;
