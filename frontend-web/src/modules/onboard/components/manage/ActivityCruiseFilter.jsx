import React from "react";
import { Search, Filter } from "lucide-react";

function ActivityCruiseFilter({
  searchTerm,
  onSearchChange,
  statusFilter,
  onStatusChange,
}) {
  return (
    <div
      style={{
        display: "flex",
        gap: "12px",
        marginBottom: "16px",
        backgroundColor: "#fff",
        padding: "12px",
        borderRadius: "8px",
        border: "1px solid #e2e8f0",
      }}
    >
      {/* Ô Nhập Tìm Kiếm */}
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "8px",
          flex: 1,
          backgroundColor: "#f8fafc",
          border: "1px solid #cbd5e1",
          borderRadius: "6px",
          padding: "0 12px",
        }}
      >
        <Search size={18} color="#64748b" />
        <input
          type="text"
          placeholder="Tìm theo tên hoạt động hoặc mô tả..."
          value={searchTerm}
          onChange={(e) => onSearchChange(e.target.value)}
          style={{
            width: "100%",
            padding: "8px 0",
            border: "none",
            outline: "none",
            backgroundColor: "transparent",
            fontSize: "14px",
          }}
        />
      </div>

      {/* Select Lọc Trạng Thái */}
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "8px",
          backgroundColor: "#f8fafc",
          border: "1px solid #cbd5e1",
          borderRadius: "6px",
          padding: "0 12px",
        }}
      >
        <Filter size={18} color="#64748b" />
        <select
          value={statusFilter}
          onChange={(e) => onStatusChange(e.target.value)}
          style={{
            border: "none",
            outline: "none",
            backgroundColor: "transparent",
            padding: "8px 0",
            fontSize: "14px",
            color: "#334155",
            cursor: "pointer",
          }}
        >
          <option value="ALL">Tất cả trạng thái</option>
          <option value="ACTIVE">Hoạt động (ACTIVE)</option>
          <option value="INACTIVE">Tạm ngưng (INACTIVE)</option>
        </select>
      </div>
    </div>
  );
}

export default ActivityCruiseFilter;
