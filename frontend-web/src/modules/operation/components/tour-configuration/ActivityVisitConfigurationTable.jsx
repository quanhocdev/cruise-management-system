// src/modules/operation/components/tour-configuration/ActivityVisitConfigurationTable.jsx

import React from "react";
import { Activity, CheckCircle2, Clock3, MapPin, XCircle } from "lucide-react";

import { formatVND } from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/ActivityVisitConfigurationTable.css";

const ActivityVisitConfigurationTable = ({ activities = [] }) => {
  const safeActivities = Array.isArray(activities) ? activities : [];

  return (
    <section className="activity-visit-configuration-table-section">
      {/* =========================================================
          HEADER
          ========================================================= */}
      <div className="activity-visit-configuration-table-header">
        <div className="activity-visit-configuration-table-title">
          <div className="activity-visit-configuration-table-icon">
            <Activity size={20} />
          </div>

          <div>
            <h2>Hoạt động trên bờ</h2>

            <p>Các hoạt động tham quan trên bờ đã được cấu hình cho Tour.</p>
          </div>
        </div>

        <span className="activity-visit-configuration-table-count">
          {safeActivities.length} hoạt động
        </span>
      </div>

      {/* =========================================================
          EMPTY
          ========================================================= */}
      {safeActivities.length === 0 ? (
        <div className="activity-visit-configuration-table-empty">
          <XCircle size={24} />

          <span>Tour chưa có hoạt động trên bờ được cấu hình.</span>
        </div>
      ) : (
        /* =======================================================
           TABLE
           ======================================================= */
        <div className="activity-visit-configuration-table-wrapper">
          <table className="activity-visit-configuration-table">
            <thead>
              <tr>
                <th>Hoạt động</th>
                <th>Điểm tham quan</th>
                <th>Thời gian</th>
                <th>Khách tối đa</th>
                <th>Giá</th>
                <th>Trạng thái</th>
              </tr>
            </thead>

            <tbody>
              {safeActivities.map((item, index) => (
                <tr key={item.id || `activity-visit-${index}`}>
                  {/* =================================================
                      ACTIVITY
                      ================================================= */}
                  <td>
                    <div className="activity-visit-configuration-name">
                      <strong>
                        {item.activityVisitName ||
                          item.activityName ||
                          "Chưa xác định"}
                      </strong>

                      {item.activityVisitId && (
                        <span>{item.activityVisitId}</span>
                      )}
                    </div>
                  </td>

                  {/* =================================================
                      VISIT LOCATION
                      ================================================= */}
                  <td>
                    <div className="activity-visit-configuration-area">
                      <MapPin size={15} />

                      <div>
                        <strong>
                          {item.locationName ||
                            item.visitLocationName ||
                            item.cruiseAreaName ||
                            "—"}
                        </strong>

                        {item.locationAddress && (
                          <span>{item.locationAddress}</span>
                        )}
                      </div>
                    </div>
                  </td>

                  {/* =================================================
                      TIME
                      ================================================= */}
                  <td>
                    <div className="activity-visit-configuration-time">
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
                    <span className="activity-visit-configuration-status configured">
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

export default ActivityVisitConfigurationTable;
