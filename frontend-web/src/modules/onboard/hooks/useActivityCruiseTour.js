// src/modules/onboard/hooks/useActivityCruiseTour.js

import { useCallback, useEffect, useState } from "react";

import { activityCruiseTourService } from "../services/activityCruiseTourService";

const useActivityCruiseTour = () => {
  // =====================================================
  // STATE
  // =====================================================

  const [pendingActivities, setPendingActivities] = useState([]);

  const [loading, setLoading] = useState(false);

  const [error, setError] = useState(null);

  // =====================================================
  // LOAD PENDING CONFIG
  // =====================================================

  const loadPendingActivities = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await activityCruiseTourService.getPendingConfig();

      setPendingActivities(data || []);
    } catch (err) {
      console.error("LOAD PENDING ACTIVITY CRUISE TOUR ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách hoạt động cần cấu hình",
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

      const updatedActivity = await activityCruiseTourService.configure(
        assignmentId,
        configData,
      );

      // WAITING_CONFIG -> NOT_STARTED
      //
      // Assignment không còn nằm trong pending list.

      setPendingActivities((previous) =>
        previous.filter((item) => item.id !== assignmentId),
      );

      return updatedActivity;
    } catch (err) {
      console.error("CONFIGURE ACTIVITY CRUISE TOUR ERROR:", err);

      const message =
        err.response?.data?.message || "Không thể cấu hình hoạt động";

      setError(message);

      throw err;
    }
  }, []);

  // =====================================================
  // UPDATE CONFIG
  // =====================================================

  const updateActivityConfig = useCallback(async (assignmentId, configData) => {
    try {
      setError(null);

      return await activityCruiseTourService.updateConfig(
        assignmentId,
        configData,
      );
    } catch (err) {
      console.error("UPDATE ACTIVITY CRUISE TOUR ERROR:", err);

      const message =
        err.response?.data?.message || "Không thể cập nhật cấu hình hoạt động";

      setError(message);

      throw err;
    }
  }, []);

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadPendingActivities();
  }, [loadPendingActivities]);

  // =====================================================
  // RETURN
  // =====================================================

  return {
    // Data
    pendingActivities,

    // State
    loading,
    error,

    // Actions
    loadPendingActivities,
    configureActivity,
    updateActivityConfig,
  };
};

export default useActivityCruiseTour;
