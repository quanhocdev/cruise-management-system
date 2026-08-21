import { useCallback, useState } from "react";
import operationTourConfigurationService from "../services/operationTourConfigurationService";

const useOperationTourConfiguration = () => {
  const [configuration, setConfiguration] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadConfiguration = useCallback(async (tourId) => {
    if (!tourId) return null;

    try {
      setLoading(true);
      setError(null);

      const data =
        await operationTourConfigurationService.getConfiguration(tourId);
      setConfiguration(data);
      return data;
    } catch (err) {
      console.error("LOAD TOUR CONFIGURATION ERROR:", err);

      const errorMessage =
        err?.response?.data?.message ||
        err?.message ||
        "Không thể tải cấu hình Tour";

      setError(errorMessage);
      setConfiguration(null);

      // Trả về null thay vì re-throw nếu muốn UI xử lý an toàn thông qua state `error`
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  const clearConfiguration = useCallback(() => {
    setConfiguration(null);
    setError(null);
  }, []);

  return {
    configuration,
    loading,
    error,
    loadConfiguration,
    clearConfiguration,
  };
};

export default useOperationTourConfiguration;
