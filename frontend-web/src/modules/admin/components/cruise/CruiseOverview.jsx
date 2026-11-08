// src/modules/admin/components/cruise/CruiseOverview.jsx

import {
  CheckCircle2,
  PauseCircle,
  Search,
  Ship,
  Users,
  X,
} from "lucide-react";

export function CruiseStats({ cruises }) {
  const active = cruises.filter((c) => c.status === "ACTIVE").length;
  const capacity = cruises.reduce(
    (total, c) => total + (Number(c.maxPassengers) || 0),
    0,
  );

  const items = [
    {
      label: "Tổng du thuyền",
      value: cruises.length,
      icon: Ship,
      tone: "blue",
    },
    {
      label: "Đang hoạt động",
      value: active,
      icon: CheckCircle2,
      tone: "green",
    },
    {
      label: "Ngừng hoạt động",
      value: cruises.length - active,
      icon: PauseCircle,
      tone: "gray",
    },
  ];

  return (
    <section className="mc-stats">
      {items.map(({ label, value, icon: Icon, tone }) => (
        <div key={label} className="mc-stat">
          <span className={`mc-stat-icon mc-stat-icon--${tone}`}>
            <Icon size={22} />
          </span>

          <div>
            <strong>{value}</strong>
            <span>{label}</span>
          </div>
        </div>
      ))}
    </section>
  );
}

const FILTERS = [
  { value: "ALL", label: "Tất cả" },
  { value: "ACTIVE", label: "Hoạt động" },
  { value: "INACTIVE", label: "Ngừng hoạt động" },
];

export function CruiseToolbar({
  keyword,
  onKeywordChange,
  statusFilter,
  onStatusChange,
  counts,
  shown,
  total,
}) {
  return (
    <section className="mc-toolbar">
      <div className="mc-search">
        <Search size={18} />
        <input
          type="text"
          value={keyword}
          onChange={(event) => onKeywordChange(event.target.value)}
          placeholder="Tìm theo tên hoặc mã du thuyền..."
          aria-label="Tìm du thuyền"
        />

        {keyword && (
          <button
            type="button"
            onClick={() => onKeywordChange("")}
            aria-label="Xóa từ khóa"
          >
            <X size={16} />
          </button>
        )}
      </div>

      <div
        className="mc-filter"
        role="tablist"
        aria-label="Lọc theo trạng thái"
      >
        {FILTERS.map((filter) => (
          <button
            key={filter.value}
            type="button"
            role="tab"
            aria-selected={statusFilter === filter.value}
            className={statusFilter === filter.value ? "is-active" : ""}
            onClick={() => onStatusChange(filter.value)}
          >
            {filter.label}
            <span>{counts[filter.value]}</span>
          </button>
        ))}
      </div>

      <p className="mc-count">
        Hiển thị <strong>{shown}</strong> / {total} du thuyền
      </p>
    </section>
  );
}
