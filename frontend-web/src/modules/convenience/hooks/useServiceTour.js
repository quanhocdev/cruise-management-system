// src/modules/convenience/hooks/useServiceTour.js

import { useCallback, useEffect, useState } from "react";

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
  // RETURN
  // =====================================================

  return {
    serviceTours,

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
