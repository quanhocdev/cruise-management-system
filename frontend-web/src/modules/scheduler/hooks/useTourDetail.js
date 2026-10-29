// src/modules/scheduler/hooks/useTourDetail.js
// (FILE MỚI) Tải 1 Tour theo id và tính danh sách các ngày của Tour.

import { useCallback, useEffect, useMemo, useState } from "react";

import tourService from "../services/tourService";

const toDateKey = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");

  return `${year}-${month}-${day}`;
};

const useTourDetail = (tourId) => {
  const [tour, setTour] = useState(null);
  const [loading, setLoading] = useState(false);

  const loadTour = useCallback(async () => {
    if (!tourId) return;

    setLoading(true);

    try {
      setTour(await tourService.getTourById(tourId));
    } catch (err) {
      console.error("LOAD TOUR ERROR:", err);
    } finally {
      setLoading(false);
    }
  }, [tourId]);

  useEffect(() => {
    loadTour();
  }, [loadTour]);

  // [{ dayNumber: 1, realDay: "2026-10-10" }, ...]
  const tourDays = useMemo(() => {
    if (!tour?.startDate || !tour?.endDate) return [];

    const end = new Date(`${tour.endDate}T00:00:00`);
    const current = new Date(`${tour.startDate}T00:00:00`);
    const days = [];

    for (let dayNumber = 1; current <= end; dayNumber += 1) {
      days.push({ dayNumber, realDay: toDateKey(current) });
      current.setDate(current.getDate() + 1);
    }

    return days;
  }, [tour]);

  return { tour, tourDays, loading, reload: loadTour };
};

export default useTourDetail;
