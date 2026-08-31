// frontend-web/src/modules/shore/components/ActivityVisitTourHistoryDetail.jsx

import {
  X,
  History,
  Clock,
  Users,
  DollarSign,
  MapPin,
  AlertCircle,
  RefreshCw,
} from "lucide-react";

import "../styles/ActivityVisitTourHistoryDetail.css";

const formatDateTime = (value) => {
  if (!value) return "—";

  return new Date(value).toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatPrice = (value) => {
  if (value === null || value === undefined) {
    return "—";
  }

  return `${Number(value).toLocaleString("vi-VN")} đ`;
};

const getStatusLabel = (status) => {
  const labels = {
    WAITING_CONFIG: "Chờ cấu hình",
    CONFIGURED: "Đã cấu hình",
    NOT_STARTED: "Chưa bắt đầu",
    IN_PROGRESS: "Đang diễn ra",
    COMPLETED: "Đã hoàn thành",
    DELAYED: "Trì hoãn",
    CANCELLED: "Đã hủy",
  };

  return labels[status] || status || "—";
};

function ActivityVisitTourHistoryDetail({
  history,
  activities = [],
  loading = false,
  error = null,
  onClose,
}) {
  if (!history) return null;

  return (
    <div className="activity-visit-history-detail__overlay" onClick={onClose}>
      <div
        className="activity-visit-history-detail"
        onClick={(e) => e.stopPropagation()}
      >
        {/* =================================================
            HEADER
            ================================================= */}

        <div className="activity-visit-history-detail__header">
          <div className="activity-visit-history-detail__heading">
            <div className="activity-visit-history-detail__icon">
              <History size={21} />
            </div>

            <div>
              <h2>Chi tiết cấu hình Visit Tour</h2>

              <p>Cấu hình được lưu tại thời điểm hoàn thành Tour.</p>
            </div>
          </div>

          <button
            type="button"
            className="activity-visit-history-detail__close"
            onClick={onClose}
            aria-label="Đóng"
          >
            <X size={20} />
          </button>
        </div>

        {/* =================================================
            SUMMARY
            ================================================= */}

        <div className="activity-visit-history-detail__summary">
          <div className="activity-visit-history-detail__summary-item">
            <span>Tour ID</span>

            <strong>{history.tourId}</strong>
          </div>

          <div className="activity-visit-history-detail__summary-item">
            <span>Số cấu hình</span>

            <strong>{history.totalConfigurations ?? 0}</strong>
          </div>

          <div className="activity-visit-history-detail__summary-item">
            <span>Hoàn thành lúc</span>

            <strong>{formatDateTime(history.completedAt)}</strong>
          </div>
        </div>

        {/* =================================================
            CONTENT
            ================================================= */}

        <div className="activity-visit-history-detail__content">
          <div className="activity-visit-history-detail__section-title">
            <MapPin size={18} />

            <span>Danh sách Visit Tour đã cấu hình</span>
          </div>

          {/* =================================================
              DETAIL LOADING
              ================================================= */}

          {loading && (
            <div className="activity-visit-history-detail__loading">
              <RefreshCw
                size={22}
                className="activity-visit-history-detail__spinner"
              />

              <span>Đang tải chi tiết cấu hình...</span>
            </div>
          )}

          {/* =================================================
              DETAIL ERROR
              ================================================= */}

          {!loading && error && (
            <div className="activity-visit-history-detail__error">
              <AlertCircle size={19} />

              <span>{error}</span>
            </div>
          )}

          {/* =================================================
              EMPTY
              ================================================= */}

          {!loading && !error && activities.length === 0 && (
            <div className="activity-visit-history-detail__empty">
              Không tìm thấy dữ liệu cấu hình của Tour này.
            </div>
          )}

          {/* =================================================
              ACTIVITIES
              ================================================= */}

          {!loading && !error && activities.length > 0 && (
            <div className="activity-visit-history-detail__list">
              {activities.map((activity) => (
                <div
                  key={activity.id}
                  className="activity-visit-history-detail__activity"
                >
                  <div className="activity-visit-history-detail__activity-main">
                    <div className="activity-visit-history-detail__activity-title">
                      {activity.name || "Visit Tour"}
                    </div>

                    {activity.description && <p>{activity.description}</p>}
                  </div>

                  <div className="activity-visit-history-detail__activity-info">
                    <div>
                      <Clock size={15} />

                      <span>
                        {formatDateTime(activity.startTime)}

                        {" → "}

                        {formatDateTime(activity.endTime)}
                      </span>
                    </div>

                    <div>
                      <Users size={15} />

                      <span>{activity.maxPassengers ?? "—"} khách</span>
                    </div>

                    <div>
                      <DollarSign size={15} />

                      <span>{formatPrice(activity.price)}</span>
                    </div>
                  </div>

                  <div
                    className={`activity-visit-history-detail__status ${
                      activity.status ? activity.status.toLowerCase() : ""
                    }`}
                  >
                    {getStatusLabel(activity.status)}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* =================================================
            FOOTER
            ================================================= */}

        <div className="activity-visit-history-detail__footer">
          <button
            type="button"
            className="activity-visit-history-detail__close-button"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}

export default ActivityVisitTourHistoryDetail;
