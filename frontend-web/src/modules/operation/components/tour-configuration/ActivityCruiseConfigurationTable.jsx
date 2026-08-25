// src/modules/operation/components/tour-configuration/ActivityCruiseConfigurationTable.jsx

import React from "react";
import { Activity, CheckCircle2, Clock3, MapPin, XCircle } from "lucide-react";

import { formatVND } from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/ActivityConfigurationTable.css";

const ActivityCruiseConfigurationTable = ({ activities = [] }) => {
  const safeActivities = Array.isArray(activities) ? activities : [];

  return (
    <section className="activity-configuration-table-section">
      {/* =========================================================
          HEADER
          ========================================================= */}
      <div className="activity-configuration-table-header">
        <div className="activity-configuration-table-title">
          <div className="activity-configuration-table-icon">
            <Activity size={20} />
          </div>

          <div>
            <h2>Hoạt động trên tàu</h2>

            <p>Các hoạt động trên tàu đã được cấu hình cho Tour.</p>
          </div>
        </div>

        <span className="activity-configuration-table-count">
          {safeActivities.length} hoạt động
        </span>
      </div>

      {/* =========================================================
          EMPTY
          ========================================================= */}
      {safeActivities.length === 0 ? (
        <div className="activity-configuration-table-empty">
          <XCircle size={24} />

          <span>Tour chưa có hoạt động trên tàu được cấu hình.</span>
        </div>
      ) : (
        /* =======================================================
           TABLE
           ======================================================= */
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
              {safeActivities.map((item, index) => (
                <tr key={item.id || `activity-cruise-${index}`}>
                  {/* =================================================
                      ACTIVITY
                      ================================================= */}
                  <td>
                    <div className="activity-configuration-name">
                      <strong>
                        {item.activityCruiseName ||
                          item.activityName ||
                          "Chưa xác định"}
                      </strong>

                      {item.activityCruiseId && (
                        <span>{item.activityCruiseId}</span>
                      )}
                    </div>
                  </td>

                  {/* =================================================
                      CRUISE AREA
                      ================================================= */}
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

                  {/* =================================================
                      TIME
                      ================================================= */}
                  <td>
                    <div className="activity-configuration-time">
                      <Clock3 size={15} />

                      <div>
                        <span>{item.startTime || "—"}</span>

                        <span>{item.endTime || "—"}</span>
                      </div>
                    </div>
                  </td>

                  {/* =================================================
                      MAX PASSENGERS
                      ================================================= */}
                  <td>
                    {item.maxPassengers != null ? item.maxPassengers : "—"}
                  </td>

                  {/* =================================================
                      PRICE
                      ================================================= */}
                  <td>{formatVND(item.price)}</td>

                  {/* =================================================
                      STATUS
                      ================================================= */}
                  <td>
                    <span
                      className="
                        activity-configuration-status
                        configured
                      "
                    >
                      <CheckCircle2 size={14} />
                      Đã cấu hình
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
};

export default ActivityCruiseConfigurationTable;
