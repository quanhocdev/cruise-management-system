// src/modules/onboard/components/activity-cruise-tour/ActivityCruiseTourDetail.jsx
import React from "react";
import { CalendarDays, Clock3, MapPin, Ship, Users, X } from "lucide-react";

import "../../styles/activity-cruise-tour/ActivityCruiseTourDetail.css";

const ActivityCruiseTourDetail = ({ activity, onClose }) => {
  if (!activity) {
    return null;
  }

  const formatDateTime = (value) => {
    if (!value) {
      return "—";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleString("vi-VN", {
      dateStyle: "short",
      timeStyle: "short",
    });
  };

  return (
    <div
      className="activity-cruise-tour-detail-overlay"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) {
          onClose?.();
        }
      }}
    >
      <div className="activity-cruise-tour-detail-modal">
        {/* HEADER */}
        <div className="activity-cruise-tour-detail-header">
          <div>
            <span className="activity-cruise-tour-detail-eyebrow">
              Chi tiết phân công
            </span>

            <h2>{activity.tourCode || "Tour"}</h2>

            <p>{activity.tourName || "—"}</p>
          </div>

          <button
            type="button"
            className="activity-cruise-tour-detail-close"
            onClick={onClose}
            aria-label="Đóng"
          >
            <X size={20} />
          </button>
        </div>

        {/* CONTENT */}
        <div className="activity-cruise-tour-detail-content">
          <div className="activity-cruise-tour-detail-section">
            <h3>Thông tin tour</h3>

            <div className="activity-cruise-tour-detail-grid">
              <div className="activity-cruise-tour-detail-item">
                <span className="detail-label">
                  <Ship size={16} />
                  Mã tour
                </span>

                <strong>{activity.tourCode || "—"}</strong>
              </div>

              <div className="activity-cruise-tour-detail-item">
                <span className="detail-label">
                  <CalendarDays size={16} />
                  Tên tour
                </span>

                <strong>{activity.tourName || "—"}</strong>
              </div>

              <div className="activity-cruise-tour-detail-item">
                <span className="detail-label">
                  <MapPin size={16} />
                  Khu vực
                </span>

                <strong>{activity.cruiseAreaName || "—"}</strong>
              </div>

              <div className="activity-cruise-tour-detail-item">
                <span className="detail-label">Trạng thái</span>

                <span className="activity-cruise-tour-detail-status">
                  {activity.status === "WAITING_CONFIG"
                    ? "Chờ cấu hình"
                    : activity.status || "—"}
                </span>
              </div>
            </div>
          </div>

          {/* Nếu sau này GET detail trả thêm configuration */}
          {(activity.activityCruiseName ||
            activity.startTime ||
            activity.endTime ||
            activity.maxPassengers !== undefined ||
            activity.price !== undefined) && (
            <div className="activity-cruise-tour-detail-section">
              <h3>Cấu hình hoạt động</h3>

              <div className="activity-cruise-tour-detail-grid">
                <div className="activity-cruise-tour-detail-item">
                  <span className="detail-label">Hoạt động</span>

                  <strong>{activity.activityCruiseName || "—"}</strong>
                </div>

                <div className="activity-cruise-tour-detail-item">
                  <span className="detail-label">
                    <Clock3 size={16} />
                    Bắt đầu
                  </span>

                  <strong>{formatDateTime(activity.startTime)}</strong>
                </div>

                <div className="activity-cruise-tour-detail-item">
                  <span className="detail-label">
                    <Clock3 size={16} />
                    Kết thúc
                  </span>

                  <strong>{formatDateTime(activity.endTime)}</strong>
                </div>

                <div className="activity-cruise-tour-detail-item">
                  <span className="detail-label">
                    <Users size={16} />
                    Số hành khách
                  </span>

                  <strong>{activity.maxPassengers ?? "—"}</strong>
                </div>

                <div className="activity-cruise-tour-detail-item">
                  <span className="detail-label">Giá</span>

                  <strong>
                    {activity.price != null
                      ? `${Number(activity.price).toLocaleString("vi-VN")} ₫`
                      : "—"}
                  </strong>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* FOOTER */}
        <div className="activity-cruise-tour-detail-footer">
          <button
            type="button"
            className="activity-cruise-tour-detail-footer-button"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};

export default ActivityCruiseTourDetail;
