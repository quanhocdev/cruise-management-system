import { useEffect, useState } from "react";
import passengerRoomTypeService from "../services/passengerRoomTypeService";

export default function usePassengerRoomType(roomTypeId) {
  const [roomType, setRoomType] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!roomTypeId) {
      setRoomType(null);
      setError("");
      return;
    }

    let cancelled = false;
    setLoading(true);
    setError("");

    passengerRoomTypeService
      .getRoomTypeDetail(roomTypeId)
      .then((data) => {
        if (!cancelled) setRoomType(data);
      })
      .catch((err) => {
        if (!cancelled) {
          console.error("🔥 LOAD PASSENGER ROOM TYPE ERROR:", err);
          setRoomType(null);
          setError(
            err.response?.data?.message ||
              "Không thể tải thông tin hạng phòng.",
          );
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [roomTypeId]);

  return { roomType, loading, error };
}
