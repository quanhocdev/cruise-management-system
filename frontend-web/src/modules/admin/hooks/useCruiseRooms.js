// src/modules/admin/hooks/useCruises.js
import { useCallback, useEffect, useState } from "react";
import cruiseService from "../services/cruiseService";

export default function useCruiseRooms(deckId) {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadRooms = useCallback(async () => {
    if (!deckId) {
      setRooms([]);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await cruiseService.getRooms(deckId);

      setRooms(response.data);
    } catch (err) {
      setError(err.response?.data?.message || "Không thể tải danh sách phòng.");
    } finally {
      setLoading(false);
    }
  }, [deckId]);

  useEffect(() => {
    loadRooms();
  }, [loadRooms]);

  const createRoom = async (data) => {
    setError("");
    setSuccess("");

    try {
      const response = await cruiseService.createRoom(deckId, data);

      setRooms((previous) =>
        [...previous, response.data].sort((a, b) =>
          a.code.localeCompare(b.code, undefined, {
            numeric: true,
          }),
        ),
      );

      setSuccess("Tạo phòng thành công.");

      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể tạo phòng.");

      return null;
    }
  };

  const updateRoom = async (roomId, data) => {
    setError("");
    setSuccess("");

    try {
      const response = await cruiseService.updateRoom(deckId, roomId, data);

      setRooms((previous) =>
        previous
          .map((room) => (room.id === roomId ? response.data : room))
          .sort((a, b) =>
            a.code.localeCompare(b.code, undefined, {
              numeric: true,
            }),
          ),
      );

      setSuccess("Cập nhật phòng thành công.");

      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể cập nhật phòng.");

      return null;
    }
  };

  const deleteRoom = async (roomId) => {
    setError("");
    setSuccess("");

    try {
      await cruiseService.deleteRoom(deckId, roomId);

      setRooms((previous) => previous.filter((room) => room.id !== roomId));

      setSuccess("Xóa phòng thành công.");

      return true;
    } catch (err) {
      setError(err.response?.data?.message || "Không thể xóa phòng.");

      return false;
    }
  };

  return {
    rooms,
    loading,
    error,
    success,

    setError,
    setSuccess,

    loadRooms,
    createRoom,
    updateRoom,
    deleteRoom,
  };
}
