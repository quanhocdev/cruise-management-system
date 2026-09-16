// src/modules/finance/hooks/useFinanceTour.js
import { useCallback, useEffect, useState } from "react";
import financeService from "../services/financeService";

export const useFinanceTours = () => {
  const [tours, setTours] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchTours = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await financeService.getTours();
      setTours(data);
    } catch (err) {
      console.error("🔥 FETCH TOURS ERROR:", err);
      setError(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTours();
  }, [fetchTours]);

  return { tours, loading, error, reload: fetchTours };
};

export const useFinanceSchedule = (tourId) => {
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchSchedules = useCallback(async () => {
    if (!tourId) {
      setSchedules([]);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const data = await financeService.getSchedulesByTour(tourId);
      setSchedules(data);
    } catch (err) {
      console.error("🔥 FETCH SCHEDULES ERROR:", err);
      setError(err);
      setSchedules([]);
    } finally {
      setLoading(false);
    }
  }, [tourId]);

  useEffect(() => {
    fetchSchedules();
  }, [fetchSchedules]);

  return { schedules, loading, error, reload: fetchSchedules };
};
