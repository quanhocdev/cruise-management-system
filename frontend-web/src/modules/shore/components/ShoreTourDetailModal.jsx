// src/modules/shore/components/ShoreTourDetailModal.jsx
import { Clock, MapPin, Ship, Tag, X, Calendar, Compass } from "lucide-react";
import "../styles/ShoreTourDetailModal.css";

// Hàm format ngày giờ hiển thị gọn gàng, dễ đọc
function formatDisplayDateTime(dateTimeStr) {
  if (!dateTimeStr) return "—";
  try {
    const [datePart, timePart] = dateTimeStr.split("T");
    if (!datePart || !timePart) return dateTimeStr;
    const [year, month, day] = datePart.split("-");
    const timeOnly = timePart.substring(0, 5);
    return `${day}/${month}/${year} lúc ${timeOnly}`;
  } catch (e) {
    return dateTimeStr;
  }
}

const ShoreTourDetailModal = ({ visitTour, masterTour, onClose }) => {
  if (!visitTour) return null;

  // Trích xuất thông tin portName, arriveAt, leaveAt từ masterTour dựa trên scheduleStopId của visitTour
  let targetStop = null;
  let targetScheduleDate = "";

  if (masterTour && masterTour.schedules) {
    for (const schedule of masterTour.schedules) {
      if (schedule.stops) {
        const found = schedule.stops.find(
          (stop) =>
            stop.id === visitTour.scheduleStopId ||
            stop.scheduleStopId === visitTour.scheduleStopId,
        );
        if (found) {
          targetStop = found;
          targetScheduleDate = schedule.scheduleDate || "";
          break;
        }
      }
    }
  }

  const getStatusLabel = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "Chờ cấu hình";
      case "CONFIGURED":
        return "Đã cấu hình";
      default:
        return status || "Không xác định";
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "waiting";
      case "CONFIGURED":
        return "configured";
      default:
        return "";
    }
  };

  const handleOverlayClick = () => {
    onClose();
  };

  const stopPropagation = (event) => {
    event.stopPropagation();
  };

  return (
    <div className="shore-tour-detail-overlay" onClick={handleOverlayClick}>
      <div className="shore-tour-detail-modal" onClick={stopPropagation}>
        {/* HEADER */}
        <div className="shore-tour-detail-header">
          <h3>
            <Ship size={18} />
            Chi tiết Visit Tour
          </h3>
          <button
            type="button"
            className="shore-tour-detail-close"
            onClick={onClose}
            title="Đóng"
          >
            <X size={18} />
          </button>
        </div>

        {/* BODY */}
        <div className="shore-tour-detail-body">
          {/* TOUR MASTER INFO */}
          <section className="shore-tour-detail-section">
            <h4>
              <Compass size={15} />
              Thông tin Master Tour
            </h4>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">Mã Tour (Code)</span>
              <span className="shore-tour-detail-value">
                {masterTour?.code || "—"}
              </span>
            </div>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">Tên Tour</span>
              <span className="shore-tour-detail-value font-medium">
                {masterTour?.name || "—"}
              </span>
            </div>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">Thời gian Tour</span>
              <span className="shore-tour-detail-value">
                {masterTour?.startDate && masterTour?.endDate
                  ? `${masterTour.startDate} → ${masterTour.endDate}`
                  : "—"}
              </span>
            </div>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">Trạng thái chuyến</span>
              <span className="shore-tour-detail-value">
                {masterTour?.statusTrip || "—"}
              </span>
            </div>
          </section>

          {/* VISIT TOUR CONFIGURATION */}
          <section className="shore-tour-detail-section">
            <h4>
              <Ship size={15} />
              Cấu hình Visit Tour
            </h4>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">Tên Visit Tour</span>
              <span className="shore-tour-detail-value">
                {visitTour.name || "Chưa cấu hình"}
              </span>
            </div>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">Mô tả</span>
              <span className="shore-tour-detail-value">
                {visitTour.description || "—"}
              </span>
            </div>
          </section>

          {/* SCHEDULE STOP & PORT INFO */}
          <section className="shore-tour-detail-section">
            <h4>
              <MapPin size={15} />
              Thông tin Điểm dừng & Cập cảng
            </h4>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">
                Tên cảng / Điểm dừng
              </span>
              <span className="shore-tour-detail-value font-medium">
                {targetStop?.portName || visitTour.portName || "—"}
              </span>
            </div>

            {targetScheduleDate && (
              <div className="shore-tour-detail-row">
                <span className="shore-tour-detail-label">Ngày lịch trình</span>
                <span className="shore-tour-detail-value">
                  {targetScheduleDate}
                </span>
              </div>
            )}

            {(targetStop?.arriveAt || targetStop?.leaveAt) && (
              <div className="shore-tour-detail-row">
                <span className="shore-tour-detail-label">
                  Khung giờ cập cảng
                </span>
                <span className="shore-tour-detail-value">
                  {formatDisplayDateTime(targetStop?.arriveAt)} →{" "}
                  {formatDisplayDateTime(targetStop?.leaveAt)}
                </span>
              </div>
            )}

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">Schedule Stop ID</span>
              <span className="shore-tour-detail-value font-mono">
                {visitTour.scheduleStopId || "—"}
              </span>
            </div>
          </section>

          {/* SCHEDULE & PRICE */}
          <section className="shore-tour-detail-section">
            <h4>
              <Clock size={15} />
              Thời gian & Giá thực tế
            </h4>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">
                Thời gian hoạt động
              </span>
              <span className="shore-tour-detail-value">
                {visitTour.startTime && visitTour.endTime
                  ? `${formatDisplayDateTime(visitTour.startTime)} → ${formatDisplayDateTime(visitTour.endTime)}`
                  : "—"}
              </span>
            </div>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">Số khách tối đa</span>
              <span className="shore-tour-detail-value">
                {visitTour.maxPassengers != null
                  ? visitTour.maxPassengers
                  : "—"}
              </span>
            </div>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">Giá</span>
              <span className="shore-tour-detail-value">
                {visitTour.price != null
                  ? `${Number(visitTour.price).toLocaleString("vi-VN")} ₫`
                  : "—"}
              </span>
            </div>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">
                <Tag size={13} style={{ marginRight: 4 }} />
                Trạng thái cấu hình
              </span>
              <span
                className={`shore-tour-detail-status ${getStatusClass(
                  visitTour.status,
                )}`}
              >
                {getStatusLabel(visitTour.status)}
              </span>
            </div>
          </section>
        </div>

        {/* FOOTER */}
        <div className="shore-tour-detail-footer">
          <button
            type="button"
            className="shore-tour-detail-close-btn"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};

export default ShoreTourDetailModal;
