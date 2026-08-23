// src/modules/onboard/components/activity-cruise-tour/ActivityCruiseTourTable.jsx
import React from "react";
import { CalendarDays, MapPin, Settings, Ship } from "lucide-react";

import "../../styles/activity-cruise-tour/ActivityCruiseTourTable.css";

const ActivityCruiseTourTable = ({
  activities = [],
  loading = false,
  onConfigure,
  onViewDetail,
}) => {
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
              <th>Tour</th>
              <th>Tên tour</th>
              <th>Khu vực</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>

          <tbody>
            {activities.map((item) => (
              <tr key={item.id}>
                {/* TOUR CODE */}
                <td>
                  <div className="activity-cruise-tour-code">
                    <Ship size={16} />
                    <span>{item.tourCode || "—"}</span>
                  </div>
                </td>

                {/* TOUR NAME */}
                <td>
                  <div className="activity-cruise-tour-name">
                    {item.tourName || "—"}
                  </div>
                </td>

                {/* CRUISE AREA */}
                <td>
                  <div className="activity-cruise-tour-area">
                    <MapPin size={16} />
                    <span>{item.cruiseAreaName || "—"}</span>
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
                <td>
                  <div className="activity-cruise-tour-actions">
                    <button
                      type="button"
                      className="activity-cruise-tour-detail-button"
                      onClick={() => onViewDetail?.(item)}
                    >
                      Chi tiết
                    </button>

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
