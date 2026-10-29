import React from "react";
import {
  CalendarDays,
  MapPin,
  Settings,
  Ship,
  Eye,
  UserPlus,
} from "lucide-react";

import "../../styles/activity-cruise-tour/ActivityCruiseTourTable.css";

const ActivityCruiseTourTable = ({
  activities = [],
  loading = false,
  onConfigure,
  onViewDetail,
  onAssign,
}) => {
  const formatShortId = (id) => {
    if (!id) return "—";
    return id.length > 8 ? `${id.substring(0, 8)}...` : id;
  };

  if (loading) {
    return (
      <div className="activity-cruise-tour-table-wrapper">
        <div className="activity-cruise-tour-table-loading">
          Đang tải danh sách hoạt động...
        </div>
      </div>
    );
  }

  if (!activities.length) {
    return (
      <div className="activity-cruise-tour-table-wrapper">
        <div className="activity-cruise-tour-table-empty">
          <CalendarDays size={40} />
          <h3>Không có hoạt động</h3>
          <p>Hiện tại chưa có hoạt động nào thuộc các trạng thái đang chọn.</p>
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
                <td>
                  <span className="activity-cruise-tour-id" title={item.id}>
                    #{formatShortId(item.id)}
                  </span>
                </td>

                <td>
                  <div className="activity-cruise-tour-code flex-cell">
                    <div className="id-text-wrapper" title={item.tourId}>
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

                <td>
                  <div className="activity-cruise-tour-area flex-cell">
                    <div className="id-text-wrapper" title={item.cruiseAreaId}>
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

                <td style={{ textAlign: "center" }}>
                  <div
                    className="activity-cruise-tour-actions"
                    style={{
                      justifyContent: "center",
                      flexWrap: "wrap",
                      gap: "8px",
                    }}
                  >
                    {/* PHÂN CÔNG NHÂN VIÊN */}
                    <button
                      type="button"
                      className="activity-cruise-tour-config-button"
                      onClick={() => onAssign?.(item)}
                      title="Phân công nhân viên ONBOARD cho hoạt động"
                    >
                      <UserPlus size={16} />
                      Phân công
                    </button>

                    {/* CẤU HÌNH HOẠT ĐỘNG */}
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
