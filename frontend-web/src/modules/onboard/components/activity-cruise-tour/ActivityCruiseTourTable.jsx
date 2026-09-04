// src/modules/onboard/components/activity-cruise-tour/ActivityCruiseTourTable.jsx
import React from "react";
import { CalendarDays, MapPin, Settings, Ship, Eye } from "lucide-react";

import "../../styles/activity-cruise-tour/ActivityCruiseTourTable.css";

const ActivityCruiseTourTable = ({
  activities = [],
  loading = false,
  onConfigure,
  onViewDetail,
}) => {
  // Hàm bổ trợ cắt ngắn UUID nếu cần
  const formatShortId = (id) => {
    if (!id) return "—";
    return id.length > 8 ? `${id.substring(0, 8)}...` : id;
  };

  if (loading) {
    return (
      <div className="activity-cruise-tour-table-wrapper">
        <div className="activity-cruise-tour-table-loading">
          Đang tải danh sách hoạt động cần cấu hình...
        </div>
      </div>
    );
  }

  if (!activities.length) {
    return (
      <div className="activity-cruise-tour-table-wrapper">
        <div className="activity-cruise-tour-table-empty">
          <CalendarDays size={40} />
          <h3>Không có hoạt động cần cấu hình</h3>
          <p>Hiện tại không có hoạt động nào đang chờ ONBOARD cấu hình.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="activity-cruise-tour-table-wrapper">
      <div className="activity-cruise-tour-table-scroll">
        <table className="activity-cruise-tour-table">
          <thead>
            <tr>
              <th>ID Phân công</th>
              <th>Mã Tour (ID)</th>
              <th>Mã Tàu / Khu vực (ID)</th>
              <th>Trạng thái</th>
              <th style={{ textAlign: "center" }}>Thao tác</th>
            </tr>
          </thead>

          <tbody>
            {activities.map((item) => (
              <tr key={item.id}>
                {/* ASSIGNMENT ID */}
                <td>
                  <span
                    className="activity-cruise-tour-id"
                    title={item.id} // Di chuột vào để xem đầy đủ UUID
                  >
                    #{formatShortId(item.id)}
                  </span>
                </td>

                {/* TOUR ID + NÚT XEM DETAIL */}
                <td>
                  <div className="activity-cruise-tour-code flex-cell">
                    <div
                      className="id-text-wrapper"
                      title={item.tourId} // Di chuột vào xem full tourId
                    >
                      <Ship size={16} />
                      <span className="truncate-id">{item.tourId || "—"}</span>
                    </div>

                    {item.tourId && (
                      <button
                        type="button"
                        className="activity-cruise-tour-inline-btn"
                        title="Xem chi tiết Tour"
                        onClick={() => onViewDetail?.(item, "TOUR")}
                      >
                        <Eye size={14} />
                      </button>
                    )}
                  </div>
                </td>

                {/* CRUISE AREA ID + NÚT XEM DETAIL */}
                <td>
                  <div className="activity-cruise-tour-area flex-cell">
                    <div
                      className="id-text-wrapper"
                      title={item.cruiseAreaId} // Di chuột vào xem full cruiseAreaId
                    >
                      <MapPin size={16} />
                      <span className="truncate-id">
                        {item.cruiseAreaId || "—"}
                      </span>
                    </div>

                    {item.cruiseAreaId && (
                      <button
                        type="button"
                        className="activity-cruise-tour-inline-btn"
                        title="Xem chi tiết Tàu / Khu vực"
                        onClick={() => onViewDetail?.(item, "CRUISE_AREA")}
                      >
                        <Eye size={14} />
                      </button>
                    )}
                  </div>
                </td>

                {/* STATUS */}
                <td>
                  <span
                    className={`activity-cruise-tour-status status-${String(
                      item.status || "",
                    ).toLowerCase()}`}
                  >
                    {item.status === "WAITING_CONFIG"
                      ? "Chờ cấu hình"
                      : item.status || "—"}
                  </span>
                </td>

                {/* ACTIONS */}
                <td style={{ textAlign: "center" }}>
                  <div
                    className="activity-cruise-tour-actions"
                    style={{ justifyContent: "center" }}
                  >
                    {item.status === "WAITING_CONFIG" && (
                      <button
                        type="button"
                        className="activity-cruise-tour-config-button"
                        onClick={() => onConfigure?.(item)}
                      >
                        <Settings size={16} />
                        Cấu hình
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ActivityCruiseTourTable;
