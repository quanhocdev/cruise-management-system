// src/modules/convenience/hooks/useServiceTour.js

import { useCallback, useEffect, useMemo, useState } from "react";

import serviceTourService from "../services/serviceTourService";

const useServiceTour = () => {
  // =====================================================
  // STATE
  // =====================================================

  const [serviceTours, setServiceTours] = useState([]);

  const [loading, setLoading] = useState(false);

  const [error, setError] = useState(null);

  const [completing, setCompleting] = useState(false);

  const [completeError, setCompleteError] = useState(null);

  // =====================================================
  // LOAD ALL
  // =====================================================

  const loadServiceTours = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await serviceTourService.getAll();

      setServiceTours(data || []);
    } catch (err) {
      console.error("LOAD SERVICE TOUR ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách dịch vụ của tour",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // CREATE CONFIG
  // =====================================================

  const configureService = useCallback(async (assignmentId, configData) => {
    try {
      setError(null);

      const updatedService = await serviceTourService.configure(
        assignmentId,
        configData,
      );

      setServiceTours((previous) =>
        previous.map((item) =>
          item.id === assignmentId ? updatedService : item,
        ),
      );

      return updatedService;
    } catch (err) {
      console.error("CONFIGURE SERVICE TOUR ERROR:", err);

      const message =
        err.response?.data?.message || "Không thể cấu hình dịch vụ";

      setError(message);

      throw err;
    }
  }, []);

  // =====================================================
  // UPDATE CONFIG
  // =====================================================

  const updateService = useCallback(async (assignmentId, configData) => {
    try {
      setError(null);

      const updatedService = await serviceTourService.updateConfig(
        assignmentId,
        configData,
      );

      setServiceTours((previous) =>
        previous.map((item) =>
          item.id === assignmentId ? updatedService : item,
        ),
      );

      return updatedService;
    } catch (err) {
      console.error("UPDATE SERVICE TOUR ERROR:", err);

      const message =
        err.response?.data?.message || "Không thể cập nhật cấu hình dịch vụ";

      setError(message);

      throw err;
    }
  }, []);

  // =====================================================
  // COMPLETE TOUR CONFIGURATION
  // =====================================================

  const completeTourConfiguration = useCallback(
    async (tourId) => {
      try {
        setCompleting(true);
        setCompleteError(null);

        const result = await serviceTourService.completeConfiguration(tourId);

        // Sau khi Complete, status của ServiceTour
        // được cập nhật thành CONFIGURED ở backend.
        // Chỉ cần load lại danh sách chính.
        await loadServiceTours();

        return result;
      } catch (err) {
        console.error("COMPLETE SERVICE TOUR ERROR:", err);

        const message =
          err.response?.data?.message || "Không thể hoàn thành cấu hình Tour";

        setCompleteError(message);

        throw err;
      } finally {
        setCompleting(false);
      }
    },
    [loadServiceTours],
  );

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadServiceTours();
  }, [loadServiceTours]);

  // =====================================================
  // TOUR SUMMARIES
  // =====================================================

  const tourSummaries = useMemo(() => {
    const map = new Map();

    serviceTours.forEach((item) => {
      if (!item.tourId) return;

      if (!map.has(item.tourId)) {
        map.set(item.tourId, {
          tourId: item.tourId,
          total: 0,
          configuredCount: 0,
        });
      }

      const entry = map.get(item.tourId);

      entry.total += 1;

      if (item.status === "CONFIGURED") {
        entry.configuredCount += 1;
      }
    });

    return Array.from(map.values()).map((entry) => ({
      ...entry,

      // Một Tour được xem là đã hoàn thành cấu hình
      // khi tất cả ServiceTour của Tour đều CONFIGURED.
      completed: entry.total > 0 && entry.configuredCount === entry.total,
    }));
  }, [serviceTours]);

  // =====================================================
  // RETURN
  // =====================================================

  return {
    serviceTours,
    tourSummaries,

    loading,
    error,

    completing,
    completeError,

    loadServiceTours,

    configureService,
    updateService,
    completeTourConfiguration,
  };
};

export default useServiceTour;
