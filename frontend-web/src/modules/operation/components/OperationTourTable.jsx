import { CheckCircle, Ship, CalendarDays, Eye } from "lucide-react";

function formatDate(value) {
  if (!value) {
    return "-";
  }

  try {
    return new Date(value).toLocaleDateString("vi-VN");
  } catch {
    return value;
  }
}

function getStatusLabel(status) {
  switch (status) {
    case "APPROVAL_PENDING":
      return "Chờ duyệt";

    case "APPROVED":
      return "Đã duyệt";

    case "IN_PROGRESS":
      return "Đang diễn ra";

    case "COMPLETED":
      return "Hoàn thành";

    case "CANCELLED":
      return "Đã hủy";

    case "DRAFT":
      return "Đang cấu hình";

    default:
      return status || "-";
  }
}

function OperationTourTable({ tours, loading, onSelectCruise }) {
  if (loading) {
    return (
      <div className="operation-tour-table-state">
        Đang tải danh sách Tour...
      </div>
    );
  }

  if (!tours || tours.length === 0) {
    return (
      <div className="operation-tour-table-state empty">
        <CheckCircle size={40} />

        <h3>Không có Tour chờ duyệt</h3>

        <p>Hiện tại không có Tour nào đang chờ Operation xử lý.</p>
      </div>
    );
  }

  return (
    <div className="operation-tour-table-wrapper">
      <table className="operation-tour-table">
        <thead>
          <tr>
            <th>Tour</th>

            <th>Mã Tour</th>

            <th>Thời gian</th>

            <th>Du thuyền</th>

            <th>Trạng thái</th>

            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {tours.map((tour) => (
            <tr key={tour.id}>
              {/* TOUR */}
              <td>
                <div className="operation-tour-name">
                  <strong>{tour.name}</strong>

                  {tour.description && <span>{tour.description}</span>}
                </div>
              </td>

              {/* CODE */}
              <td>
                <span className="operation-tour-code">{tour.code}</span>
              </td>

              {/* DATE */}
              <td>
                <div className="operation-tour-dates">
                  <div>
                    <CalendarDays size={15} />

                    <span>{formatDate(tour.startDate)}</span>
                  </div>

                  <span className="date-separator">→</span>

                  <div>
                    <CalendarDays size={15} />

                    <span>{formatDate(tour.endDate)}</span>
                  </div>
                </div>
              </td>

              {/* CRUISE */}
              <td>
                {tour.cruise ? (
                  <div className="operation-tour-cruise">
                    <Ship size={16} />

                    <div>
                      <strong>{tour.cruise.name}</strong>

                      <span>{tour.cruise.code}</span>
                    </div>
                  </div>
                ) : (
                  <span className="operation-tour-no-cruise">Chưa gán</span>
                )}
              </td>

              {/* STATUS */}
              <td>
                <span
                  className={`operation-tour-status ${(
                    tour.statusTrip || ""
                  ).toLowerCase()}`}
                >
                  {getStatusLabel(tour.statusTrip)}
                </span>
              </td>

              {/* ACTION */}
              <td>
                <div className="operation-tour-actions">
                  <button
                    type="button"
                    className="operation-tour-approve-button"
                    title="Gán du thuyền và duyệt Tour"
                    onClick={() => onSelectCruise?.(tour)}
                  >
                    <Ship size={16} />

                    <span>Gán tàu & duyệt</span>
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

export default OperationTourTable;
