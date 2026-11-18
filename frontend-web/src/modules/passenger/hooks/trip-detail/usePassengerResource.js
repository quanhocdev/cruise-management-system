// src/modules/passenger/hooks/trip-detail/usePassengerResource.js
import { useState, useCallback } from "react";

export default function usePassengerResource(fetcher, initialValue = null) {
  const [data, setData] = useState(initialValue);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const result = await fetcher();
      setData(result);
      return result;
    } catch (err) {
      const message =
        err.response?.data?.message || err.message || "Không thể tải dữ liệu.";

      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [fetcher]);

  return {
    data,
    setData,
    loading,
    error,
    load,
  };
}
