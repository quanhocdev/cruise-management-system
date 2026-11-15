// src/modules/passenger/hooks/useBookedTours.js
// Danh sách tour đã đặt (booking CONFIRMED) kèm statusTrip.
import { useCallback, useEffect, useState } from "react";
import bookingService from "../services/bookingService";

// Đổi tên field của API sang shape mà TourCard đang dùng
const toCardShape = (t) => ({
  id: t.tourId,
  code: t.tourCode,
  name: t.tourName,
  startDate: t.departureDate,
  endDate: t.endDate,
  statusTrip: t.statusTrip,
});

export default function useBookedTours() {
  const [tours, setTours] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadTours = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await bookingService.getMyBookedTours();
      const list = Array.isArray(data) ? data : data?.content || [];
      setTours(list.map(toCardShape));
    } catch (err) {
      setError(
        err.response?.data?.message || "Không thể tải danh sách hành trình.",
      );
      setTours([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTours();
  }, [loadTours]);

  return { tours, loading, error, loadTours };
}
