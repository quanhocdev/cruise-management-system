// src/modules/guest/hooks/usePublicTours.js
import { useState, useEffect, useCallback } from "react";
import { publicTourService } from "../services/publicTourService";

// Hook cho danh sách Tour (Trang chủ / Danh sách)
export const usePublicTours = () => {
  const [tours, setTours] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchTours = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await publicTourService.getAll();
      setTours(data);
      return data;
    } catch (err) {
      console.error("🔥 FETCH PUBLIC TOURS ERROR:", err);
      const message =
        err.response?.data?.message ||
        "Không thể tải danh sách tour công khai.";
      setError(message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTours();
  }, [fetchTours]);

  return {
    tours,
    loading,
    error,
    refresh: fetchTours,
  };
};

// Hook cho chi tiết 1 Tour (Trang chi tiết)
export const usePublicTourDetail = (tourId) => {
  const [tour, setTour] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchTourDetail = useCallback(async (id) => {
    if (!id) return;
    setLoading(true);
    setError("");
    try {
      const data = await publicTourService.getById(id);
      setTour(data);
      return data;
    } catch (err) {
      console.error("🔥 FETCH TOUR DETAIL ERROR:", err);
      const message =
        err.response?.data?.message || "Không thể tải thông tin chi tiết tour.";
      setError(message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (tourId) {
      fetchTourDetail(tourId);
    }
  }, [tourId, fetchTourDetail]);

  return {
    tour,
    loading,
    error,
    refresh: () => fetchTourDetail(tourId),
  };
};
