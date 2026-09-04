// src/modules/onboard/components/activity-cruise-tour/ActivityCruiseTourHistoryDetail.jsx

import {
  X,
  CalendarDays,
  Activity,
  Clock,
  Users,
  DollarSign,
} from "lucide-react";

import "../../styles/activity-cruise-tour/ActivityCruiseTourHistoryDetail.css";

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
  if (value === null || value === undefined) return "—";

  return Number(value).toLocaleString("vi-VN") + " đ";
};

const ActivityCruiseTourHistoryDetail = ({
  history,
  activities = [],
  loading = false,
  error = null,
  onClose,
}) => {
  if (!history) return null;

  // ĐANG TẢI CHI TIẾT
  if (loading) {
    return (
      <div
        className="activity-cruise-history-detail__overlay"
        onClick={onClose}
      >
        <div
          className="activity-cruise-history-detail"
          onClick={(e) => e.stopPropagation()}
        >
          <div className="activity-cruise-history-detail__loading">
            Đang tải chi tiết cấu hình...
          </div>
        </div>
      </div>
    );
  }
  if (error) {
    return (
      <div
        className="activity-cruise-history-detail__overlay"
        onClick={onClose}
      >
        <div
          className="activity-cruise-history-detail"
          onClick={(e) => e.stopPropagation()}
        >
          <div className="activity-cruise-history-detail__error">{error}</div>

          <button
            type="button"
            className="activity-cruise-history-detail__close-button"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
      </div>
    );
  }
  const tourActivities = activities.filter(
    (item) => item.tourId === history.tourId,
  );

  return (
    <div className="activity-cruise-history-detail__overlay" onClick={onClose}>
      <div
        className="activity-cruise-history-detail"
        onClick={(e) => e.stopPropagation()}
      >
        {/* HEADER */}
        <div className="activity-cruise-history-detail__header">
          <div className="activity-cruise-history-detail__heading">
            <div className="activity-cruise-history-detail__icon">
              <Activity size={21} />
            </div>

            <div>
              <h2>Chi tiết cấu hình Tour</h2>
              <p>Các hoạt động đã được cấu hình tại thời điểm hoàn thành</p>
            </div>
          </div>

          <button
            type="button"
            className="activity-cruise-history-detail__close"
            onClick={onClose}
            aria-label="Đóng"
          >
            <X size={20} />
          </button>
        </div>

        {/* TOUR SUMMARY */}
        <div className="activity-cruise-history-detail__summary">
          <div className="activity-cruise-history-detail__summary-item">
            <span>Tour ID</span>
            <strong>{history.tourId}</strong>
          </div>

          <div className="activity-cruise-history-detail__summary-item">
            <span>Số cấu hình</span>
            <strong>{history.totalConfigurations}</strong>
          </div>

          <div className="activity-cruise-history-detail__summary-item">
            <span>Hoàn thành lúc</span>
            <strong>{formatDateTime(history.completedAt)}</strong>
          </div>
        </div>

        {/* ACTIVITIES */}
        <div className="activity-cruise-history-detail__content">
          <div className="activity-cruise-history-detail__section-title">
            <Activity size={18} />
            <span>Danh sách hoạt động đã cấu hình</span>
          </div>

          {tourActivities.length === 0 ? (
            <div className="activity-cruise-history-detail__empty">
              Không tìm thấy dữ liệu cấu hình của Tour này.
            </div>
          ) : (
            <div className="activity-cruise-history-detail__list">
              {tourActivities.map((activity) => (
                <div
                  key={activity.id}
                  className="activity-cruise-history-detail__activity"
                >
                  <div className="activity-cruise-history-detail__activity-main">
                    <div className="activity-cruise-history-detail__activity-title">
                      {activity.activityCruiseName || "Hoạt động"}
                    </div>

                    {activity.activityCruiseDescription && (
                      <p>{activity.activityCruiseDescription}</p>
                    )}
                  </div>

                  <div className="activity-cruise-history-detail__activity-info">
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

                  <div className="activity-cruise-history-detail__status">
                    {activity.status}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* FOOTER */}
        <div className="activity-cruise-history-detail__footer">
          <button
            type="button"
            className="activity-cruise-history-detail__close-button"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};

export default ActivityCruiseTourHistoryDetail;
