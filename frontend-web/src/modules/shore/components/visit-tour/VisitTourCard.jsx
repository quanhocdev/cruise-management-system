import { Clock, Pencil, Trash2, Users } from "lucide-react";
import "../../styles/VisitTourCard.css";
const STATUS_LABELS = {
  NOT_STARTED: "Chưa bắt đầu",
  IN_PROGRESS: "Đang diễn ra",
  COMPLETED: "Đã hoàn thành",
  DELAYED: "Trì hoãn",
  CANCELLED: "Đã hủy",
};

const STATUS_TRANSITIONS = {
  NOT_STARTED: [
    {
      value: "IN_PROGRESS",
      label: "Đang diễn ra",
    },
    {
      value: "CANCELLED",
      label: "Hủy",
    },
  ],

  IN_PROGRESS: [
    {
      value: "COMPLETED",
      label: "Hoàn thành",
    },
    {
      value: "DELAYED",
      label: "Trì hoãn",
    },
    {
      value: "CANCELLED",
      label: "Hủy",
    },
  ],

  DELAYED: [
    {
      value: "IN_PROGRESS",
      label: "Tiếp tục",
    },
    {
      value: "COMPLETED",
      label: "Hoàn thành",
    },
    {
      value: "CANCELLED",
      label: "Hủy",
    },
  ],

  COMPLETED: [],

  CANCELLED: [],
};

function formatPrice(price) {
  if (price === null || price === undefined) {
    return "-";
  }

  return new Intl.NumberFormat("vi-VN").format(price);
}

function VisitTourCard({ visitTour, onEdit, onDelete, onStatusChange }) {
  const transitions = STATUS_TRANSITIONS[visitTour.status] || [];

  const statusLabel = STATUS_LABELS[visitTour.status] || visitTour.status;

  const canEdit =
    visitTour.status !== "COMPLETED" && visitTour.status !== "CANCELLED";

  return (
    <article
      className={`visit-tour-card status-${visitTour.status?.toLowerCase()}`}
    >
      {/* =================================================
          HEADER
          ================================================= */}

      <div className="visit-tour-card-header">
        <div className="visit-tour-card-title">
          <h4>{visitTour.name}</h4>

          <span
            className={`visit-tour-status status-${visitTour.status?.toLowerCase()}`}
          >
            {statusLabel}
          </span>
        </div>

        <div className="visit-tour-card-actions">
          {canEdit && (
            <button
              type="button"
              title="Chỉnh sửa"
              onClick={() => onEdit(visitTour)}
            >
              <Pencil size={16} />
            </button>
          )}

          {canEdit && (
            <button
              type="button"
              title="Xóa"
              onClick={() => onDelete(visitTour)}
            >
              <Trash2 size={16} />
            </button>
          )}
        </div>
      </div>

      {/* =================================================
          DESCRIPTION
          ================================================= */}

      {visitTour.description && (
        <p className="visit-tour-card-description">{visitTour.description}</p>
      )}

      {/* =================================================
          INFORMATION
          ================================================= */}

      <div className="visit-tour-card-info">
        <div className="visit-tour-card-info-item">
          <Clock size={16} />

          <span>{visitTour.startTime}</span>

          <span>→</span>

          <span>{visitTour.endTime}</span>
        </div>

        <div className="visit-tour-card-info-item">
          <Users size={16} />

          <span>Tối đa {visitTour.maxPassengers} khách</span>
        </div>

        <div className="visit-tour-card-price">
          {formatPrice(visitTour.price)} ₫
        </div>
      </div>

      {/* =================================================
          STATUS ACTION
          ================================================= */}

      {transitions.length > 0 && (
        <div className="visit-tour-card-status-actions">
          <label htmlFor={`status-${visitTour.id}`}>Cập nhật trạng thái</label>

          <select
            id={`status-${visitTour.id}`}
            value=""
            onChange={(event) => {
              if (!event.target.value) {
                return;
              }

              onStatusChange(visitTour, event.target.value);
            }}
          >
            <option value="">Chọn trạng thái</option>

            {transitions.map((transition) => (
              <option key={transition.value} value={transition.value}>
                {transition.label}
              </option>
            ))}
          </select>
        </div>
      )}
    </article>
  );
}

export default VisitTourCard;
