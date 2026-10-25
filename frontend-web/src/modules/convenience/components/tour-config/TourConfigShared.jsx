// src/modules/convenience/tour-config/TourConfigShared.jsx
// (FILE MỚI) Các phần dùng chung của ServiceTourConfigTable và ProductTourConfigTable.
// Prop `prefix` = "tour-config" (class CSS nằm trong styles/tour-config/TourConfigTable.css)

import { useMemo, useState } from "react";
import { CheckCircle2, Edit3, Eye, Plus } from "lucide-react";

export const STATUS_LABELS = {
  ALL: "Tất cả",
  WAITING_CONFIG: "Chờ cấu hình",
  CONFIGURED: "Đã cấu hình",
  NOT_STARTED: "Chưa bắt đầu",
  IN_PROGRESS: "Đang phục vụ",
  OUT_OF_STOCK: "Hết hàng",
  COMPLETED: "Đã kết thúc",
};

export const getStatusLabel = (status) =>
  STATUS_LABELS[status] || status || "Không xác định";

export const formatShortId = (str, maxLength = 8) => {
  if (!str) return "—";
  const value = String(str);
  if (value.length <= maxLength) return value;
  return `${value.substring(0, maxLength)}...`;
};

// ---------------------------------------------------------
// Tab lọc theo trạng thái. statuses = ["ALL", "WAITING_CONFIG", ...]
// ---------------------------------------------------------
export function StatusTabs({ prefix, statuses, value, onChange }) {
  return (
    <div className={`${prefix}-filters`}>
      {statuses.map((status) => (
        <button
          key={status}
          type="button"
          className={`${prefix}-filter-btn ${
            value === status ? `${prefix}-filter-btn--active` : ""
          }`}
          onClick={() => onChange(status)}
        >
          {STATUS_LABELS[status]}
        </button>
      ))}
    </div>
  );
}

// ---------------------------------------------------------
// Ô "Tour" và ô "Khu vực" (có nút con mắt xem chi tiết)
// ---------------------------------------------------------
const cellStyle = { display: "flex", alignItems: "center", gap: "6px" };

export function TourCell({ prefix, assignment, onView }) {
  const tourId = assignment.tourId || assignment.id;
  const fullCode = assignment.tourCode || (tourId ? `TOUR-${tourId}` : "");

  return (
    <div className={`${prefix}-tour`} style={cellStyle}>
      <strong className="font-mono" title={fullCode}>
        {assignment.tourCode ||
          (tourId ? `TOUR-${formatShortId(tourId, 6)}` : "—")}
      </strong>
      <button
        type="button"
        className={`${prefix}-action ${prefix}-action--view`}
        onClick={() => onView(assignment)}
        title="Xem chi tiết Tour"
      >
        <Eye size={14} />
      </button>
      {assignment.tourName && <span>{assignment.tourName}</span>}
    </div>
  );
}

export function AreaCell({ prefix, assignment, onView }) {
  const fullArea = assignment.cruiseAreaName || assignment.cruiseAreaId || "";

  return (
    <div className={`${prefix}-area`} style={cellStyle}>
      <strong className="font-mono" title={fullArea}>
        {assignment.cruiseAreaName || formatShortId(assignment.cruiseAreaId, 6)}
      </strong>
      <button
        type="button"
        className={`${prefix}-action ${prefix}-action--view`}
        onClick={() => onView(assignment)}
        title="Xem chi tiết Khu vực"
      >
        <Eye size={14} />
      </button>
      {assignment.deckNumber != null && (
        <span>Tầng {assignment.deckNumber}</span>
      )}
    </div>
  );
}

// ---------------------------------------------------------
// Nút thao tác: WAITING_CONFIG -> Cấu hình, CONFIGURED -> Chỉnh sửa
// (khớp backend: configure() / updateConfig())
// ---------------------------------------------------------
export function ConfigAction({ prefix, assignment, onOpen }) {
  if (assignment.status === "WAITING_CONFIG") {
    return (
      <button
        type="button"
        className={`${prefix}-action`}
        onClick={() => onOpen(assignment)}
        title="Cấu hình"
      >
        <Plus size={16} />
        <span>Cấu hình</span>
      </button>
    );
  }

  if (assignment.status === "CONFIGURED") {
    return (
      <button
        type="button"
        className={`${prefix}-action`}
        onClick={() => onOpen(assignment)}
        title="Chỉnh sửa"
      >
        <Edit3 size={16} />
        <span>Chỉnh sửa</span>
      </button>
    );
  }

  return null;
}

// ---------------------------------------------------------
// Khung "Hoàn thành cấu hình Tour" (chọn Tour + nút Hoàn thành)
// noun = "dịch vụ" | "sản phẩm"
// onComplete(tourId) phải trả về Promise (throw nếu lỗi)
// ---------------------------------------------------------
export function TourCompleteBox({
  prefix,
  noun,
  tourSummaries,
  completing,
  completeError,
  onComplete,
}) {
  const [selectedTourId, setSelectedTourId] = useState("");
  const [success, setSuccess] = useState(false);

  const selectedSummary = useMemo(
    () => tourSummaries.find((tour) => tour.tourId === selectedTourId),
    [tourSummaries, selectedTourId],
  );

  const canComplete =
    !!selectedTourId && (!selectedSummary || !selectedSummary.completed);

  const handleComplete = async () => {
    if (!canComplete) return;

    try {
      setSuccess(false);
      await onComplete(selectedTourId);
      setSuccess(true);
      setSelectedTourId("");
    } catch (err) {
      console.error("COMPLETE TOUR CONFIG ERROR:", err);
      // completeError đã được hook set
    }
  };

  return (
    <div className={`${prefix}-complete-box`}>
      <div className={`${prefix}-complete-info`}>
        <strong>Hoàn thành cấu hình Tour</strong>
        <p>
          Chọn Tour và bấm Hoàn thành khi tất cả {noun} của Tour đó đã được cấu
          hình xong. Hệ thống sẽ gửi thông tin sang Tour service.
        </p>
      </div>

      <div className={`${prefix}-complete-controls`}>
        <select
          className={`${prefix}-complete-select`}
          value={selectedTourId}
          onChange={(e) => {
            setSelectedTourId(e.target.value);
            setSuccess(false);
          }}
          disabled={completing}
        >
          <option value="">— Chọn Tour —</option>
          {tourSummaries.map((tour) => (
            <option key={tour.tourId} value={tour.tourId}>
              {formatShortId(tour.tourId, 8)} ({tour.configuredCount}/
              {tour.total} đã cấu hình)
              {tour.completed ? " — Đã hoàn thành" : ""}
            </option>
          ))}
        </select>

        <button
          type="button"
          className={`${prefix}-complete-button`}
          onClick={handleComplete}
          disabled={!canComplete || completing}
        >
          <CheckCircle2 size={16} />
          {completing ? "Đang xử lý..." : "Hoàn thành cấu hình"}
        </button>
      </div>

      {selectedTourId && selectedSummary?.completed && (
        <span className={`${prefix}-complete-hint`}>
          Tour này đã được hoàn thành cấu hình trước đó.
        </span>
      )}

      {completeError && (
        <span className={`${prefix}-complete-error`}>{completeError}</span>
      )}

      {success && (
        <span className={`${prefix}-complete-success`}>
          Đã hoàn thành cấu hình Tour thành công.
        </span>
      )}
    </div>
  );
}
