// src/modules/operation/components/tour-configuration/ActivityVisitConfigurationTable.jsx

import React from "react";
import {
  Activity,
  CheckCircle2,
  Clock3,
  XCircle,
  FileText,
} from "lucide-react";

import {
  formatVND,
  getTourStatusMeta,
  isTourItemConfigured,
} from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/ActivityVisitConfigurationTable.css";

function formatDisplayDateTime(dateTimeStr) {
  if (!dateTimeStr) return "";
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

const ActivityVisitConfigurationTable = ({ activities = [] }) => {
  const safeActivities = Array.isArray(activities) ? activities : [];

  const configuredCount = safeActivities.filter((item) =>
    isTourItemConfigured(item.status),
  ).length;

  return (
    <section className="activity-visit-configuration-table-section">
      <div className="activity-visit-configuration-table-header">
        <div className="activity-visit-configuration-table-title">
          <div className="activity-visit-configuration-table-icon">
            <Activity size={20} />
          </div>

          <div>
            <h2>Hoạt động trên bờ</h2>
            <p>Các hoạt động tham quan trên bờ được phân công cho Tour.</p>
          </div>
        </div>

        <span className="activity-visit-configuration-table-count">
          {configuredCount}/{safeActivities.length} đã cấu hình
        </span>
      </div>

      {safeActivities.length === 0 ? (
        <div className="activity-visit-configuration-table-empty">
          <XCircle size={24} />
          <span>Tour chưa có hoạt động trên bờ được phân công.</span>
        </div>
      ) : (
        <div className="activity-visit-configuration-table-wrapper">
          <table className="activity-visit-configuration-table">
            <thead>
              <tr>
                <th>Hoạt động</th>
                <th>Mô tả</th>
                <th>Thời gian</th>
                <th>Khách tối đa</th>
                <th>Giá</th>
                <th>Trạng thái</th>
              </tr>
            </thead>

            <tbody>
              {safeActivities.map((item, index) => {
                const statusMeta = getTourStatusMeta(item.status);
                const configured = isTourItemConfigured(item.status);

                const displayName =
                  item.visitName ||
                  item.name ||
                  item.activityVisitName ||
                  item.activityName ||
                  "Chưa cấu hình";

                const displayDescription =
                  item.visitDescription || item.description || "—";

                const startTimeFormatted = formatDisplayDateTime(
                  item.startTime,
                );
                const endTimeFormatted = formatDisplayDateTime(item.endTime);

                return (
                  <tr
                    key={
                      item.id ||
                      item.activityVisitId ||
                      `activity-visit-${index}`
                    }
                  >
                    {/* HOẠT ĐỘNG */}
                    <td>
                      <div className="activity-visit-configuration-name">
                        <strong>{displayName}</strong>
                      </div>
                    </td>

                    {/* MÔ TẢ */}
                    <td>
                      <div
                        className="activity-visit-configuration-desc"
                        style={{
                          color:
                            displayDescription === "—" ? "#94a3b8" : "#475569",
                          maxWidth: "250px",
                          fontSize: "0.85rem",
                          lineHeight: "1.4",
                        }}
                      >
                        {displayDescription}
                      </div>
                    </td>

                    {/* THỜI GIAN */}
                    <td>
                      <div className="activity-visit-configuration-time">
                        <Clock3 size={15} />
                        <div>
                          <span>
                            {startTimeFormatted || item.startTime || "—"}
                          </span>
                          <span>{endTimeFormatted || item.endTime || "—"}</span>
                        </div>
                      </div>
                    </td>

                    {/* KHÁCH TỐI ĐA */}
                    <td>
                      {item.maxPassengers != null ? item.maxPassengers : "—"}
                    </td>

                    {/* GIÁ */}
                    <td>{formatVND(item.price)}</td>

                    {/* TRẠNG THÁI */}
                    <td>
                      <span
                        className={`activity-visit-configuration-status ${statusMeta.className}`}
                      >
                        {configured ? (
                          <CheckCircle2 size={14} />
                        ) : (
                          <XCircle size={14} />
                        )}
                        {statusMeta.label}
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

export default ActivityVisitConfigurationTable;
