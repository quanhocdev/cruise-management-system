// src/modules/convenience/hooks/useProductTour.js

import { useCallback, useEffect, useState } from "react";

import productTourService from "../services/productTourService";

const useProductTour = () => {
  // =====================================================
  // STATE
  // =====================================================

  const [productTours, setProductTours] = useState([]);

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

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadProductTours();
  }, [loadProductTours]);

  // =====================================================
  // RETURN
  // =====================================================

  return {
    productTours,

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
