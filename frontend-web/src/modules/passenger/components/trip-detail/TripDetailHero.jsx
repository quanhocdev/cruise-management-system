// src/modules/passenger/components/trip-detail/TripDetailHero.jsx
import React from "react";
import { ArrowLeft, RefreshCw, Ship, CalendarDays } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "./TripDetailHero.css";

const formatDate = (value) => {
  if (!value) return "Chưa cập nhật";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return "Chưa cập nhật";

  return date.toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

const STATUS_LABEL = {
  DRAFT: "Bản nháp",
  APPROVAL_PENDING: "Chờ phê duyệt",
  APPROVED: "Đã phê duyệt",
  READY: "Sẵn sàng",
  IN_PROGRESS: "Đang diễn ra",
  COMPLETED: "Đã hoàn thành",
  CANCELLED: "Đã hủy",
};

export default function TripDetailHero({ booking, cruise, onReload }) {
  const navigate = useNavigate();

  const status = booking?.status;
  const tourName =
    booking?.tourName ||
    booking?.tour?.name ||
    cruise?.tourName ||
    "Chi tiết chuyến đi";

  const cruiseName =
    cruise?.cruiseName ||
    cruise?.name ||
    cruise?.cruise?.name ||
    "Thông tin tàu đang được cập nhật";

  const startDate =
    booking?.startDate || booking?.tourStartDate || booking?.tour?.startDate;

  const endDate =
    booking?.endDate || booking?.tourEndDate || booking?.tour?.endDate;

  return (
    <section className="trip-detail-hero">
      <div className="trip-detail-hero__inner">
        <div className="trip-detail-hero__top">
          <button
            type="button"
            className="trip-detail-hero__back"
            onClick={() => navigate(-1)}
          >
            <ArrowLeft size={18} />
            <span>Quay lại</span>
          </button>

          <button
            type="button"
            className="trip-detail-hero__reload"
            onClick={onReload}
            title="Tải lại thông tin"
          >
            <RefreshCw size={17} />
            <span>Làm mới</span>
          </button>
        </div>

        <div className="trip-detail-hero__content">
          <div className="trip-detail-hero__title">
            <span className="trip-detail-hero__eyebrow">
              HÀNH TRÌNH CỦA BẠN
            </span>

            <h1>{tourName}</h1>

            {status && (
              <span
                className={`trip-detail-hero__status trip-detail-hero__status--${String(
                  status,
                ).toLowerCase()}`}
              >
                {STATUS_LABEL[status] || status}
              </span>
            )}
          </div>

          <div className="trip-detail-hero__meta">
            <div className="trip-detail-hero__meta-item">
              <Ship size={19} />
              <div>
                <span>Tàu du lịch</span>
                <strong>{cruiseName}</strong>
              </div>
            </div>

            <div className="trip-detail-hero__meta-item">
              <CalendarDays size={19} />
              <div>
                <span>Thời gian chuyến đi</span>
                <strong>
                  {formatDate(startDate)} – {formatDate(endDate)}
                </strong>
              </div>
            </div>

            {booking?.bookingCode && (
              <div className="trip-detail-hero__meta-item">
                <div>
                  <span>Mã đặt chỗ</span>
                  <strong>{booking.bookingCode}</strong>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </section>
  );
}
