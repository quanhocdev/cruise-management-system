// src/modules/shore/components/ShoreTourDetailModal.jsx
import { Clock, MapPin, Ship, Tag, X, Calendar, Compass } from "lucide-react";
import "../styles/ShoreTourDetailModal.css";

const ShoreTourDetailModal = ({ visitTour, masterTour, onClose }) => {
  if (!visitTour) return null;

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

          {/* SCHEDULE STOP */}
          <section className="shore-tour-detail-section">
            <h4>
              <MapPin size={15} />
              Điểm dừng
            </h4>

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
              Thời gian & Giá
            </h4>

            <div className="shore-tour-detail-row">
              <span className="shore-tour-detail-label">
                Thời gian hoạt động
              </span>
              <span className="shore-tour-detail-value">
                {visitTour.startTime && visitTour.endTime
                  ? `${visitTour.startTime} → ${visitTour.endTime}`
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
