import { useCallback, useEffect, useState } from "react";
import cruiseService from "../services/cruiseService";

export default function useCruises() {
  const [cruises, setCruises] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadCruises = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await cruiseService.getAll();

      setCruises(response.data);
    } catch (err) {
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
      const response = await cruiseService.create(formData);

      setCruises((previous) => [...previous, response.data]);

      setSuccess("Tạo du thuyền thành công.");

      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể tạo du thuyền.");

      return null;
    }
  };

  const updateCruise = async (id, formData) => {
    setError("");
    setSuccess("");

    try {
      const response = await cruiseService.update(id, formData);

      setCruises((previous) =>
        previous.map((cruise) => (cruise.id === id ? response.data : cruise)),
      );

      setSuccess("Cập nhật du thuyền thành công.");

      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể cập nhật du thuyền.");

      return null;
    }
  };

  const deleteCruise = async (id) => {
    setError("");
    setSuccess("");

    try {
      await cruiseService.remove(id);

      setCruises((previous) => previous.filter((cruise) => cruise.id !== id));

      setSuccess("Xóa du thuyền thành công.");

      return true;
    } catch (err) {
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
