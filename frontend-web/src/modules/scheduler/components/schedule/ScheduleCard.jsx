import { CalendarDays, Eye, Pencil, Trash2, MapPin } from "lucide-react";

const getStatusLabel = (status) => {
  switch (status) {
    case "ACTIVE":
      return "Đang hoạt động";

    case "INACTIVE":
      return "Không hoạt động";

    default:
      return status || "-";
  }
};

const formatDate = (dateString) => {
  if (!dateString) {
    return "-";
  }

  const date = new Date(`${dateString}T00:00:00`);

  return date.toLocaleDateString("vi-VN", {
    weekday: "long",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

function ScheduleCard({ schedule, onEdit, onDelete, onView }) {
  return (
    <article className="schedule-card">
      <div className="schedule-card-top">
        <span className="schedule-card-label">Lịch trình trong ngày</span>

        <span
          className={`schedule-status ${
            schedule.status === "ACTIVE" ? "active" : "inactive"
          }`}
        >
          {getStatusLabel(schedule.status)}
        </span>
      </div>

      <div className="schedule-card-body">
        <h3>{schedule.name}</h3>

        <div className="schedule-date">
          <CalendarDays size={15} />

          <span>{formatDate(schedule.realDay)}</span>
        </div>

        {schedule.description && (
          <p>
            {schedule.description.length > 130
              ? `${schedule.description.substring(0, 130)}...`
              : schedule.description}
          </p>
        )}

        <div className="schedule-card-stops">
          <MapPin size={16} />

          <span>Quản lý các điểm dừng cập cảng của ngày này</span>
        </div>
      </div>

      <div className="schedule-card-actions">
        <button
          type="button"
          className="schedule-action view"
          onClick={() => onView?.(schedule)}
        >
          <Eye size={16} />
          Điểm dừng
        </button>

        <button
          type="button"
          className="schedule-action edit"
          onClick={() => onEdit?.(schedule)}
        >
          <Pencil size={16} />
          Sửa
        </button>

        <button
          type="button"
          className="schedule-action delete"
          onClick={() => onDelete?.(schedule)}
          title="Xóa lịch trình"
        >
          <Trash2 size={16} />
        </button>
      </div>
    </article>
  );
}

export default ScheduleCard;
