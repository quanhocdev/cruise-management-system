// src/modules/operation/pages/OperationTourOpenBooking.jsx

import React, { useState, useEffect, useCallback } from "react";
import { ArrowLeft, AlertCircle, CheckCircle2, Trash2 } from "lucide-react";
import { useNavigate, useSearchParams } from "react-router-dom";
import useTourBooking from "../hooks/useTourBooking";
import "../styles/OperationTourConfiguration.css";

const OperationTourOpenBooking = () => {
  const [searchParams] = useSearchParams();
  const tourId = searchParams.get("tourId");
  const navigate = useNavigate();

  const [bookingStart, setBookingStart] = useState("");
  const [bookingEnd, setBookingEnd] = useState("");
  const [currentStatus, setCurrentStatus] = useState("NOT_OPEN");
  const [successMessage, setSuccessMessage] = useState(null);

  const {
    getBookingConfig,
    openBooking,
    updateBooking,
    deleteBooking,
    loading,
    error,
  } = useTourBooking();

  // Format LocalDateTime sang chuẩn input datetime-local (YYYY-MM-DDTHH:mm)
  const formatForInput = (dateString) => {
    if (!dateString) return "";
    return dateString.substring(0, 16);
  };

  // Tải thông tin cấu hình cũ nếu có
  const fetchBookingData = useCallback(async () => {
    if (!tourId) return;
    try {
      const data = await getBookingConfig(tourId);
      if (data) {
        setBookingStart(formatForInput(data.bookingStart));
        setBookingEnd(formatForInput(data.bookingEnd));
        setCurrentStatus(data.statusBooking || "NOT_OPEN");
      }
    } catch (err) {
      // Tour chưa cấu hình thì giữ trống form
    }
  }, [tourId, getBookingConfig]);

  useEffect(() => {
    fetchBookingData();
  }, [fetchBookingData]);

  const handleBack = () => {
    navigate(-1);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!tourId) return;

    if (new Date(bookingEnd) <= new Date(bookingStart)) {
      alert("Thời gian đóng booking phải diễn ra sau thời gian mở booking.");
      return;
    }

    try {
      setSuccessMessage(null);
      const payload = {
        bookingStart: new Date(bookingStart).toISOString(),
        bookingEnd: new Date(bookingEnd).toISOString(),
      };

      // Nếu đã có trạng thái khác NOT_OPEN thì dùng PATCH (update), ngược lại dùng POST (open mới)
      if (currentStatus !== "NOT_OPEN") {
        await updateBooking(tourId, payload);
        setSuccessMessage("Cập nhật thời gian mở bán vé thành công!");
      } else {
        await openBooking(tourId, payload);
        setSuccessMessage("Mở bán vé cho tour thành công!");
      }

      setTimeout(() => {
        navigate(`/operation/tours/configuration?tourId=${tourId}`);
      }, 1500);
    } catch (err) {
      // Lỗi đã được hook xử lý và lưu vào biến error
    }
  };

  const handleDelete = async () => {
    if (
      !window.confirm(
        "Bạn có chắc chắn muốn hủy cấu hình mở bán của tour này không?",
      )
    )
      return;

    try {
      setSuccessMessage(null);
      await deleteBooking(tourId);
      setSuccessMessage("Đã hủy cấu hình mở bán vé thành công!");

      setTimeout(() => {
        navigate(`/operation/tours/configuration?tourId=${tourId}`);
      }, 1500);
    } catch (err) {
      // Lỗi được hook bắt
    }
  };

  return (
    <div className="operation-tour-configuration-page">
      <div className="operation-tour-configuration-header">
        <button
          type="button"
          className="operation-tour-configuration-back"
          onClick={handleBack}
        >
          <ArrowLeft size={18} />
          Quay lại
        </button>
        <div className="operation-tour-configuration-title">
          <span>Cấu hình nghiệp vụ</span>
          <h1>
            {currentStatus !== "NOT_OPEN"
              ? "Quản lý / Sửa cấu hình Mở Bán"
              : "Mở Bán Vé Tour"}
          </h1>
        </div>
      </div>

      <div
        style={{
          maxWidth: "600px",
          margin: "24px auto",
          background: "#fff",
          padding: "24px",
          borderRadius: "8px",
          boxShadow: "0 1px 3px rgba(0,0,0,0.1)",
        }}
      >
        {error && (
          <div
            style={{
              marginBottom: "16px",
              padding: "12px",
              background: "#fee2e2",
              color: "#991b1b",
              borderRadius: "6px",
              display: "flex",
              gap: "8px",
              alignItems: "center",
            }}
          >
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        {successMessage && (
          <div
            style={{
              marginBottom: "16px",
              padding: "12px",
              background: "#d1fae5",
              color: "#065f46",
              borderRadius: "6px",
              display: "flex",
              gap: "8px",
              alignItems: "center",
            }}
          >
            <CheckCircle2 size={18} />
            <span>{successMessage}</span>
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: "20px" }}
        >
          <div>
            <label
              style={{ display: "block", fontWeight: 500, marginBottom: "8px" }}
            >
              Mã Tour (ID):
            </label>
            <input
              type="text"
              value={tourId || ""}
              disabled
              style={{
                width: "100%",
                padding: "10px",
                borderRadius: "6px",
                border: "1px solid #d1d5db",
                background: "#f3f4f6",
                color: "#6b7280",
              }}
            />
          </div>

          <div>
            <label
              style={{ display: "block", fontWeight: 500, marginBottom: "8px" }}
            >
              Trạng thái Booking hiện tại:
            </label>
            <span
              style={{
                display: "inline-block",
                padding: "4px 10px",
                borderRadius: "4px",
                fontSize: "12px",
                fontWeight: 600,
                background:
                  currentStatus === "OPEN"
                    ? "#d1fae5"
                    : currentStatus === "WAITING"
                      ? "#fef3c7"
                      : "#f3f4f6",
                color:
                  currentStatus === "OPEN"
                    ? "#065f46"
                    : currentStatus === "WAITING"
                      ? "#92400e"
                      : "#374151",
              }}
            >
              {currentStatus}
            </span>
          </div>

          <div>
            <label
              style={{ display: "block", fontWeight: 500, marginBottom: "8px" }}
            >
              Thời gian bắt đầu nhận Booking:
            </label>
            <input
              type="datetime-local"
              value={bookingStart}
              onChange={(e) => setBookingStart(e.target.value)}
              required
              style={{
                width: "100%",
                padding: "10px",
                borderRadius: "6px",
                border: "1px solid #d1d5db",
              }}
            />
          </div>

          <div>
            <label
              style={{ display: "block", fontWeight: 500, marginBottom: "8px" }}
            >
              Thời gian đóng nhận Booking:
            </label>
            <input
              type="datetime-local"
              value={bookingEnd}
              onChange={(e) => setBookingEnd(e.target.value)}
              required
              style={{
                width: "100%",
                padding: "10px",
                borderRadius: "6px",
                border: "1px solid #d1d5db",
              }}
            />
          </div>

          <div style={{ display: "flex", gap: "12px", marginTop: "10px" }}>
            <button
              type="submit"
              disabled={loading}
              style={{
                flex: 1,
                backgroundColor:
                  currentStatus !== "NOT_OPEN" ? "#0d9488" : "#2563eb",
                color: "#fff",
                padding: "12px",
                borderRadius: "6px",
                border: "none",
                fontWeight: 600,
                cursor: loading ? "not-allowed" : "pointer",
                opacity: loading ? 0.7 : 1,
              }}
            >
              {loading
                ? "Đang xử lý..."
                : currentStatus !== "NOT_OPEN"
                  ? "Lưu thay đổi"
                  : "Xác nhận Mở Bán Vé"}
            </button>

            {/* Chỉ hiển thị nút hủy cấu hình nếu đang ở trạng thái NOT_OPEN hoặc WAITING */}
            {(currentStatus === "NOT_OPEN" || currentStatus === "WAITING") &&
              bookingStart && (
                <button
                  type="button"
                  onClick={handleDelete}
                  disabled={loading}
                  style={{
                    backgroundColor: "#fee2e2",
                    color: "#991b1b",
                    padding: "12px 16px",
                    borderRadius: "6px",
                    border: "none",
                    fontWeight: 600,
                    cursor: "pointer",
                    display: "flex",
                    alignItems: "center",
                    gap: "6px",
                  }}
                  title="Hủy cấu hình booking"
                >
                  <Trash2 size={18} />
                  Hủy
                </button>
              )}
          </div>
        </form>
      </div>
    </div>
  );
};

export default OperationTourOpenBooking;
