// src/modules/convenience/hooks/useServiceTour.js

import { useCallback, useEffect, useMemo, useState } from "react";

import serviceTourService from "../services/serviceTourService";

const useServiceTour = () => {
  // =====================================================
  // STATE
  // =====================================================

  const [serviceTours, setServiceTours] = useState([]);

  // ✅ Lịch sử các Tour đã hoàn thành cấu hình (HistoryServiceTourResponse[])
  const [completionHistory, setCompletionHistory] = useState([]);

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
  // LOAD CONFIGURATION HISTORY
  // =====================================================
  //
  // ✅ Đây là nguồn dữ liệu DUY NHẤT xác định 1 Tour đã
  // "Hoàn thành cấu hình" hay chưa — không được suy luận
  // từ status của từng ServiceTour (status không đổi sau
  // khi hoàn thành).
  //
  // =====================================================

  const loadCompletionHistory = useCallback(async () => {
    try {
      const data = await serviceTourService.getConfigurationHistory();

      setCompletionHistory(data || []);
    } catch (err) {
      console.error("LOAD SERVICE TOUR CONFIGURATION HISTORY ERROR:", err);
      // Không set error chung để tránh che mất lỗi load danh sách chính.
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

        // ✅ Load lại cả 2 nguồn: danh sách chính + lịch sử hoàn thành,
        // để nút "Hoàn thành" bị khóa lại NGAY sau khi thao tác xong.
        await Promise.all([loadServiceTours(), loadCompletionHistory()]);

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
    [loadServiceTours, loadCompletionHistory],
  );

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadServiceTours();
    loadCompletionHistory();
  }, [loadServiceTours, loadCompletionHistory]);

  // =====================================================
  // TOUR SUMMARIES (dùng cho dropdown + canComplete)
  // =====================================================

  const completedTourIds = useMemo(
    () => new Set(completionHistory.map((h) => h.tourId)),
    [completionHistory],
  );

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
      // ✅ cờ quyết định khóa nút "Hoàn thành", lấy từ configuration-history
      // (nguồn đáng tin cậy), KHÔNG suy ra từ status.
      completed: completedTourIds.has(entry.tourId),
    }));
  }, [serviceTours, completedTourIds]);

  // =====================================================
  // RETURN
  // =====================================================

  return {
    serviceTours,
    completionHistory,
    tourSummaries,

    loading,
    error,

    completing,
    completeError,

    loadServiceTours,
    loadCompletionHistory,

    configureService,
    updateService,
    completeTourConfiguration,
  };
};

export default useServiceTour;
