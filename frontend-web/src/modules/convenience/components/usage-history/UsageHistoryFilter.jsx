// src/modules/convenience/components/usage-history/UsageHistoryFilter.jsx
import { Search, RotateCcw } from "lucide-react";
import "./UsageHistoryFilter.css";
const UsageHistoryFilter = ({
  searchKeyword,
  setSearchKeyword,
  statusFilter,
  setStatusFilter,
  statusOptions = [],
  onReset,
}) => {
  return (
    <div className="usage-history-filter">
      <div className="usage-history-filter__search">
        <Search size={18} className="usage-history-filter__search-icon" />

        <input
          type="text"
          value={searchKeyword}
          onChange={(event) => setSearchKeyword(event.target.value)}
          placeholder="Tìm hành khách, booking, sản phẩm hoặc dịch vụ..."
          className="usage-history-filter__search-input"
        />
      </div>

      {statusOptions.length > 0 && (
        <select
          value={statusFilter}
          onChange={(event) => setStatusFilter(event.target.value)}
          className="usage-history-filter__status"
          aria-label="Lọc theo trạng thái"
        >
          {statusOptions.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      )}

      <button
        type="button"
        className="usage-history-filter__reset"
        onClick={onReset}
      >
        <RotateCcw size={16} />
        <span>Đặt lại</span>
      </button>
    </div>
  );
};

export default UsageHistoryFilter;
