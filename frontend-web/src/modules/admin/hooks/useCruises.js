// src/modules/admin/hooks/useCruises.js
import { useCallback, useEffect, useState } from "react";
import cruiseService from "../services/cruiseService";

export default function useCruises() {
  const [cruises, setCruises] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // LOAD CRUISES
  // =====================================================

  const loadCruises = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await cruiseService.getAllCruises();

      setCruises(data);
    } catch (err) {
      console.error("🔥 LOAD CRUISES ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(
        err.response?.data?.message || "Không thể tải danh sách du thuyền.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadCruises();
  }, [loadCruises]);

  // =====================================================
  // CREATE CRUISE
  // =====================================================

  const createCruise = async (formData) => {
    setError("");
    setSuccess("");

    try {
      const data = await cruiseService.createCruise(formData);

      setCruises((previous) => [...previous, data]);

      setSuccess("Tạo du thuyền thành công.");

      return data;
    } catch (err) {
      console.error("🔥 CREATE CRUISE ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);
      console.error("🔥 REQUEST:", err.request);

      setError(err.response?.data?.message || "Không thể tạo du thuyền.");

      return null;
    }
  };

  // =====================================================
  // UPDATE CRUISE
  // =====================================================

  const updateCruise = async (id, formData) => {
    setError("");
    setSuccess("");

    try {
      const data = await cruiseService.updateCruise(id, formData);

      setCruises((previous) =>
        previous.map((cruise) => (cruise.id === id ? data : cruise)),
      );

      setSuccess("Cập nhật du thuyền thành công.");

      return data;
    } catch (err) {
      console.error("🔥 UPDATE CRUISE ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể cập nhật du thuyền.");

      return null;
    }
  };

  // =====================================================
  // DELETE CRUISE
  // =====================================================

  const deleteCruise = async (id) => {
    setError("");
    setSuccess("");

    try {
      await cruiseService.deleteCruise(id);

      setCruises((previous) => previous.filter((cruise) => cruise.id !== id));

      setSuccess("Xóa du thuyền thành công.");

      return true;
    } catch (err) {
      console.error("🔥 DELETE CRUISE ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể xóa du thuyền.");

      return false;
    }
  };

  // =====================================================
  // RETURN
  // =====================================================

  return {
    cruises,
    loading,
    error,
    success,

    setError,
    setSuccess,

    loadCruises,
    createCruise,
    updateCruise,
    deleteCruise,
  };
}
