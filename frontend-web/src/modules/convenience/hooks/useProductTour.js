// src/modules/convenience/hooks/useProductTour.js

import { useCallback, useEffect, useMemo, useState } from "react";

import productTourService from "../services/productTourService";

const useProductTour = () => {
  // STATE
  const [productTours, setProductTours] = useState([]);

  const [loading, setLoading] = useState(false);

  const [error, setError] = useState(null);

  const [completing, setCompleting] = useState(false);

  const [completeError, setCompleteError] = useState(null);

  // LOAD ALL
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

  // CREATE CONFIG
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

  // UPDATE CONFIG
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

  // COMPLETE TOUR CONFIGURATION
  const completeTourConfiguration = useCallback(
    async (tourId) => {
      try {
        setCompleting(true);
        setCompleteError(null);

        const result = await productTourService.completeConfiguration(tourId);

        await loadProductTours();

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
    [loadProductTours],
  );

  // INITIAL LOAD
  useEffect(() => {
    loadProductTours();
  }, [loadProductTours]);

  // TOUR SUMMARIES
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

      completed: entry.total > 0 && entry.configuredCount === entry.total,
    }));
  }, [productTours]);

  // RETURN
  return {
    productTours,
    tourSummaries,

    loading,
    error,

    completing,
    completeError,

    loadProductTours,

    configureProduct,
    updateProduct,
    completeTourConfiguration,
  };
};

export default useProductTour;
