// src/modules/operation/components/OperationTourFilter.jsx

import { Search, CalendarDays, X } from "lucide-react";

function OperationTourFilter({
  keyword,
  month,
  year,
  onKeywordChange,
  onMonthChange,
  onYearChange,
  onClear,
}) {
  return (
    <div className="operation-tour-filter">
      {/* =====================================================
          SEARCH
          ===================================================== */}

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

      {/* =====================================================
          DATE FILTER
          ===================================================== */}

      <div className="operation-tour-date-filter">
        {/* MONTH */}

        <div className="operation-tour-date-field">
          <CalendarDays size={17} />

          <select value={month} onChange={(e) => onMonthChange(e.target.value)}>
            <option value="">Tháng</option>

            {Array.from({ length: 12 }, (_, index) => {
              const monthValue = String(index + 1).padStart(2, "0");

              return (
                <option key={monthValue} value={monthValue}>
                  Tháng {index + 1}
                </option>
              );
            })}
          </select>
        </div>

        {/* YEAR */}

        <div className="operation-tour-date-field">
          <select value={year} onChange={(e) => onYearChange(e.target.value)}>
            <option value="">Năm</option>

            {Array.from({ length: 11 }, (_, index) => {
              const currentYear = new Date().getFullYear();

              const yearValue = currentYear - 5 + index;

              return (
                <option key={yearValue} value={yearValue}>
                  {yearValue}
                </option>
              );
            })}
          </select>
        </div>

        {/* CLEAR FILTER */}

        {(keyword || month || year) && (
          <button
            type="button"
            className="operation-tour-clear-filter"
            onClick={onClear}
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
