// src/modules/convenience/hooks/useProductTour.js

import { useCallback, useEffect, useMemo, useState } from "react";

import productTourService from "../services/productTourService";

const useProductTour = () => {
  // =====================================================
  // STATE
  // =====================================================

  const [productTours, setProductTours] = useState([]);

  // ✅ Lịch sử các Tour đã hoàn thành cấu hình (HistoryProductTourResponse[])
  const [completionHistory, setCompletionHistory] = useState([]);

  const [loading, setLoading] = useState(false);

  const [error, setError] = useState(null);

  const [completing, setCompleting] = useState(false);

  const [completeError, setCompleteError] = useState(null);

  // =====================================================
  // LOAD ALL
  // =====================================================

  const loadProductTours = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await productTourService.getAll();

      setProductTours(data || []);
    } catch (err) {
      console.error("LOAD PRODUCT TOUR ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách sản phẩm của tour",
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
  // từ status của từng ProductTour (status không đổi sau
  // khi hoàn thành).
  //
  // =====================================================

  const loadCompletionHistory = useCallback(async () => {
    try {
      const data = await productTourService.getConfigurationHistory();

      setCompletionHistory(data || []);
    } catch (err) {
      console.error("LOAD PRODUCT TOUR CONFIGURATION HISTORY ERROR:", err);
      // Không set error chung để tránh che mất lỗi load danh sách chính.
    }
  }, []);

  // =====================================================
  // CREATE CONFIG
  // =====================================================

  const configureProduct = useCallback(async (assignmentId, configData) => {
    try {
      setError(null);

      const updatedProduct = await productTourService.configure(
        assignmentId,
        configData,
      );

      setProductTours((previous) =>
        previous.map((item) =>
          item.id === assignmentId ? updatedProduct : item,
        ),
      );

      return updatedProduct;
    } catch (err) {
      console.error("CONFIGURE PRODUCT TOUR ERROR:", err);

      const message =
        err.response?.data?.message || "Không thể cấu hình sản phẩm";

      setError(message);

      throw err;
    }
  }, []);

  // =====================================================
  // UPDATE CONFIG
  // =====================================================

  const updateProduct = useCallback(async (assignmentId, configData) => {
    try {
      setError(null);

      const updatedProduct = await productTourService.updateConfig(
        assignmentId,
        configData,
      );

      setProductTours((previous) =>
        previous.map((item) =>
          item.id === assignmentId ? updatedProduct : item,
        ),
      );

      return updatedProduct;
    } catch (err) {
      console.error("UPDATE PRODUCT TOUR ERROR:", err);

      const message =
        err.response?.data?.message || "Không thể cập nhật cấu hình sản phẩm";

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

        const result = await productTourService.completeConfiguration(tourId);

        // ✅ Load lại cả 2 nguồn: danh sách chính + lịch sử hoàn thành,
        // để nút "Hoàn thành" bị khóa lại NGAY sau khi thao tác xong.
        await Promise.all([loadProductTours(), loadCompletionHistory()]);

        return result;
      } catch (err) {
        console.error("COMPLETE PRODUCT TOUR ERROR:", err);

        const message =
          err.response?.data?.message || "Không thể hoàn thành cấu hình Tour";

        setCompleteError(message);

        throw err;
      } finally {
        setCompleting(false);
      }
    },
    [loadProductTours, loadCompletionHistory],
  );

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadProductTours();
    loadCompletionHistory();
  }, [loadProductTours, loadCompletionHistory]);

  // =====================================================
  // TOUR SUMMARIES (dùng cho dropdown + canComplete)
  // =====================================================

  const completedTourIds = useMemo(
    () => new Set(completionHistory.map((h) => h.tourId)),
    [completionHistory],
  );

  const tourSummaries = useMemo(() => {
    const map = new Map();

    productTours.forEach((item) => {
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
  }, [productTours, completedTourIds]);

  // =====================================================
  // RETURN
  // =====================================================

  return {
    productTours,
    completionHistory,
    tourSummaries,

    loading,
    error,

    completing,
    completeError,

    loadProductTours,
    loadCompletionHistory,

    configureProduct,
    updateProduct,
    completeTourConfiguration,
  };
};

export default useProductTour;
