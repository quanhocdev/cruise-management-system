import { useCallback, useEffect, useState } from "react";

import roomTypeService from "../services/roomTypeService";

export default function useRoomTypes() {
  const [roomTypes, setRoomTypes] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // LOAD
  // =====================================================

  const loadRoomTypes = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await roomTypeService.getAllRoomTypes();

      setRoomTypes(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("🔥 LOAD ROOM TYPES ERROR:", err);

      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(
        err.response?.data?.message || "Không thể tải danh sách loại phòng.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // CREATE
  // =====================================================

  const createRoomType = async (data) => {
    setError("");
    setSuccess("");

    try {
      const response = await roomTypeService.createRoomType(data);

      setRoomTypes((previous) =>
        [...previous, response].sort((a, b) => a.name.localeCompare(b.name)),
      );

      setSuccess("Thêm loại phòng thành công.");

      return response;
    } catch (err) {
      console.error("🔥 CREATE ROOM TYPE ERROR:", err);

      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể thêm loại phòng.");

      return null;
    }
  };

  // =====================================================
  // UPDATE
  // =====================================================

  const updateRoomType = async (id, data) => {
    setError("");
    setSuccess("");

    try {
      const response = await roomTypeService.updateRoomType(id, data);

      setRoomTypes((previous) =>
        previous
          .map((roomType) => (roomType.id === id ? response : roomType))
          .sort((a, b) => a.name.localeCompare(b.name)),
      );

      setSuccess("Cập nhật loại phòng thành công.");

      return response;
    } catch (err) {
      console.error("🔥 UPDATE ROOM TYPE ERROR:", err);

      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể cập nhật loại phòng.");

      return null;
    }
  };

  // =====================================================
  // DELETE
  // =====================================================

  const deleteRoomType = async (id) => {
    setError("");
    setSuccess("");

    try {
      await roomTypeService.deleteRoomType(id);

      setRoomTypes((previous) =>
        previous.filter((roomType) => roomType.id !== id),
      );

      setSuccess("Xóa loại phòng thành công.");

      return true;
    } catch (err) {
      console.error("🔥 DELETE ROOM TYPE ERROR:", err);

      console.error("🔥 RESPONSE:", err.response);
      console.error("🔥 RESPONSE DATA:", err.response?.data);
      console.error("🔥 STATUS:", err.response?.status);

      setError(err.response?.data?.message || "Không thể xóa loại phòng.");

      return false;
    }
  };

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadRoomTypes();
  }, [loadRoomTypes]);

  return {
    roomTypes,
    loading,
    error,
    success,

    setError,
    setSuccess,

    loadRoomTypes,
    createRoomType,
    updateRoomType,
    deleteRoomType,
  };
}
