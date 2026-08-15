// src/modules/admin/hooks/usePorts.js

import { useCallback, useEffect, useState } from "react";

import portService from "../services/portService";

export default function usePorts() {
  const [ports, setPorts] = useState([]);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // LOAD PORTS
  // =====================================================

  const loadPorts = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await portService.getPorts(false);

      setPorts(Array.isArray(data) ? data : []);

      return Array.isArray(data) ? data : [];
    } catch (err) {
      console.error("🔥 LOAD PORTS ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể tải danh sách cảng.";

      setError(message);

      return [];
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadPorts();
  }, [loadPorts]);

  // =====================================================
  // CREATE
  // =====================================================

  const createPort = useCallback(async (data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const createdPort = await portService.createPort(data);

      setPorts((prev) => {
        const exists = prev.some((port) => port.id === createdPort.id);

        if (exists) {
          return prev;
        }

        return [...prev, createdPort].sort((a, b) =>
          (a.name || "").localeCompare(b.name || "", "vi"),
        );
      });

      setSuccess("Tạo cảng thành công.");

      return createdPort;
    } catch (err) {
      console.error("🔥 CREATE PORT ERROR:", err);

      const message = err?.response?.data?.message || "Không thể tạo cảng.";

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // UPDATE
  // =====================================================

  const updatePort = useCallback(async (id, data) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const updatedPort = await portService.updatePort(id, data);

      setPorts((prev) =>
        prev
          .map((port) => (port.id === id ? updatedPort : port))
          .sort((a, b) => (a.name || "").localeCompare(b.name || "", "vi")),
      );

      setSuccess("Cập nhật cảng thành công.");

      return updatedPort;
    } catch (err) {
      console.error("🔥 UPDATE PORT ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể cập nhật cảng.";

      setError(message);

      return null;
    } finally {
      setSaving(false);
    }
  }, []);

  // =====================================================
  // DEACTIVATE
  // =====================================================

  const deactivatePort = useCallback(async (id) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await portService.deactivatePort(id);

      setPorts((prev) =>
        prev.map((port) =>
          port.id === id
            ? {
                ...port,
                status: "INACTIVE",
              }
            : port,
        ),
      );

      setSuccess("Đã vô hiệu hóa cảng.");

      return true;
    } catch (err) {
      console.error("🔥 DEACTIVATE PORT ERROR:", err);

      const message =
        err?.response?.data?.message || "Không thể vô hiệu hóa cảng.";

      setError(message);

      return false;
    } finally {
      setSaving(false);
    }
  }, []);

  return {
    ports,

    loading,
    saving,

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
