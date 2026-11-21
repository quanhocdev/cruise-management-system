// src/modules/passenger/hooks/useBookedTours.js
import { useCallback, useEffect, useState } from "react";
import bookingService from "../services/bookingService";

// Chuẩn hóa dữ liệu API sang shape mà TourCard sử dụng
const toCardShape = (t) => ({
  ...t,
  id: t.tourId,
  bookingId: t.bookingId,
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
