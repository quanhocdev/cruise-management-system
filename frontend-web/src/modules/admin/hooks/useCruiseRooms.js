import { useCallback, useEffect, useState } from "react";

import cruiseRoomService from "../services/cruiseRoomService";
import roomTypeService from "../services/roomTypeService";

export default function useCruiseRooms(deckId) {
  const [rooms, setRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // LOAD ROOMS
  // =====================================================

  const loadRooms = useCallback(async () => {
    if (!deckId) {
      setRooms([]);
      setLoading(false);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const data = await cruiseRoomService.getRooms(deckId);

      setRooms(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("🔥 LOAD ROOMS ERROR:", err);
      console.error("🔥 RESPONSE:", err?.response);
      console.error("🔥 RESPONSE DATA:", err?.response?.data);
      console.error("🔥 STATUS:", err?.response?.status);

      setError(
        err?.response?.data?.message || "Không thể tải danh sách phòng.",
      );
    } finally {
      setLoading(false);
    }
  }, [deckId]);

  // =====================================================
  // LOAD ROOM TYPES
  // =====================================================

  const loadRoomTypes = useCallback(async () => {
    try {
      const data = await roomTypeService.getAllRoomTypes();

      setRoomTypes(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("🔥 LOAD ROOM TYPES ERROR:", err);
      console.error("🔥 RESPONSE:", err?.response);
      console.error("🔥 RESPONSE DATA:", err?.response?.data);
      console.error("🔥 STATUS:", err?.response?.status);

      setError(
        err?.response?.data?.message || "Không thể tải danh sách loại phòng.",
      );
    }
  }, []);

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadRooms();
    loadRoomTypes();
  }, [loadRooms, loadRoomTypes]);

  // =====================================================
  // CREATE ROOM
  // =====================================================

  const createRoom = async (data) => {
    setError("");
    setSuccess("");

    try {
      const createdRoom = await cruiseRoomService.createRoom(deckId, data);

      setRooms((previous) => [
        ...previous,
        ...(Array.isArray(createdRoom) ? createdRoom : [createdRoom]),
      ]);

      const quantity = Array.isArray(createdRoom)
        ? createdRoom.length
        : data?.quantity || 1;

      setSuccess(`Đã tạo ${quantity} phòng thành công.`);

      return createdRoom;
    } catch (err) {
      console.error("🔥 CREATE ROOM ERROR:", err);
      console.error("🔥 RESPONSE:", err?.response);
      console.error("🔥 RESPONSE DATA:", err?.response?.data);
      console.error("🔥 STATUS:", err?.response?.status);

      setError(err?.response?.data?.message || "Không thể tạo phòng.");

      return null;
    }
  };

  // =====================================================
  // UPDATE ROOM
  // =====================================================

  const updateRoom = async (roomId, data) => {
    setError("");
    setSuccess("");

    try {
      const updatedRoom = await cruiseRoomService.updateRoom(
        deckId,
        roomId,
        data,
      );

      setRooms((previous) =>
        previous.map((room) => (room.id === roomId ? updatedRoom : room)),
      );

      setSuccess("Đã cập nhật phòng thành công.");

      return updatedRoom;
    } catch (err) {
      console.error("🔥 UPDATE ROOM ERROR:", err);
      console.error("🔥 RESPONSE:", err?.response);
      console.error("🔥 RESPONSE DATA:", err?.response?.data);
      console.error("🔥 STATUS:", err?.response?.status);

      setError(err?.response?.data?.message || "Không thể cập nhật phòng.");

      return null;
    }
  };

  // =====================================================
  // DELETE ROOM
  // =====================================================

  const deleteRoom = async (roomId) => {
    setError("");
    setSuccess("");

    try {
      await cruiseRoomService.deleteRoom(deckId, roomId);

      setRooms((previous) => previous.filter((room) => room.id !== roomId));

      setSuccess("Đã xóa phòng thành công.");

      return true;
    } catch (err) {
      console.error("🔥 DELETE ROOM ERROR:", err);
      console.error("🔥 RESPONSE:", err?.response);
      console.error("🔥 RESPONSE DATA:", err?.response?.data);
      console.error("🔥 STATUS:", err?.response?.status);

      setError(err?.response?.data?.message || "Không thể xóa phòng.");

      return false;
    }
  };

  return {
    rooms,
    roomTypes,
    loading,
    error,
    success,

    setError,
    setSuccess,

    loadRooms,
    loadRoomTypes,
    createRoom,
    updateRoom,
    deleteRoom,
  };
}
