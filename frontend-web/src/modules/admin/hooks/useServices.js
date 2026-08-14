import { useCallback, useEffect, useState } from "react";
import api from "../../../api/axios";

export default function useServices() {
  const [services, setServices] = useState([]);

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadServices = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get("/admin/services");

      const data = response.data;

      setServices(
        Array.isArray(data)
          ? data
          : Array.isArray(data?.data)
            ? data.data
            : Array.isArray(data?.content)
              ? data.content
              : [],
      );
    } catch (error) {
      console.error("Load services error:", error);

      if (error.code === "ERR_NETWORK") {
        setError("Backend chưa chạy. Chưa thể tải dữ liệu dịch vụ.");
      } else {
        setError(
          error.response?.data?.message || "Không thể tải danh sách dịch vụ.",
        );
      }

      setServices([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadServices();
  }, [loadServices]);

  /*
   * =====================================================
   * CREATE
   * =====================================================
   */
  const createService = async (formData) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await api.post("/admin/services", formData);

      setSuccess("Tạo dịch vụ thành công.");

      await loadServices();

      return true;
    } catch (error) {
      console.error("Create service error:", error);

      if (error.code === "ERR_NETWORK") {
        setError("Backend chưa chạy. Không thể tạo dịch vụ.");
      } else {
        setError(error.response?.data?.message || "Không thể tạo dịch vụ.");
      }

      return false;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * UPDATE
   * =====================================================
   */
  const updateService = async (serviceId, formData) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await api.patch(`/admin/services/${serviceId}`, formData);

      setSuccess("Cập nhật dịch vụ thành công.");

      await loadServices();

      return true;
    } catch (error) {
      console.error("Update service error:", error);

      if (error.code === "ERR_NETWORK") {
        setError("Backend chưa chạy. Không thể cập nhật dịch vụ.");
      } else {
        setError(
          error.response?.data?.message || "Không thể cập nhật dịch vụ.",
        );
      }

      return false;
    } finally {
      setSaving(false);
    }
  };

  /*
   * =====================================================
   * DELETE
   * =====================================================
   */
  const deleteService = async (serviceId) => {
    setError("");
    setSuccess("");

    try {
      await api.delete(`/admin/services/${serviceId}`);

      setSuccess("Xóa dịch vụ thành công.");

      await loadServices();

      return true;
    } catch (error) {
      console.error("Delete service error:", error);

      if (error.code === "ERR_NETWORK") {
        setError("Backend chưa chạy. Không thể xóa dịch vụ.");
      } else {
        setError(error.response?.data?.message || "Không thể xóa dịch vụ.");
      }

      return false;
    }
  };

  return {
    services,

    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadServices,
    createService,
    updateService,
    deleteService,
  };
}
