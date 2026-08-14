import { useCallback, useEffect, useState } from "react";
import api from "../../../api/axios";

export default function usePorts() {
  const [ports, setPorts] = useState([]);
  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadPorts = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get("/admin/ports");

      const data = response.data;

      setPorts(
        Array.isArray(data) ? data : Array.isArray(data?.data) ? data.data : [],
      );
    } catch (error) {
      console.error("Load ports error:", error);

      setError(
        error.response?.data?.message || "Không thể tải danh sách cảng.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadPorts();
  }, [loadPorts]);

  const createPort = async (requestData) => {
    setError("");
    setSuccess("");

    try {
      await api.post("/admin/ports", requestData);

      setSuccess("Tạo cảng thành công.");

      await loadPorts();

      return true;
    } catch (error) {
      console.error("Create port error:", error);

      setError(error.response?.data?.message || "Không thể tạo cảng.");

      return false;
    }
  };

  const updatePort = async (id, requestData) => {
    setError("");
    setSuccess("");

    try {
      await api.patch(`/admin/ports/${id}`, requestData);

      setSuccess("Cập nhật cảng thành công.");

      await loadPorts();

      return true;
    } catch (error) {
      console.error("Update port error:", error);

      setError(error.response?.data?.message || "Không thể cập nhật cảng.");

      return false;
    }
  };

  const deactivatePort = async (id) => {
    setError("");
    setSuccess("");

    try {
      await api.delete(`/admin/ports/${id}`);

      setSuccess("Đã vô hiệu hóa cảng.");

      await loadPorts();

      return true;
    } catch (error) {
      console.error("Deactivate port error:", error);

      setError(error.response?.data?.message || "Không thể vô hiệu hóa cảng.");

      return false;
    }
  };

  return {
    ports,
    loading,

    error,
    success,

    setError,
    setSuccess,

    loadPorts,
    createPort,
    updatePort,
    deactivatePort,
  };
}
