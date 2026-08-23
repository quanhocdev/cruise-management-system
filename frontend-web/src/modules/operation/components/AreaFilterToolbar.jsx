// AreaFilterToolbar.jsx
import React from "react";
import { Search, Filter, X } from "lucide-react";
import "../styles/AreaFilterToolbar.css";
function AreaFilterToolbar({
  searchTerm,
  setSearchTerm,
  viewType,
  setViewType,
}) {
  return (
    <div className="caam-toolbar">
      {/* Search Input */}
      <div className="caam-search-box">
        <Search size={16} />
        <input
          type="text"
          placeholder="Tìm kiếm khu vực, phòng..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        {searchTerm && (
          <button
            type="button"
            className="clear-search"
            onClick={() => setSearchTerm("")}
          >
            <X size={14} />
          </button>
        )}
      </div>

      {/* Filter Buttons */}
      <div className="caam-filter-group">
        <Filter size={15} className="filter-icon" />
        <button
          type="button"
          className={`filter-btn ${viewType === "ALL" ? "active" : ""}`}
          onClick={() => setViewType("ALL")}
        >
          Tất cả
        </button>
        <button
          type="button"
          className={`filter-btn ${viewType === "AREA" ? "active" : ""}`}
          onClick={() => setViewType("AREA")}
        >
          Khu vực
        </button>
        <button
          type="button"
          className={`filter-btn ${viewType === "ROOM" ? "active" : ""}`}
          onClick={() => setViewType("ROOM")}
        >
          Phòng
        </button>
      </div>
    </div>
  );
}

export default AreaFilterToolbar;
