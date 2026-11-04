// src/modules/admin/hooks/useCruises.js
import { useCallback, useEffect, useState } from "react";
import cruiseService from "../services/cruiseService";

const logError = (label, err) =>
  console.error(
    `🔥 ${label}:`,
    err.response?.status,
    err.response?.data || err,
  );

export default function useCruises() {
  const [cruises, setCruises] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadCruises = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      setCruises(await cruiseService.getAllCruises());
    } catch (err) {
      logError("LOAD CRUISES ERROR", err);
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

  const createCruise = async (formData) => {
    setError("");
    setSuccess("");

    try {
      const data = await cruiseService.createCruise(formData);

      setCruises((previous) => [...previous, data]);
      setSuccess("Tạo du thuyền thành công.");

      return data;
    } catch (err) {
      logError("CREATE CRUISE ERROR", err);
      setError(err.response?.data?.message || "Không thể tạo du thuyền.");

      return null;
    }
  };

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
      logError("UPDATE CRUISE ERROR", err);
      setError(err.response?.data?.message || "Không thể cập nhật du thuyền.");

      return null;
    }
  };

  const deleteCruise = async (id) => {
    setError("");
    setSuccess("");

    try {
      await cruiseService.deleteCruise(id);

      setCruises((previous) => previous.filter((cruise) => cruise.id !== id));
      setSuccess("Xóa du thuyền thành công.");

      return true;
    } catch (err) {
      logError("DELETE CRUISE ERROR", err);
      setError(err.response?.data?.message || "Không thể xóa du thuyền.");

      return false;
    }
  };

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
