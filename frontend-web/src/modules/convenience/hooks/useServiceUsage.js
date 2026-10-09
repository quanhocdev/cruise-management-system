// src/modules/convenience/hooks/useServiceUsage.js
import { useCallback, useEffect, useState } from "react";
import serviceUsageService from "../services/serviceUsageService";

const useServiceUsage = () => {
  const [serviceUsages, setServiceUsages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchServiceUsages = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await serviceUsageService.getAll();

      setServiceUsages(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to fetch service usage history:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Không thể tải lịch sử sử dụng dịch vụ.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchServiceUsages();
  }, [fetchServiceUsages]);

  return {
    serviceUsages,
    loading,
    error,
    refetch: fetchServiceUsages,
  };
};

export default useServiceUsage;
