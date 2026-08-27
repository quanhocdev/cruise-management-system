// src/modules/onboard/hooks/useActivityCruiseTourHistory.js
// src/modules/onboard/hooks/useActivityCruiseTourHistory.js

import { useCallback, useState } from "react";
import { activityCruiseTourService } from "../services/activityCruiseTourService";

const useActivityCruiseTourHistory = () => {
  const [history, setHistory] = useState([]);
  const [configurationDetail, setConfigurationDetail] = useState([]);

  const [loading, setLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);

  const [error, setError] = useState(null);
  const [detailError, setDetailError] = useState(null);

  // =====================================================
  // LOAD CONFIGURATION HISTORY
  // =====================================================

  const loadHistory = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await activityCruiseTourService.getConfigurationHistory();

      setHistory(data || []);
    } catch (err) {
      console.error(
        "LOAD ACTIVITY CRUISE TOUR CONFIGURATION HISTORY ERROR:",
        err,
      );

      setError(
        err.response?.data?.message || "Không thể tải lịch sử cấu hình Tour",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // LOAD CONFIGURATION DETAIL
  // =====================================================

  const loadConfigurationDetail = useCallback(async (tourId) => {
    if (!tourId) return;

    try {
      setDetailLoading(true);
      setDetailError(null);
      setConfigurationDetail([]);

      const data =
        await activityCruiseTourService.getConfigurationDetail(tourId);

      setConfigurationDetail(data || []);
    } catch (err) {
      console.error(
        "LOAD ACTIVITY CRUISE TOUR CONFIGURATION DETAIL ERROR:",
        err,
      );

      setDetailError(
        err.response?.data?.message || "Không thể tải chi tiết cấu hình Tour",
      );
    } finally {
      setDetailLoading(false);
    }
  }, []);

  // =====================================================
  // CLEAR DETAIL
  // =====================================================

  const clearConfigurationDetail = useCallback(() => {
    setConfigurationDetail([]);
    setDetailError(null);
  }, []);

  return {
    history,
    configurationDetail,

    loading,
    detailLoading,

    error,
    detailError,

    loadHistory,
    loadConfigurationDetail,
    clearConfigurationDetail,
  };
};

export default useActivityCruiseTourHistory;
