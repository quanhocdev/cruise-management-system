// src/modules/shore/hooks/useVisitTours.js

import { useCallback, useEffect, useState } from "react";

import visitTourService from "../services/visitTourService";

const useVisitTours = ({ tourId = null, scheduleStopId = null } = {}) => {
  const [visitTours, setVisitTours] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadVisitTours = useCallback(async () => {
    if (!tourId && !scheduleStopId) {
      setVisitTours([]);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      let data;

      if (scheduleStopId) {
        data = await visitTourService.getByScheduleStop(scheduleStopId);
      } else {
        data = await visitTourService.getByTour(tourId);
      }

      setVisitTours(data);
    } catch (err) {
      console.error("🔥 LOAD VISIT TOURS ERROR:", err);

      setError(err);
      setVisitTours([]);
    } finally {
      setLoading(false);
    }
  }, [tourId, scheduleStopId]);

  useEffect(() => {
    loadVisitTours();
  }, [loadVisitTours]);

  // =====================================================
  // CREATE
  // =====================================================

  const createVisitTour = useCallback(
    async (targetScheduleStopId, payload) => {
      setError(null);

      try {
        const data = await visitTourService.create(
          targetScheduleStopId,
          payload,
        );

        await loadVisitTours();

        return data;
      } catch (err) {
        console.error("🔥 CREATE VISIT TOUR ERROR:", err);

        setError(err);
        throw err;
      }
    },
    [loadVisitTours],
  );

  // =====================================================
  // UPDATE
  // =====================================================

  const updateVisitTour = useCallback(
    async (id, payload) => {
      setError(null);

      try {
        const data = await visitTourService.update(id, payload);

        await loadVisitTours();

        return data;
      } catch (err) {
        console.error("🔥 UPDATE VISIT TOUR ERROR:", err);

        setError(err);
        throw err;
      }
    },
    [loadVisitTours],
  );

  // =====================================================
  // DELETE
  // =====================================================

  const deleteVisitTour = useCallback(
    async (id) => {
      setError(null);

      try {
        await visitTourService.delete(id);

        await loadVisitTours();
      } catch (err) {
        console.error("🔥 DELETE VISIT TOUR ERROR:", err);

        setError(err);
        throw err;
      }
    },
    [loadVisitTours],
  );

  return {
    visitTours,
    loading,
    error,

    loadVisitTours,
    reload: loadVisitTours,

    createVisitTour,
    updateVisitTour,
    deleteVisitTour,
  };
};

export default useVisitTours;
