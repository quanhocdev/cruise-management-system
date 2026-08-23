// src/modules/admin/hooks/useCruises.js
import { useCallback, useEffect, useState } from "react";
import cruiseAreaService from "../services/cruiseAreaService";

export default function useCruiseAreas(deckId) {
  const [areas, setAreas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // LOAD AREAS
  // =====================================================

  const loadAreas = useCallback(async () => {
    if (!deckId) {
      setAreas([]);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const data = await cruiseAreaService.getAreas(deckId);

      setAreas(data);
    } catch (err) {
      console.error("🔥 LOAD AREAS ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

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

  // =====================================================
  // CREATE AREA
  // =====================================================

  const createArea = async (formData) => {
    setError("");
    setSuccess("");

    try {
      const createdArea = await cruiseAreaService.createArea(deckId, formData);

      setAreas((previous) =>
        [...previous, createdArea].sort((a, b) =>
          a.name.localeCompare(b.name, "vi"),
        ),
      );

      setSuccess("Tạo khu vực thành công.");

      return createdArea;
    } catch (err) {
      console.error("🔥 CREATE AREA ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể tạo khu vực.");

      return null;
    }
  };

  // =====================================================
  // UPDATE AREA
  // =====================================================

  const updateArea = async (areaId, formData) => {
    setError("");
    setSuccess("");

    try {
      const updatedArea = await cruiseAreaService.updateArea(
        deckId,
        areaId,
        formData,
      );

      setAreas((previous) =>
        previous
          .map((area) => (area.id === areaId ? updatedArea : area))
          .sort((a, b) => a.name.localeCompare(b.name, "vi")),
      );

      setSuccess("Cập nhật khu vực thành công.");

      return updatedArea;
    } catch (err) {
      console.error("🔥 UPDATE AREA ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể cập nhật khu vực.");

      return null;
    }
  };

  // =====================================================
  // DEACTIVATE AREA
  // =====================================================

  const deactivateArea = async (areaId) => {
    setError("");
    setSuccess("");

    try {
      const updatedArea = await cruiseAreaService.deactivateArea(
        deckId,
        areaId,
      );

      setAreas((previous) =>
        previous
          .map((area) => (area.id === areaId ? updatedArea : area))
          .sort((a, b) => a.name.localeCompare(b.name, "vi")),
      );

      setSuccess("Đã vô hiệu hóa khu vực.");

      return updatedArea;
    } catch (err) {
      console.error("🔥 DEACTIVATE AREA ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể vô hiệu hóa khu vực.");

      return null;
    }
  };

  // =====================================================
  // DELETE AREA
  // =====================================================

  const deleteArea = async (areaId) => {
    setError("");
    setSuccess("");

    try {
      await cruiseAreaService.deleteArea(deckId, areaId);

      setAreas((previous) => previous.filter((area) => area.id !== areaId));

      setSuccess("Xóa khu vực thành công.");

      return true;
    } catch (err) {
      console.error("🔥 DELETE AREA ERROR:", err);
      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể xóa khu vực.");

      return false;
    }
  };

  // =====================================================
  // RETURN
  // =====================================================

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
