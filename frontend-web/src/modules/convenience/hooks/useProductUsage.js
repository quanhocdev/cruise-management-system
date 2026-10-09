// src/modules/convenience/hooks/useProductUsage.js
import { useCallback, useEffect, useState } from "react";
import productUsageService from "../services/productUsageService";

const useProductUsage = () => {
  const [productUsages, setProductUsages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchProductUsages = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await productUsageService.getAll();

      setProductUsages(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to fetch product usage history:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Không thể tải lịch sử sử dụng sản phẩm.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProductUsages();
  }, [fetchProductUsages]);

  return {
    productUsages,
    loading,
    error,
    refetch: fetchProductUsages,
  };
};

export default useProductUsage;
