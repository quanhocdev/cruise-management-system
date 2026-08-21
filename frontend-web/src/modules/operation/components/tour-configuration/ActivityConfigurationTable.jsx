// src/modules/operation/components/tour-configuration/ActivityConfigurationTable.jsx

import React from "react";
import { Activity, CheckCircle2, Clock3, MapPin, XCircle } from "lucide-react";
import {
  isActivityConfigured,
  formatVND,
} from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/ActivityConfigurationTable.css";

const ActivityConfigurationTable = ({ activities = [] }) => {
  const safeActivities = activities || [];

  const getStatusLabel = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "Chờ cấu hình";
      case "NOT_STARTED":
        return "Đã cấu hình";
      case "IN_PROGRESS":
        return "Đang hoạt động";
      case "COMPLETED":
        return "Đã kết thúc";
      default:
        return status || "Không xác định";
    }
  };

  return (
    <section className="activity-configuration-table-section">
      <div className="activity-configuration-table-header">
        <div className="activity-configuration-table-title">
          <div className="activity-configuration-table-icon">
            <Activity size={20} />
          </div>

          <div>
            <h2>Hoạt động</h2>
            <p>Các hoạt động được Operation phân công cho tour.</p>
          </div>
        </div>

        <span className="activity-configuration-table-count">
          {safeActivities.length} phân công
        </span>
      </div>

      {safeActivities.length === 0 ? (
        <div className="activity-configuration-table-empty">
          <XCircle size={24} />
          <span>Tour chưa được phân công hoạt động.</span>
        </div>
      ) : (
        <div className="activity-configuration-table-wrapper">
          <table className="activity-configuration-table">
            <thead>
              <tr>
                <th>Hoạt động</th>
                <th>Khu vực</th>
                <th>Thời gian</th>
                <th>Khách tối đa</th>
                <th>Giá</th>
                <th>Trạng thái</th>
              </tr>
            </thead>

            <tbody>
              {safeActivities.map((item, index) => {
                const configured = isActivityConfigured(item);

                return (
                  <tr key={item.id || `activity-${index}`}>
                    <td>
                      <div className="activity-configuration-name">
                        <strong>
                          {item.activityCruiseName || "Chưa chọn hoạt động"}
                        </strong>

                        {item.activityCruiseId && (
                          <span>{item.activityCruiseId}</span>
                        )}
                      </div>
                    </td>

                    <td>
                      <div className="activity-configuration-area">
                        <MapPin size={15} />

                        <div>
                          <strong>{item.cruiseAreaName || "—"}</strong>

                          {item.deckNumber != null && (
                            <span>Tầng {item.deckNumber}</span>
                          )}
                        </div>
                      </div>
                    </td>

                    <td>
                      <div className="activity-configuration-time">
                        <Clock3 size={15} />

                        <div>
                          <span>{item.startTime || "—"}</span>
                          <span>{item.endTime || "—"}</span>
                        </div>
                      </div>
                    </td>

                    <td>
                      {item.maxPassengers != null ? item.maxPassengers : "—"}
                    </td>

                    <td>{formatVND(item.price)}</td>

                    <td>
                      <span
                        className={`activity-configuration-status ${
                          configured ? "configured" : "waiting"
                        }`}
                      >
                        {configured ? (
                          <CheckCircle2 size={14} />
                        ) : (
                          <XCircle size={14} />
                        )}

                        {configured
                          ? "Đã cấu hình"
                          : getStatusLabel(item.status)}
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
};

export default ActivityConfigurationTable;
