// src/modules/admin/hooks/useServices.js

import { useCallback, useEffect, useState } from "react";
import serviceService from "../services/serviceService";

const useServices = (activeOnly = false) => {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // LOAD SERVICES
  // =====================================================

  const loadServices = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await serviceService.getServices(activeOnly);

      setServices(data);

      return data;
    } catch (err) {
      console.error("🔥 LOAD SERVICES ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể tải danh sách dịch vụ.";

      setError(message);

      throw err;
    } finally {
      setLoading(false);
    }
  }, [activeOnly]);

  // =====================================================
  // CREATE SERVICE
  // =====================================================

  const createService = useCallback(async (formData) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const data = await serviceService.createService(formData);

      setServices((prev) =>
        [...prev, data].sort((a, b) => a.name.localeCompare(b.name)),
      );

      setSuccess("Tạo dịch vụ thành công.");

      return data;
    } catch (err) {
      console.error("🔥 CREATE SERVICE ERROR:", err);

      const message = err?.response?.data?.message || "Không thể tạo dịch vụ.";

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // UPDATE SERVICE
  // =====================================================

  const updateService = useCallback(async (serviceId, formData) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const data = await serviceService.updateService(serviceId, formData);

      setServices((prev) =>
        prev
          .map((service) => (service.id === serviceId ? data : service))
          .sort((a, b) => a.name.localeCompare(b.name)),
      );

      setSuccess("Cập nhật dịch vụ thành công.");

      return data;
    } catch (err) {
      console.error("🔥 UPDATE SERVICE ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể cập nhật dịch vụ.";

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // DELETE SERVICE
  // =====================================================

  const deleteService = useCallback(async (serviceId) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await serviceService.deleteService(serviceId);

      setServices((prev) => prev.filter((service) => service.id !== serviceId));

      setSuccess("Xóa dịch vụ thành công.");

      return true;
    } catch (err) {
      console.error("🔥 DELETE SERVICE ERROR:", err);

      const message = err?.response?.data?.message || "Không thể xóa dịch vụ.";

      setError(message);

      return false;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadServices();
  }, [loadServices]);

  // =====================================================
  // RETURN
  // =====================================================

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
};

export default useServices;
