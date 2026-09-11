// src/modules/onboard/hooks/useActivityCruiseTour.js
import { useCallback, useEffect, useState, useMemo } from "react";
import { activityCruiseTourService } from "../services/activityCruiseTourService";

const useActivityCruiseTour = () => {
  const [activities, setActivities] = useState([]);
  const [configurationHistory, setConfigurationHistory] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [statusFilter, setStatusFilter] = useState("ALL");

  const [completing, setCompleting] = useState(false);
  const [completeError, setCompleteError] = useState(null);

  const loadAllActivities = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const [activitiesData, historyData] = await Promise.all([
        activityCruiseTourService.getAll(),
        activityCruiseTourService.getConfigurationHistory(),
      ]);

      setActivities(activitiesData || []);
      setConfigurationHistory(historyData || []);
    } catch (err) {
      console.error("LOAD ACTIVITY CRUISE TOUR / HISTORY ERROR:", err);
      setError(
        err.response?.data?.message || "Không thể tải danh sách hoạt động",
      );
    } finally {
      setLoading(false);
    }
  }, []);

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

  useEffect(() => {
    loadAllActivities();
  }, [loadAllActivities]);

  // Lọc và khử trùng lặp dữ liệu tránh việc bị hiện bản ghi nhân đôi trong bảng
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

  const completedTourIds = useMemo(() => {
    return new Set(configurationHistory.map((history) => history.tourId));
  }, [configurationHistory]);

  const tourSummaries = useMemo(() => {
    const map = new Map();

    activities.forEach((item) => {
      if (!item.tourId) return;

      if (!map.has(item.tourId)) {
        map.set(item.tourId, {
          tourId: item.tourId,
          tourCode: item.tourCode || null,
          total: 0,
          configuredCount: 0,
          completed: completedTourIds.has(item.tourId),
        });
      }

      const entry = map.get(item.tourId);
      entry.total += 1;

      if (item.status === "CONFIGURED") {
        entry.configuredCount += 1;
      }
    });

    return Array.from(map.values());
  }, [activities, completedTourIds]);

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
