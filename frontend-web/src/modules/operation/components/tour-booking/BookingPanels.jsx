// src/modules/operation/components/tour-booking/BookingPanels.jsx

import {
  AlertCircle,
  Calendar,
  Clock,
  Edit3,
  PlayCircle,
  Trash2,
} from "lucide-react";

const formatDateTime = (dateString) => {
  if (!dateString) return "---";

  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) return dateString;

  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const STATUS_BADGES = {
  OPEN: { label: "Đang mở bán", modifier: "open" },
  WAITING: { label: "Chờ mở bán", modifier: "waiting" },
};

// ---------------------------------------------------------
// Thông tin Tour + trạng thái mở bán
// ---------------------------------------------------------
export function TourSummaryCard({ tourInfo, currentStatus }) {
  const badge = STATUS_BADGES[currentStatus] || {
    label: "Chưa cấu hình",
    modifier: "idle",
  };

  return (
    <section className="otb-summary">
      <div className="otb-summary-main">
        <span className="otb-summary-code">
          MÃ TOUR: {tourInfo.code || "---"}
        </span>

        <h2>{tourInfo.tourName || "Đang tải thông tin Tour..."}</h2>

        <p className="otb-summary-dates">
          <Calendar size={14} />
          <span>
            Thời gian thực tế:{" "}
            <strong>{formatDateTime(tourInfo.startDate)}</strong> đến{" "}
            <strong>{formatDateTime(tourInfo.endDate)}</strong>
          </span>
        </p>
      </div>

      <span className={`otb-badge otb-badge--${badge.modifier}`}>
        {badge.label}
      </span>
    </section>
  );
}

// ---------------------------------------------------------
// Xem cấu hình hiện tại
// ---------------------------------------------------------
export function BookingConfigView({
  bookingStart,
  bookingEnd,
  loading,
  onEdit,
  onDelete,
}) {
  return (
    <section className="otb-panel">
      <div className="otb-panel-head">
        <h3>Thời gian nhận đăng ký &amp; Mở bán vé</h3>

        <button type="button" className="otb-link" onClick={onEdit}>
          <Edit3 size={16} />
          Chỉnh sửa
        </button>
      </div>

      <div className="otb-times">
        <div className="otb-time">
          <span>
            <Clock size={14} />
            Bắt đầu nhận booking
          </span>
          <strong>{formatDateTime(bookingStart)}</strong>
        </div>

        <div className="otb-time">
          <span>
            <Clock size={14} />
            Đóng nhận booking
          </span>
          <strong>{formatDateTime(bookingEnd)}</strong>
        </div>
      </div>

      <div className="otb-actions">
        <button
          type="button"
          className="otb-btn otb-btn--danger"
          onClick={onDelete}
          disabled={loading}
        >
          <Trash2 size={16} />
          Hủy cấu hình
        </button>
      </div>
    </section>
  );
}

// ---------------------------------------------------------
// Form thiết lập / cập nhật
// ---------------------------------------------------------
export function BookingConfigForm({
  isUpdate,
  bookingStart,
  bookingEnd,
  onChangeStart,
  onChangeEnd,
  formError,
  loading,
  onSave,
  onCancel,
}) {
  return (
    <section className="otb-panel otb-panel--edit">
      <div className="otb-panel-head">
        <h3>
          {isUpdate
            ? "Cập nhật thời gian mở bán vé"
            : "Thiết lập thời gian mở bán vé"}
        </h3>
      </div>

      {formError && (
        <p className="otb-field-error" role="alert">
          <AlertCircle size={15} />
          {formError}
        </p>
      )}

      <div className="otb-form">
        <div className="otb-field">
          <label htmlFor="otb-booking-start">Bắt đầu nhận Booking</label>
          <input
            id="otb-booking-start"
            type="datetime-local"
            value={bookingStart}
            onChange={(e) => onChangeStart(e.target.value)}
          />
        </div>

        <div className="otb-field">
          <label htmlFor="otb-booking-end">Đóng nhận Booking</label>
          <input
            id="otb-booking-end"
            type="datetime-local"
            value={bookingEnd}
            min={bookingStart || undefined}
            onChange={(e) => onChangeEnd(e.target.value)}
          />
        </div>
      </div>

      <div className="otb-actions">
        <button
          type="button"
          className="otb-btn otb-btn--primary"
          onClick={onSave}
          disabled={loading}
        >
          <PlayCircle size={16} />
          {loading ? "Đang xử lý..." : "Lưu cấu hình mở bán"}
        </button>

        {isUpdate && (
          <button
            type="button"
            className="otb-btn otb-btn--secondary"
            onClick={onCancel}
            disabled={loading}
          >
            Hủy bỏ
          </button>
        )}
      </div>
    </section>
  );
}
