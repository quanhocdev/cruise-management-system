import { useCallback, useEffect, useState } from "react";
import cruiseService from "../services/cruiseService";

export default function useCruiseAreas(deckId) {
  const [areas, setAreas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadAreas = useCallback(async () => {
    if (!deckId) {
      setAreas([]);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await cruiseService.getAreas(deckId);

      setAreas(response.data);
    } catch (err) {
      setError(
        err.response?.data?.message || "Không thể tải danh sách khu vực.",
      );
    } finally {
      setLoading(false);
    }
  }, [deckId]);

  useEffect(() => {
    loadAreas();
  }, [loadAreas]);

  const createArea = async (formData) => {
    setError("");
    setSuccess("");

    try {
      const response = await cruiseService.createArea(deckId, formData);

      setAreas((previous) =>
        [...previous, response.data].sort((a, b) =>
          a.name.localeCompare(b.name, "vi"),
        ),
      );

      setSuccess("Tạo khu vực thành công.");

      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể tạo khu vực.");

      return null;
    }
  };

  const updateArea = async (areaId, formData) => {
    setError("");
    setSuccess("");

    try {
      const response = await cruiseService.updateArea(deckId, areaId, formData);

      setAreas((previous) =>
        previous
          .map((area) => (area.id === areaId ? response.data : area))
          .sort((a, b) => a.name.localeCompare(b.name, "vi")),
      );

      setSuccess("Cập nhật khu vực thành công.");

      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể cập nhật khu vực.");

      return null;
    }
  };

  const deactivateArea = async (areaId) => {
    setError("");
    setSuccess("");

    try {
      const response = await cruiseService.deactivateArea(deckId, areaId);

      setAreas((previous) =>
        previous.map((area) => (area.id === areaId ? response.data : area)),
      );

      setSuccess("Đã vô hiệu hóa khu vực.");

      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể vô hiệu hóa khu vực.");

      return null;
    }
  };

  const deleteArea = async (areaId) => {
    setError("");
    setSuccess("");

    try {
      await cruiseService.deleteArea(deckId, areaId);

      setAreas((previous) => previous.filter((area) => area.id !== areaId));

      setSuccess("Xóa khu vực thành công.");

      return true;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể xóa khu vực.");

      return false;
    }
  };

  return {
    areas,
    loading,
    error,
    success,

    setError,
    setSuccess,

    loadAreas,
    createArea,
    updateArea,
    deactivateArea,
    deleteArea,
  };
}
