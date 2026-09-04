// src/modules/shore/hooks/useVisitTourHistory.js
import { useCallback, useState } from "react";
import visitTourService from "../services/visitTourService";

const useVisitTourHistory = () => {
  const [history, setHistory] = useState([]);
  const [configurationDetail, setConfigurationDetail] = useState([]);

  const [loading, setLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);

  const [error, setError] = useState(null);
  const [detailError, setDetailError] = useState(null);

  const loadHistory = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await visitTourService.getConfigurationHistory();

      setHistory(data || []);
    } catch (err) {
      console.error("LOAD VISIT TOUR HISTORY ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải lịch sử cấu hình Visit Tour",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const loadConfigurationDetail = useCallback(async (tourId) => {
    if (!tourId) return;

    try {
      setDetailLoading(true);
      setDetailError(null);
      setConfigurationDetail([]);

      const data = await visitTourService.getConfigurationHistoryDetail(tourId);

      setConfigurationDetail(data || []);
    } catch (err) {
      console.error("LOAD VISIT TOUR CONFIGURATION DETAIL ERROR:", err);

      setDetailError(
        err.response?.data?.message ||
          "Không thể tải chi tiết cấu hình Visit Tour",
      );
    } finally {
      setDetailLoading(false);
    }
  }, []);

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

export default useVisitTourHistory;
