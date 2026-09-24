// src/modules/onboard/hooks/useActivityCruiseTour.js

import { useCallback, useEffect, useMemo, useState } from "react";
import { activityCruiseTourService } from "../services/activityCruiseTourService";

const useActivityCruiseTour = () => {
  const [activities, setActivities] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [statusFilter, setStatusFilter] = useState("ALL");

  const [completing, setCompleting] = useState(false);
  const [completeError, setCompleteError] = useState(null);

  // =====================================================
  // LOAD ALL ACTIVITY CRUISE TOURS
  // =====================================================

  const loadAllActivities = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const activitiesData = await activityCruiseTourService.getAll();

      setActivities(activitiesData || []);
    } catch (err) {
      console.error("LOAD ACTIVITY CRUISE TOUR ERROR:", err);

      setError(
        err.response?.data?.message || "Không thể tải danh sách hoạt động",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // CONFIGURE ACTIVITY
  // =====================================================

  const configureActivity = useCallback(async (assignmentId, configData) => {
    try {
      setError(null);

      const updatedItem = await activityCruiseTourService.configure(
        assignmentId,
        configData,
      );

      setActivities((prev) =>
        prev.map((item) => (item.id === assignmentId ? updatedItem : item)),
      );

      return updatedItem;
    } catch (err) {
      console.error("CONFIGURE ACTIVITY CRUISE TOUR ERROR:", err);

      const message =
        err.response?.data?.message || "Không thể cấu hình hoạt động";

      setError(message);

      throw err;
    }
  }, []);

  // =====================================================
  // UPDATE ACTIVITY CONFIG
  // =====================================================

  const updateActivityConfig = useCallback(async (assignmentId, configData) => {
    try {
      setError(null);

      const updatedItem = await activityCruiseTourService.updateConfig(
        assignmentId,
        configData,
      );

      setActivities((prev) =>
        prev.map((item) => (item.id === assignmentId ? updatedItem : item)),
      );

      return updatedItem;
    } catch (err) {
      console.error("UPDATE ACTIVITY CRUISE TOUR ERROR:", err);

      const message =
        err.response?.data?.message || "Không thể cập nhật cấu hình hoạt động";

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

        await activityCruiseTourService.completeTourConfiguration(tourId);

        await loadAllActivities();

        return true;
      } catch (err) {
        console.error("COMPLETE TOUR CONFIGURATION ERROR:", err);

        const message =
          err.response?.data?.message ||
          "Không thể hoàn thành cấu hình cho Tour này";

        setCompleteError(message);

        throw err;
      } finally {
        setCompleting(false);
      }
    },
    [loadAllActivities],
  );

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadAllActivities();
  }, [loadAllActivities]);

  // =====================================================
  // FILTER + REMOVE DUPLICATES
  // =====================================================

  const filteredActivities = useMemo(() => {
    const uniqueMap = new Map();

    activities.forEach((item) => {
      const uniqueKey = item.id || item.assignmentId;

      if (uniqueKey && !uniqueMap.has(uniqueKey)) {
        uniqueMap.set(uniqueKey, item);
      }
    });

    const uniqueList = Array.from(uniqueMap.values());

    if (statusFilter === "ALL") {
      return uniqueList;
    }

    return uniqueList.filter((item) => item.status === statusFilter);
  }, [activities, statusFilter]);

  // =====================================================
  // TOUR SUMMARIES
  // =====================================================
  //
  // Không còn dùng Configuration History.
  //
  // Một Tour được xem là đã hoàn thành cấu hình khi:
  //
  //   configuredCount === total
  //
  // và total > 0.
  //
  // CONFIGURED = Operation đã Complete Configuration.
  // =====================================================

  const tourSummaries = useMemo(() => {
    const map = new Map();

    activities.forEach((item) => {
      if (!item.tourId) {
        return;
      }

      if (!map.has(item.tourId)) {
        map.set(item.tourId, {
          tourId: item.tourId,
          tourCode: item.tourCode || null,
          total: 0,
          configuredCount: 0,
          completed: false,
        });
      }

      const entry = map.get(item.tourId);

      entry.total += 1;

      if (item.status === "CONFIGURED") {
        entry.configuredCount += 1;
      }
    });

    map.forEach((entry) => {
      entry.completed =
        entry.total > 0 && entry.configuredCount === entry.total;
    });

    return Array.from(map.values());
  }, [activities]);

  // =====================================================
  // RETURN
  // =====================================================

  return {
    activities,
    filteredActivities,

    statusFilter,
    setStatusFilter,

    tourSummaries,

    loading,
    error,

    completing,
    completeError,

    loadAllActivities,
    configureActivity,
    updateActivityConfig,
    completeTourConfiguration,
  };
};

export default useActivityCruiseTour;
