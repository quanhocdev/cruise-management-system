// src/modules/finance/components/FilterToolbar.jsx
import React from "react";
import "./FilterToolbar.css";

const FilterToolbar = ({
  filterStatus,
  onStatusChange,
  searchTerm,
  onSearchChange,
}) => {
  return (
    <div className="filter-toolbar">
      <input
        type="text"
        placeholder="Tìm kiếm..."
        value={searchTerm}
        onChange={(e) => onSearchChange(e.target.value)}
        className="filter-search-input"
      />
      <select
        value={filterStatus}
        onChange={(e) => onStatusChange(e.target.value)}
        className="filter-status-select"
      >
        <option value="ALL">Tất cả trạng thái</option>
        <option value="ACTIVE">Active / Available</option>
        <option value="IN_USE">In Use</option>
      </select>
    </div>
  );
};

export default FilterToolbar;
