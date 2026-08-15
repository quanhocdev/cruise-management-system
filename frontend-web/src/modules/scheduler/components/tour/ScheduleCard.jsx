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

function ScheduleCard({ schedule, onEdit, onDelete, onView }) {
  return (
    <article className="schedule-card">
      <div className="schedule-card-top">
        <div className="schedule-day">
          <CalendarDays size={18} />

          <span>Ngày {schedule.dayNumber}</span>
        </div>

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

        {schedule.realDay && (
          <div className="schedule-date">
            <CalendarDays size={15} />

            <span>{schedule.realDay}</span>
          </div>
        )}

        {schedule.description && (
          <p>
            {schedule.description.length > 130
              ? `${schedule.description.substring(0, 130)}...`
              : schedule.description}
          </p>
        )}

        <div className="schedule-card-stops">
          <MapPin size={16} />

          <span>Quản lý các điểm dừng tại lịch trình này</span>
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
