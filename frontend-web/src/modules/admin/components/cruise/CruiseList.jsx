// src/modules/admin/components/cruise/CruiseList.jsx

import {
  Layers,
  Pencil,
  Plus,
  Ship,
  SearchX,
  Trash2,
  Users,
} from "lucide-react";

const STATUS_LABELS = {
  ACTIVE: "Hoạt động",
  INACTIVE: "Ngừng hoạt động",
};

function CruiseCard({ cruise, onEdit, onDelete, onManageDecks }) {
  const isActive = cruise.status === "ACTIVE";

  return (
    <article className={`mc-card ${isActive ? "" : "mc-card--inactive"}`}>
      <div className="mc-card-media">
        {cruise.imageUrl ? (
          <img src={cruise.imageUrl} alt={cruise.name} loading="lazy" />
        ) : (
          <div className="mc-card-placeholder">
            <Ship size={44} />
          </div>
        )}

        <span
          className={`mc-badge ${isActive ? "mc-badge--active" : "mc-badge--inactive"}`}
        >
          {STATUS_LABELS[cruise.status] || cruise.status}
        </span>
      </div>

      <div className="mc-card-body">
        <div className="mc-card-title">
          <h3 title={cruise.name}>{cruise.name}</h3>
          <code>{cruise.code}</code>
        </div>

        <p className="mc-card-desc">
          {cruise.description || "Chưa có mô tả cho du thuyền này."}
        </p>

        <div className="mc-card-meta">
          <Users size={16} />
          <span>
            Sức chứa{" "}
            <strong>
              {Number(cruise.maxPassengers || 0).toLocaleString("vi-VN")}
            </strong>{" "}
            khách
          </span>
        </div>
      </div>

      <footer className="mc-card-actions">
        <button
          type="button"
          className="mc-btn mc-btn--primary"
          onClick={() => onManageDecks(cruise)}
        >
          <Layers size={16} />
          Quản lý tầng
        </button>

        <button
          type="button"
          className="mc-icon-btn"
          onClick={() => onEdit(cruise)}
          title="Chỉnh sửa"
          aria-label={`Chỉnh sửa ${cruise.name}`}
        >
          <Pencil size={17} />
        </button>

        <button
          type="button"
          className="mc-icon-btn mc-icon-btn--danger"
          onClick={() => onDelete(cruise)}
          title="Xóa"
          aria-label={`Xóa ${cruise.name}`}
        >
          <Trash2 size={17} />
        </button>
      </footer>
    </article>
  );
}

function CruiseSkeleton() {
  return (
    <div className="mc-card mc-card--skeleton" aria-hidden="true">
      <div className="mc-card-media" />
      <div className="mc-card-body">
        <span className="mc-skeleton mc-skeleton--title" />
        <span className="mc-skeleton" />
        <span className="mc-skeleton mc-skeleton--short" />
      </div>
    </div>
  );
}

export default function CruiseList({
  cruises,
  loading,
  isFiltered,
  onEdit,
  onDelete,
  onManageDecks,
  onCreate,
  onClearFilter,
}) {
  if (loading) {
    return (
      <div className="mc-grid" role="status" aria-label="Đang tải du thuyền">
        {[0, 1, 2].map((key) => (
          <CruiseSkeleton key={key} />
        ))}
      </div>
    );
  }

  if (cruises.length === 0) {
    return (
      <div className="mc-empty">
        {isFiltered ? <SearchX size={44} /> : <Ship size={44} />}

        <h3>
          {isFiltered ? "Không tìm thấy du thuyền" : "Chưa có du thuyền nào"}
        </h3>

        <p>
          {isFiltered
            ? "Thử đổi từ khóa hoặc bộ lọc trạng thái."
            : "Thêm du thuyền đầu tiên để bắt đầu quản lý tầng, khu vực và phòng."}
        </p>

        {isFiltered ? (
          <button
            type="button"
            className="mc-btn mc-btn--ghost"
            onClick={onClearFilter}
          >
            Xóa bộ lọc
          </button>
        ) : (
          <button
            type="button"
            className="mc-btn mc-btn--primary"
            onClick={onCreate}
          >
            <Plus size={16} />
            Thêm du thuyền
          </button>
        )}
      </div>
    );
  }

  return (
    <div className="mc-grid">
      {cruises.map((cruise) => (
        <CruiseCard
          key={cruise.id}
          cruise={cruise}
          onEdit={onEdit}
          onDelete={onDelete}
          onManageDecks={onManageDecks}
        />
      ))}
    </div>
  );
}
