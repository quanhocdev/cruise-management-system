import React from "react";
import {
  Clock,
  CheckCircle2,
  Compass,
  PlayCircle,
  CheckCheck,
  XCircle,
} from "lucide-react";
import OperationTourFilter from "./OperationTourFilter";
import "../styles/OperationTourHeaderToolbar.css";

const TOUR_STATUS_TABS = [
  { key: "APPROVAL_PENDING", label: "Chờ duyệt", icon: Clock },
  { key: "APPROVED", label: "Đã duyệt", icon: CheckCircle2 },
  { key: "READY", label: "Sẵn sàng", icon: Compass },
  { key: "IN_PROGRESS", label: "Đang diễn ra", icon: PlayCircle },
  { key: "COMPLETED", label: "Hoàn thành", icon: CheckCheck },
  { key: "CANCELLED", label: "Đã hủy", icon: XCircle },
];

function OperationTourHeaderToolbar({
  tourMode = "APPROVAL_PENDING",
  statusCounts = {},
  onChangeMode,
  keyword = "",
  startDate = "",
  endDate = "",
  onKeywordChange,
  onStartDateChange,
  onEndDateChange,
  onClearFilter,
}) {
  return (
    <div className="operation-tour-toolbar-wrapper">
      {/* HÀNG 1: 6 TAB TRẠNG THÁI GIÃN ĐỀU BỀ NGANG */}
      <div className="operation-tour-status-tabs">
        {TOUR_STATUS_TABS.map((tab) => {
          const Icon = tab.icon;
          const count = statusCounts[tab.key] || 0;
          const isActive = tourMode === tab.key;

          return (
            <button
              key={tab.key}
              type="button"
              className={`status-tab-btn status-${tab.key.toLowerCase()} ${
                isActive ? "active" : ""
              }`}
              onClick={() => onChangeMode?.(tab.key)}
            >
              <div className="tab-left-content">
                <Icon size={18} />
                <span className="tab-label">{tab.label}</span>
              </div>
              <span className="status-count-badge">{count}</span>
            </button>
          );
        })}
      </div>

      {/* HÀNG 2: BỘ LỌC TÌM KIẾM & NGÀY THÁNG TRÊN CÙNG 1 HÀNG */}
      <div className="operation-tour-filter-bar">
        <OperationTourFilter
          keyword={keyword}
          startDate={startDate}
          endDate={endDate}
          onKeywordChange={onKeywordChange}
          onStartDateChange={onStartDateChange}
          onEndDateChange={onEndDateChange}
          onClear={onClearFilter}
        />
      </div>
    </div>
  );
}

export default OperationTourHeaderToolbar;
