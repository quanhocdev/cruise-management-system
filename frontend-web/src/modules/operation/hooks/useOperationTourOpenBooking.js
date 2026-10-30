// src/modules/operation/hooks/useOperationTourOpenBooking.js
import { useCallback, useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

import useTourBooking from "./useTourBooking";

// "2026-10-10T08:00:00" (UTC/ISO) -> "2026-10-10T08:00" theo giờ local cho <input datetime-local>
const toInputValue = (dateString) => {
  if (!dateString) return "";

  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) return "";

  const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return local.toISOString().substring(0, 16);
};

// "2026-10-10T08:00" -> "2026-10-10T08:00:00"
const toApiValue = (value) => (value.length === 16 ? `${value}:00` : value);

const useOperationTourOpenBooking = () => {
  const [searchParams] = useSearchParams();
  const tourId = searchParams.get("tourId");

  const {
    getBookingConfig,
    openBooking,
    updateBooking,
    deleteBooking,
    loading,
    error,
  } = useTourBooking();

  const [tourInfo, setTourInfo] = useState({
    startDate: null,
    endDate: null,
    tourName: "",
    code: "",
  });
  const [bookingStart, setBookingStart] = useState("");
  const [bookingEnd, setBookingEnd] = useState("");
  const [currentStatus, setCurrentStatus] = useState("NOT_OPEN");
  const [isEditing, setIsEditing] = useState(false);
  const [formError, setFormError] = useState("");
  const [successMessage, setSuccessMessage] = useState(null);

  const isUpdate = currentStatus !== "NOT_OPEN";

  // ---------- LOAD ----------
  const fetchBookingData = useCallback(async () => {
    if (!tourId) return;

    try {
      const data = await getBookingConfig(tourId);
      if (!data) return;

      setTourInfo({
        startDate: data.startDate,
        endDate: data.endDate,
        tourName: data.name,
        code: data.code,
      });

      if (data.bookingStart) {
        setBookingStart(toInputValue(data.bookingStart));
        setBookingEnd(toInputValue(data.bookingEnd));
        setCurrentStatus(data.statusBooking || "NOT_OPEN");
        setIsEditing(false);
      } else {
        setBookingStart("");
        setBookingEnd("");
        setCurrentStatus("NOT_OPEN");
        setIsEditing(true); // chưa có cấu hình -> mở sẵn form nhập
      }
    } catch {
      setCurrentStatus("NOT_OPEN");
      setIsEditing(true);
    }
    // getBookingConfig không ổn định giữa các lần render -> bỏ khỏi deps để tránh lặp vô hạn
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tourId]);

  useEffect(() => {
    fetchBookingData();
  }, [fetchBookingData]);

  // Tự ẩn thông báo thành công sau 3 giây
  useEffect(() => {
    if (!successMessage) return;

    const timer = setTimeout(() => setSuccessMessage(null), 3000);
    return () => clearTimeout(timer);
  }, [successMessage]);

  // ---------- ACTIONS ----------
  const handleSave = async () => {
    if (!tourId) return;

    if (!bookingStart || !bookingEnd) {
      setFormError(
        "Vui lòng chọn đầy đủ thời gian bắt đầu và kết thúc nhận booking.",
      );
      return;
    }

    if (new Date(bookingEnd) <= new Date(bookingStart)) {
      setFormError(
        "Thời gian đóng booking phải diễn ra sau thời gian mở booking.",
      );
      return;
    }

    setFormError("");
    setSuccessMessage(null);

    const payload = {
      bookingStart: toApiValue(bookingStart),
      bookingEnd: toApiValue(bookingEnd),
    };

    try {
      if (isUpdate) {
        await updateBooking(tourId, payload);
        setSuccessMessage("Cập nhật thời gian mở bán vé thành công!");
      } else {
        await openBooking(tourId, payload);
        setSuccessMessage("Mở bán vé cho tour thành công!");
      }

      await fetchBookingData();
    } catch {
      // Lỗi đã được useTourBooking đưa vào `error`
    }
  };

  const handleDelete = async () => {
    const confirmed = window.confirm(
      "Bạn có chắc chắn muốn hủy cấu hình mở bán của tour này không?",
    );
    if (!confirmed) return;

    setSuccessMessage(null);

    try {
      await deleteBooking(tourId);
      setSuccessMessage("Đã hủy cấu hình mở bán vé thành công!");
      await fetchBookingData();
    } catch {
      // Lỗi đã được useTourBooking đưa vào `error`
    }
  };

  const startEditing = () => {
    setFormError("");
    setIsEditing(true);
  };

  // Hủy sửa: tải lại để bỏ các giá trị chưa lưu
  const cancelEditing = () => {
    setFormError("");
    fetchBookingData();
  };

  return {
    tourId,
    tourInfo,
    currentStatus,
    isUpdate,
    bookingStart,
    bookingEnd,
    setBookingStart,
    setBookingEnd,
    isEditing,
    startEditing,
    cancelEditing,
    handleSave,
    handleDelete,
    loading,
    error,
    formError,
    successMessage,
  };
};

export default useOperationTourOpenBooking;
