// src/modules/operation/pages/OperationTourOpenBooking.jsx

import React, { useState, useEffect, useCallback } from "react";
import {
  ArrowLeft,
  AlertCircle,
  CheckCircle2,
  Calendar,
  Clock,
  Edit3,
  PlayCircle,
  Trash2,
} from "lucide-react";
import { useNavigate, useSearchParams } from "react-router-dom";
import useTourBooking from "../hooks/useTourBooking";
import "../styles/OperationTourOpenBooking.css";

const OperationTourOpenBooking = () => {
  const [searchParams] = useSearchParams();
  const tourId = searchParams.get("tourId");
  const navigate = useNavigate();

  const [bookingStart, setBookingStart] = useState("");
  const [bookingEnd, setBookingEnd] = useState("");
  const [currentStatus, setCurrentStatus] = useState("NOT_OPEN");
  const [tourInfo, setTourInfo] = useState({
    startDate: null,
    endDate: null,
    tourName: "",
    code: "",
  });
  const [successMessage, setSuccessMessage] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  const {
    getBookingConfig,
    openBooking,
    updateBooking,
    deleteBooking,
    loading,
    error,
  } = useTourBooking();

  const formatForInput = (dateString) => {
    if (!dateString) return "";
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return "";
    const tzOffset = date.getTimezoneOffset() * 60000;
    const localDate = new Date(date.getTime() - tzOffset);
    return localDate.toISOString().substring(0, 16);
  };

  const formatDateDisplay = (dateString) => {
    if (!dateString) return "---";
    const date = new Date(dateString);
    return isNaN(date.getTime())
      ? dateString
      : date.toLocaleString("vi-VN", {
          day: "2-digit",
          month: "2-digit",
          year: "numeric",
          hour: "2-digit",
          minute: "2-digit",
        });
  };

  const fetchBookingData = useCallback(async () => {
    if (!tourId) return;
    try {
      const data = await getBookingConfig(tourId);
      if (data) {
        setTourInfo({
          startDate: data.startDate,
          endDate: data.endDate,
          tourName: data.name,
          code: data.code,
        });

        if (data.bookingStart) {
          setBookingStart(formatForInput(data.bookingStart));
          setBookingEnd(formatForInput(data.bookingEnd));
          setCurrentStatus(data.statusBooking || "NOT_OPEN");
          setIsEditing(false);
        } else {
          setCurrentStatus("NOT_OPEN");
          setIsEditing(true); // Mở chế độ nhập nếu chưa có cấu hình
        }
      }
    } catch (err) {
      setCurrentStatus("NOT_OPEN");
      setIsEditing(true);
    }
  }, [tourId, getBookingConfig]);

  useEffect(() => {
    fetchBookingData();
  }, [tourId]);

  const handleBack = () => {
    navigate(-1);
  };

  const handleSaveConfig = async () => {
    if (!tourId) return;
    if (!bookingStart || !bookingEnd) {
      alert("Vui lòng chọn đầy đủ thời gian bắt đầu và kết thúc nhận booking.");
      return;
    }

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

      if (currentStatus !== "NOT_OPEN") {
        await updateBooking(tourId, payload);
        setSuccessMessage("Cập nhật thời gian mở bán vé thành công!");
      } else {
        await openBooking(tourId, payload);
        setSuccessMessage("Mở bán vé cho tour thành công!");
      }

      fetchBookingData();
      setIsEditing(false);

      setTimeout(() => {
        setSuccessMessage(null);
      }, 3000);
    } catch (err) {
      // Lỗi do hook xử lý
    }
  };

  const handleDeleteConfig = async () => {
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

      setBookingStart("");
      setBookingEnd("");
      setCurrentStatus("NOT_OPEN");
      setIsEditing(true);

      fetchBookingData();

      setTimeout(() => {
        setSuccessMessage(null);
      }, 3000);
    } catch (err) {
      // Lỗi do hook xử lý
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
          <span>Quản lý Vận hành</span>
          <h1>Cấu Hình Mở Bán Tour</h1>
        </div>
      </div>

      <div
        style={{
          maxWidth: "750px",
          margin: "24px auto",
          background: "#fff",
          padding: "32px",
          borderRadius: "12px",
          boxShadow: "0 4px 6px -1px rgba(0,0,0,0.05)",
          border: "1px solid #e2e8f0",
        }}
      >
        {/* Thông báo */}
        {error && (
          <div
            style={{
              marginBottom: "20px",
              padding: "12px 16px",
              background: "#fee2e2",
              color: "#991b1b",
              borderRadius: "8px",
              display: "flex",
              gap: "10px",
              alignItems: "center",
              fontSize: "14px",
            }}
          >
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        {successMessage && (
          <div
            style={{
              marginBottom: "20px",
              padding: "12px 16px",
              background: "#d1fae5",
              color: "#065f46",
              borderRadius: "8px",
              display: "flex",
              gap: "10px",
              alignItems: "center",
              fontSize: "14px",
            }}
          >
            <CheckCircle2 size={18} />
            <span>{successMessage}</span>
          </div>
        )}

        {/* Header thông tin tổng quan Tour */}
        <div
          style={{
            marginBottom: "24px",
            padding: "16px 20px",
            background: "#f8fafc",
            border: "1px solid #cbd5e1",
            borderRadius: "8px",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            flexWrap: "wrap",
            gap: "12px",
          }}
        >
          <div>
            <div
              style={{ fontSize: "12px", color: "#64748b", fontWeight: 600 }}
            >
              MÃ TOUR: {tourInfo.code || "---"}
            </div>
            <div
              style={{
                fontSize: "16px",
                fontWeight: 700,
                color: "#0f172a",
                marginTop: "2px",
              }}
            >
              {tourInfo.tourName || "Đang tải thông tin Tour..."}
            </div>
            <div
              style={{
                fontSize: "13px",
                color: "#475569",
                marginTop: "6px",
                display: "flex",
                alignItems: "center",
                gap: "6px",
              }}
            >
              <Calendar size={14} />
              <span>
                Thời gian thực tế:{" "}
                <strong>{formatDateDisplay(tourInfo.startDate)}</strong> đến{" "}
                <strong>{formatDateDisplay(tourInfo.endDate)}</strong>
              </span>
            </div>
          </div>

          <div>
            <span
              style={{
                padding: "6px 12px",
                borderRadius: "20px",
                fontSize: "12px",
                fontWeight: 700,
                background:
                  currentStatus === "OPEN"
                    ? "#d1fae5"
                    : currentStatus === "WAITING"
                      ? "#fef3c7"
                      : "#f1f5f9",
                color:
                  currentStatus === "OPEN"
                    ? "#065f46"
                    : currentStatus === "WAITING"
                      ? "#92400e"
                      : "#475569",
                border: `1px solid ${
                  currentStatus === "OPEN"
                    ? "#6ee7b7"
                    : currentStatus === "WAITING"
                      ? "#fde047"
                      : "#cbd5e1"
                }`,
              }}
            >
              {currentStatus === "OPEN"
                ? "Đang mở bán"
                : currentStatus === "WAITING"
                  ? "Chờ mở bán"
                  : "Chưa cấu hình"}
            </span>
          </div>
        </div>

        {/* Khung hiển thị cấu hình trực quan hoặc Form chỉnh sửa */}
        {!isEditing ? (
          <div
            style={{
              background: "#ffffff",
              border: "1px solid #e2e8f0",
              borderRadius: "8px",
              padding: "20px",
              display: "flex",
              flexDirection: "column",
              gap: "16px",
            }}
          >
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <div
                style={{ fontWeight: 600, color: "#1e293b", fontSize: "15px" }}
              >
                Thời gian nhận đăng ký & Mở bán vé
              </div>
              <button
                type="button"
                onClick={() => setIsEditing(true)}
                style={{
                  background: "none",
                  border: "none",
                  color: "#2563eb",
                  fontWeight: 600,
                  fontSize: "14px",
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: "6px",
                }}
              >
                <Edit3 size={16} /> Chỉnh sửa
              </button>
            </div>

            <div
              style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr",
                gap: "16px",
                background: "#f8fafc",
                padding: "16px",
                borderRadius: "6px",
              }}
            >
              <div>
                <div
                  style={{
                    fontSize: "12px",
                    color: "#64748b",
                    marginBottom: "4px",
                  }}
                >
                  BẮT ĐẦU NHẬN BOOKING
                </div>
                <div
                  style={{
                    fontSize: "14px",
                    fontWeight: 600,
                    color: "#0f172a",
                  }}
                >
                  {formatDateDisplay(bookingStart)}
                </div>
              </div>
              <div>
                <div
                  style={{
                    fontSize: "12px",
                    color: "#64748b",
                    marginBottom: "4px",
                  }}
                >
                  ĐÓNG NHẬN BOOKING
                </div>
                <div
                  style={{
                    fontSize: "14px",
                    fontWeight: 600,
                    color: "#0f172a",
                  }}
                >
                  {formatDateDisplay(bookingEnd)}
                </div>
              </div>
            </div>

            <div style={{ display: "flex", gap: "12px", marginTop: "8px" }}>
              <button
                type="button"
                onClick={handleDeleteConfig}
                disabled={loading}
                style={{
                  padding: "10px 16px",
                  background: "#fee2e2",
                  color: "#991b1b",
                  border: "none",
                  borderRadius: "6px",
                  fontWeight: 600,
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: "6px",
                  fontSize: "14px",
                }}
              >
                <Trash2 size={16} /> Hủy cấu hình
              </button>
            </div>
          </div>
        ) : (
          <div
            style={{
              background: "#ffffff",
              border: "1px solid #cbd5e1",
              borderRadius: "8px",
              padding: "20px",
              display: "flex",
              flexDirection: "column",
              gap: "16px",
            }}
          >
            <div
              style={{ fontWeight: 600, color: "#1e293b", fontSize: "15px" }}
            >
              {currentStatus !== "NOT_OPEN"
                ? "Cập nhật thời gian mở bán vé"
                : "Thiết lập thời gian mở bán vé"}
            </div>

            <div
              style={{ display: "flex", flexDirection: "column", gap: "12px" }}
            >
              <div>
                <label
                  style={{
                    display: "block",
                    fontSize: "13px",
                    fontWeight: 500,
                    marginBottom: "6px",
                    color: "#334155",
                  }}
                >
                  Thời gian bắt đầu nhận Booking:
                </label>
                <input
                  type="datetime-local"
                  value={bookingStart}
                  onChange={(e) => setBookingStart(e.target.value)}
                  style={{
                    width: "100%",
                    padding: "10px",
                    borderRadius: "6px",
                    border: "1px solid #cbd5e1",
                    fontSize: "14px",
                  }}
                />
              </div>

              <div>
                <label
                  style={{
                    display: "block",
                    fontSize: "13px",
                    fontWeight: 500,
                    marginBottom: "6px",
                    color: "#334155",
                  }}
                >
                  Thời gian đóng nhận Booking:
                </label>
                <input
                  type="datetime-local"
                  value={bookingEnd}
                  onChange={(e) => setBookingEnd(e.target.value)}
                  style={{
                    width: "100%",
                    padding: "10px",
                    borderRadius: "6px",
                    border: "1px solid #cbd5e1",
                    fontSize: "14px",
                  }}
                />
              </div>
            </div>

            <div style={{ display: "flex", gap: "12px", marginTop: "8px" }}>
              <button
                type="button"
                onClick={handleSaveConfig}
                disabled={loading}
                style={{
                  flex: 1,
                  background: "#2563eb",
                  color: "#fff",
                  padding: "10px 16px",
                  border: "none",
                  borderRadius: "6px",
                  fontWeight: 600,
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: "6px",
                }}
              >
                <PlayCircle size={16} />
                {loading ? "Đang xử lý..." : "Lưu Cấu Hình Mở Bán"}
              </button>

              {currentStatus !== "NOT_OPEN" && (
                <button
                  type="button"
                  onClick={() => setIsEditing(false)}
                  style={{
                    background: "#f1f5f9",
                    color: "#334155",
                    padding: "10px 16px",
                    border: "1px solid #cbd5e1",
                    borderRadius: "6px",
                    fontWeight: 600,
                    cursor: "pointer",
                  }}
                >
                  Hủy bỏ
                </button>
              )}
            </div>
          </div>
        )}

        {/* Vùng chuẩn bị tích hợp bảng quản lý lượng đăng ký sau này */}
        <div
          style={{
            marginTop: "32px",
            borderTop: "2px dashed #e2e8f0",
            paddingTop: "24px",
          }}
        >
          <div
            style={{
              fontWeight: 600,
              fontSize: "16px",
              color: "#0f172a",
              marginBottom: "8px",
            }}
          >
            Quản lý Lượng đăng ký & Vé
          </div>
          <p style={{ fontSize: "14px", color: "#64748b", margin: 0 }}>
            Khu vực hiển thị danh sách khách hàng đăng ký và thống kê vé sẽ được
            bổ sung tại đây.
          </p>
        </div>
      </div>
    </div>
  );
};

export default OperationTourOpenBooking;
