import { CalendarDays, Plus } from "lucide-react";
import ScheduleCard from "./ScheduleCard";

function ScheduleList({
  schedules = [],
  loading = false,
  onCreate,
  onEdit,
  onDelete,
  onView,
}) {
  if (loading) {
    return (
      <div className="schedule-list-state">
        <div className="tour-table-spinner" />
        <span>Đang tải lịch trình...</span>
      </div>
    );
  }

  return (
    <section className="schedule-list">
      <div className="schedule-list-header">
        <div>
          <div className="section-title">
            <CalendarDays size={21} />

            <h2>Lịch trình tour</h2>
          </div>

          <p>
            Quản lý từng ngày và các điểm dừng trong hành trình.
          </p>
        </div>

        <button
          type="button"
          className="primary-button"
          onClick={onCreate}
        >
          <Plus size={17} />
          Thêm lịch trình
        </button>
      </div>

      {!schedules.length ? (
        <div className="schedule-list-empty">
          <CalendarDays size={44} />

          <h3>Chưa có lịch trình</h3>

          <p>
            Hãy tạo lịch trình cho từng ngày của tour.
          </p>

          <button
            type="button"
            className="primary-button"
            onClick={onCreate}
          >
            <Plus size={17} />
            Tạo lịch trình đầu tiên
          </button>
        </div>
      ) : (
        <div className="schedule-grid">
          {schedules.map((schedule) => (
            <ScheduleCard
              key={schedule.id}
              schedule={schedule}
              onEdit={onEdit}
              onDelete={onDelete}
              onView={onView}
            />
          ))}
        </div>
      )}
    </section>
  );
}

export default ScheduleList;
