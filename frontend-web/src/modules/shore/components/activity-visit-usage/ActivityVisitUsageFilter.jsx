import { Search, RotateCcw } from "lucide-react";

const STATUS_OPTIONS = [
  { value: "ALL", label: "Tất cả trạng thái" },
  { value: "WAITING_CONFIG", label: "Chờ cấu hình" },
  { value: "CONFIGURED", label: "Đã cấu hình" },
  { value: "NOT_STARTED", label: "Chưa diễn ra" },
  { value: "IN_PROGRESS", label: "Đang diễn ra" },
  { value: "COMPLETED", label: "Đã kết thúc" },
  { value: "DELAYED", label: "Bị trì hoãn" },
  { value: "CANCELLED", label: "Đã hủy" },
];

const ActivityVisitUsageFilter = ({
  searchKeyword,
  setSearchKeyword,
  statusFilter,
  setStatusFilter,
  onReset,
}) => {
  return (
    <div className="activity-visit-usage-filter">
      {/* SEARCH */}
      <div className="activity-visit-usage-filter__search">
        <Search
          size={18}
          className="activity-visit-usage-filter__search-icon"
        />

        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          placeholder="Tìm hành khách, mã booking, hoạt động..."
          className="activity-visit-usage-filter__search-input"
        />
      </div>

      {/* STATUS */}
      <select
        value={statusFilter}
        onChange={(e) => setStatusFilter(e.target.value)}
        className="activity-visit-usage-filter__status"
      >
        {STATUS_OPTIONS.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>

      {/* RESET */}
      <button
        type="button"
        className="activity-visit-usage-filter__reset"
        onClick={onReset}
      >
        <RotateCcw size={16} />
        <span>Đặt lại</span>
      </button>
    </div>
  );
};

export default ActivityVisitUsageFilter;
