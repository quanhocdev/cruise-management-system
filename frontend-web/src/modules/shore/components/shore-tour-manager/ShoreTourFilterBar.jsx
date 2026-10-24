// src/modules/shore/components/shore-tour-manager/ShoreTourFilterBar.jsx
// (FILE MỚI) 5 ô thống kê + thanh lọc trạng thái.
// CSS: dùng chung styles/ActivityVisitTour.css (import ở page).

import { Clock3 } from "lucide-react";

const STATUS_OPTIONS = [
  { value: "ALL", label: "Tất cả" },
  { value: "WAITING_CONFIG", label: "Chờ cấu hình" },
  { value: "CONFIGURED", label: "Đã cấu hình" },
  { value: "NOT_STARTED", label: "Chưa bắt đầu" },
  { value: "IN_PROGRESS", label: "Đang diễn ra" },
  { value: "COMPLETED", label: "Đã hoàn thành" },
  { value: "DELAYED", label: "Trì hoãn" },
  { value: "CANCELLED", label: "Đã hủy" },
];

const SUMMARY_ITEMS = [
  { key: "total", label: "Tổng Visit Tour", className: "" },
  { key: "waiting", label: "Chờ cấu hình", className: "waiting" },
  { key: "configured", label: "Đã cấu hình", className: "configured" },
  { key: "inProgress", label: "Đang diễn ra", className: "progress" },
  { key: "completed", label: "Đã hoàn thành", className: "completed" },
];

function ShoreTourFilterBar({
  summary,
  statusFilter,
  onStatusChange,
  shown,
  total,
}) {
  return (
    <>
      <div className="shore-manager-tour-summary">
        {SUMMARY_ITEMS.map((item) => (
          <div
            key={item.key}
            className={`shore-manager-tour-summary-item ${item.className}`}
          >
            <span>{item.label}</span>
            <strong>{summary[item.key]}</strong>
          </div>
        ))}
      </div>

      <div className="shore-manager-tour-toolbar">
        <div className="shore-manager-tour-filter">
          <div className="shore-manager-tour-filter-label">
            <Clock3 size={16} />
            <span>Trạng thái</span>
          </div>

          <select
            value={statusFilter}
            onChange={(e) => onStatusChange(e.target.value)}
          >
            {STATUS_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>

        <div className="shore-manager-tour-result-count">
          Hiển thị <strong>{shown}</strong> / {total} Visit Tour
        </div>
      </div>
    </>
  );
}

export default ShoreTourFilterBar;
