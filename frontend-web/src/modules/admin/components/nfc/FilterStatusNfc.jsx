import React from "react";
import "../../styles/FilterStatusNfc.css";
export default function FilterStatusNfc({ currentFilter, onFilterChange }) {
  const statuses = [
    { value: "ALL", label: "Tất cả" },
    { value: "AVAILABLE", label: "Có sẵn" },
    { value: "ASSIGNED", label: "Đã gán" },
    { value: "INACTIVE", label: "Không hoạt động" },
  ];

  return (
    <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
      <span style={{ fontSize: "14px", color: "#4b5563", fontWeight: 500 }}>
        Lọc trạng thái:
      </span>
      <select
        value={currentFilter}
        onChange={(e) => onFilterChange(e.target.value)}
        className="nfc-form-select"
        style={{ width: "auto", padding: "6px 12px" }}
      >
        {statuses.map((status) => (
          <option key={status.value} value={status.value}>
            {status.label}
          </option>
        ))}
      </select>
    </div>
  );
}
