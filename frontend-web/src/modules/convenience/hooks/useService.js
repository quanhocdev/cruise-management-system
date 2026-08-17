import { useCallback, useState } from "react";
import convenienceService from "../services/convenienceServiceService";

export default function useService() {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  /**
   * LOAD SERVICES (Chỉ xem)
   */
  const loadServices = useCallback(async (params) => {
    setLoading(true);
    setError("");

    try {
      const data = await convenienceService.getAllServices(params);
      console.log("RAW DATA FROM SERVICE:", data); // Add dòng này để xem hình dáng dữ liệu thực sự
      const serviceList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setServices(serviceList);
    } catch (err) {
      console.error("LOAD CONVENIENCE SERVICES ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách dịch vụ tiện ích.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const clearMessages = useCallback(() => {
    setError("");
  }, []);

  return {
    services,
    loading,
    error,
    loadServices,
    clearMessages,
  };
}
