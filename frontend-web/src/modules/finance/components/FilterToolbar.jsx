// src/modules/finance/components/FilterToolbar.jsx
import React from "react";
import "./FilterToolbar.css";

const FilterToolbar = ({
  filterStatus,
  onStatusChange,
  searchTerm,
  onSearchChange,
  statusOptions = [],
  searchPlaceholder = "Tìm kiếm...",
}) => (
  <div className="filter-toolbar">
    <input
      type="text"
      placeholder={searchPlaceholder}
      value={searchTerm}
      onChange={(e) => onSearchChange(e.target.value)}
      className="filter-search-input"
    />
    {statusOptions.length > 0 && (
      <select
        value={filterStatus}
        onChange={(e) => onStatusChange(e.target.value)}
        className="filter-status-select"
      >
        <option value="ALL">Tất cả trạng thái</option>
        {statusOptions.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
    )}
  </div>
);

export default FilterToolbar;
