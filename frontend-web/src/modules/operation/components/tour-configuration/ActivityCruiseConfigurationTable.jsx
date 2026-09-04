// src/modules/operation/components/tour-configuration/ActivityCruiseConfigurationTable.jsx

import React from "react";
import { Activity, CheckCircle2, Clock3, MapPin, XCircle } from "lucide-react";

import {
  formatVND,
  getTourStatusMeta,
  isTourItemConfigured,
} from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/ActivityConfigurationTable.css";

const ActivityCruiseConfigurationTable = ({ activities = [] }) => {
  const safeActivities = Array.isArray(activities) ? activities : [];

  // =========================================================
  // SỐ LƯỢNG THỰC SỰ ĐÃ CẤU HÌNH
  // status !== WAITING_CONFIG
  // =========================================================

  const configuredCount = safeActivities.filter((item) =>
    isTourItemConfigured(item.status),
  ).length;

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

            <p>Các hoạt động trên tàu được phân công cho Tour.</p>
          </div>
        </div>

        {/* =========================================================
            CONFIGURED COUNT / TOTAL ASSIGNMENTS
            ========================================================= */}

        <span className="activity-configuration-table-count">
          {configuredCount}/{safeActivities.length} đã cấu hình
        </span>
      </div>

      {/* =========================================================
          EMPTY
          ========================================================= */}

      {safeActivities.length === 0 ? (
        <div className="activity-configuration-table-empty">
          <XCircle size={24} />

          <span>Tour chưa có hoạt động trên tàu được phân công.</span>
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
              {safeActivities.map((item, index) => {
                // =========================================================
                // STATUS
                // =========================================================

                console.log("Cruise item data:", item);
                const statusMeta = getTourStatusMeta(item.status);

                const configured = isTourItemConfigured(item.status);

                return (
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

                    {/* CRUISE AREA */}
                    <td>
                      <div className="activity-configuration-area">
                        <MapPin size={15} />

                        <div>
                          {/* Tên hiển thị chính (hoặc nhãn chung nếu chưa join được tên) */}
                          <strong>
                            {item.cruiseAreaName ||
                              item.areaName ||
                              "Khu vực sự kiện"}
                          </strong>

                          {/* ID hiển thị nhỏ gọn ở dòng dưới */}
                          {item.cruiseAreaId && (
                            <span
                              style={{
                                fontSize: "0.75rem",
                                color: "#94a3b8",
                                display: "block",
                                fontFamily: "monospace",
                                marginTop: "2px",
                              }}
                              title={item.cruiseAreaId}
                            >
                              ID: {item.cruiseAreaId.substring(0, 8)}...
                            </span>
                          )}

                          {/* Số tầng nếu có */}
                          {(item.deckNumber != null || item.deck != null) && (
                            <span
                              style={{
                                fontSize: "0.8rem",
                                color: "#64748b",
                                display: "block",
                              }}
                            >
                              Tầng {item.deckNumber ?? item.deck}
                            </span>
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
                        className={`activity-configuration-status ${statusMeta.className}`}
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

export default ActivityCruiseConfigurationTable;
