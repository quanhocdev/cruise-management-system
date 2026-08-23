// src/modules/operation/components/OperationTourFilter.jsx

import { Search, Calendar, X } from "lucide-react";
import "../styles/OperationTourFilter.css";

function OperationTourFilter({
  keyword = "",
  startDate = "",
  endDate = "",
  onKeywordChange,
  onStartDateChange,
  onEndDateChange,
  onClear,
}) {
  const hasFilter = Boolean(keyword || startDate || endDate);

  return (
    <div className="operation-tour-filter">
      {/* SEARCH KEYWORD */}
      <div className="operation-tour-search">
        <Search size={18} />
        <input
          type="text"
          value={keyword}
          onChange={(e) => onKeywordChange(e.target.value)}
          placeholder="Tìm tên Tour, mã Tour hoặc mô tả..."
        />
        {keyword && (
          <button
            type="button"
            className="operation-tour-search-clear"
            onClick={() => onKeywordChange("")}
            title="Xóa tìm kiếm"
          >
            <X size={16} />
          </button>
        )}
      </div>

      {/* DATE RANGE */}
      <div className="operation-tour-date-range">
        {/* TỪ NGÀY */}
        <div className="operation-tour-date-field">
          <Calendar size={16} />
          <input
            type="date"
            value={startDate}
            onChange={(e) => onStartDateChange(e.target.value)}
          />
        </div>

        <span className="operation-tour-date-separator">-</span>

        {/* ĐẾN NGÀY */}
        <div className="operation-tour-date-field">
          <Calendar size={16} />
          <input
            type="date"
            value={endDate}
            onChange={(e) => onEndDateChange(e.target.value)}
          />
        </div>

        {/* CLEAR FILTER */}
        {hasFilter && (
          <button
            type="button"
            className="operation-tour-clear-filter"
            onClick={onClear}
            title="Xóa bộ lọc"
          >
            <X size={15} />
            <span>Xóa lọc</span>
          </button>
        )}
      </div>
    </div>
  );
}

export default OperationTourFilter;
