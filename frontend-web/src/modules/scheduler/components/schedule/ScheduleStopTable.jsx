import { Pencil, Trash2, MapPin, Clock3 } from "lucide-react";

const formatDateTime = (value) => {
  if (!value) return "-";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

function ScheduleStopTable({ stops = [], loading = false, onEdit, onDelete }) {
  if (loading) {
    return (
      <div className="schedule-stop-state">
        <div className="tour-table-spinner" />
        <span>Đang tải điểm dừng...</span>
      </div>
    );
  }

  if (!stops.length) {
    return (
      <div className="schedule-stop-state">
        <MapPin size={42} />

        <h3>Chưa có điểm dừng</h3>

        <p>Thêm cảng để xây dựng hành trình cho ngày này.</p>
      </div>
    );
  }

  return (
    <div className="schedule-stop-table-wrapper">
      <table className="schedule-stop-table">
        <thead>
          <tr>
            <th>Thứ tự</th>
            <th>Cảng</th>
            <th>Giờ đến</th>
            <th>Giờ rời</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {stops.map((stop) => (
            <tr key={stop.id}>
              <td>
                <span className="stop-order">{stop.stopOrder}</span>
              </td>

              <td>
                <div className="stop-port">
                  <MapPin size={17} />

                  <strong>{stop.portName || "Chưa xác định"}</strong>
                </div>
              </td>

              <td>
                <div className="stop-time">
                  <Clock3 size={15} />

                  {formatDateTime(stop.arriveAt)}
                </div>
              </td>

              <td>
                <div className="stop-time">
                  <Clock3 size={15} />

                  {formatDateTime(stop.leaveAt)}
                </div>
              </td>

              <td>
                <div className="schedule-stop-actions">
                  <button
                    type="button"
                    className="icon-button edit"
                    title="Chỉnh sửa"
                    onClick={() => onEdit?.(stop)}
                  >
                    <Pencil size={17} />
                  </button>

                  <button
                    type="button"
                    className="icon-button delete"
                    title="Xóa"
                    onClick={() => onDelete?.(stop)}
                  >
                    <Trash2 size={17} />
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ScheduleStopTable;
