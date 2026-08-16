import { Eye, Pencil, Trash2, CalendarDays, Send } from "lucide-react";

const getTripStatusLabel = (status) => {
  switch (status) {
    case "DRAFT":
      return "Đang cấu hình";
    case "APPROVAL_PENDING":
      return "Chờ duyệt";
    case "APPROVED":
      return "Đã được duyệt";
    case "IN_PROGRESS":
      return "Đang diễn ra";
    case "COMPLETED":
      return "Hoàn thành";
    case "CANCELLED":
      return "Đã hủy";
    default:
      return status || "-";
  }
};

const getTripStatusClass = (status) => {
  switch (status) {
    case "DRAFT":
      return "status-default";
    case "APPROVAL_PENDING":
      return "status-warning";
    case "APPROVED":
      return "status-success";
    case "IN_PROGRESS":
      return "status-info";
    case "COMPLETED":
      return "status-completed";
    case "CANCELLED":
      return "status-danger";
    default:
      return "status-default";
  }
};

const getBookingStatusLabel = (status) => {
  switch (status) {
    case "NOT_OPEN":
      return "Chưa mở";
    case "WAITING":
      return "Đang chờ";
    case "OPEN":
      return "Đang mở";
    case "CLOSED":
      return "Đã đóng";
    default:
      return status || "-";
  }
};

const getBookingStatusClass = (status) => {
  switch (status) {
    case "OPEN":
      return "status-success";
    case "WAITING":
      return "status-warning";
    case "CLOSED":
      return "status-default";
    default:
      return "status-muted";
  }
};

const formatDate = (date) => {
  if (!date) return "-";

  const [year, month, day] = date.split("-");

  return `${day}/${month}/${year}`;
};

function TourTable({
  tours = [],
  loading = false,
  onView,
  onEdit,
  onDelete,
  onManageSchedule,
  onSubmitForApproval,
}) {
  if (loading) {
    return (
      <div className="tour-table-state">
        <div className="tour-table-spinner" />

        <span>Đang tải danh sách tour...</span>
      </div>
    );
  }

  if (!tours.length) {
    return (
      <div className="tour-table-state">
        <CalendarDays size={42} />

        <h3>Chưa có tour nào</h3>

        <p>Hãy tạo tour đầu tiên để bắt đầu xây dựng lịch trình.</p>
      </div>
    );
  }

  return (
    <div className="tour-table-wrapper">
      <table className="tour-table">
        <thead>
          <tr>
            <th>Mã tour</th>
            <th>Tên tour</th>
            <th>Thời gian</th>
            <th>Trạng thái tour</th>
            <th>Đăng ký</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {tours.map((tour) => {
            const isDraft = tour.statusTrip === "DRAFT";
            const canManageSchedule =
              tour.statusTrip === "DRAFT" ||
              tour.statusTrip === "APPROVED" ||
              tour.statusTrip === "IN_PROGRESS";

            return (
              <tr key={tour.id}>
                {/* =====================================================
                    CODE
                   ===================================================== */}

                <td>
                  <span className="tour-code">{tour.code}</span>
                </td>

                {/* =====================================================
                    NAME
                   ===================================================== */}

                <td>
                  <div className="tour-name-cell">
                    <strong>{tour.name}</strong>

                    {tour.description && (
                      <span>
                        {tour.description.length > 70
                          ? `${tour.description.substring(0, 70)}...`
                          : tour.description}
                      </span>
                    )}
                  </div>
                </td>

                {/* =====================================================
                    DATE
                   ===================================================== */}

                <td>
                  <span>
                    {formatDate(tour.startDate)} → {formatDate(tour.endDate)}
                  </span>
                </td>

                {/* =====================================================
                    TRIP STATUS
                   ===================================================== */}

                <td>
                  <span
                    className={`tour-status ${getTripStatusClass(
                      tour.statusTrip,
                    )}`}
                  >
                    {getTripStatusLabel(tour.statusTrip)}
                  </span>
                </td>

                {/* =====================================================
                    BOOKING STATUS
                   ===================================================== */}

                <td>
                  <span
                    className={`tour-status ${getBookingStatusClass(
                      tour.statusBooking,
                    )}`}
                  >
                    {getBookingStatusLabel(tour.statusBooking)}
                  </span>
                </td>

                {/* =====================================================
                    ACTIONS
                   ===================================================== */}

                <td>
                  <div className="tour-table-actions">
                    {/* =================================================
                        VIEW
                        Tất cả status đều được xem
                       ================================================= */}

                    <button
                      type="button"
                      className="icon-button view"
                      title="Xem chi tiết"
                      onClick={() => onView?.(tour)}
                    >
                      <Eye size={17} />
                    </button>

                    {/* =================================================
                        SCHEDULE
                        DRAFT + APPROVED
                       ================================================= */}

                    {canManageSchedule && (
                      <button
                        type="button"
                        className="icon-button schedule"
                        title="Quản lý lịch trình"
                        onClick={() => onManageSchedule?.(tour)}
                      >
                        <CalendarDays size={17} />
                      </button>
                    )}

                    {/* =================================================
                        EDIT
                        Chỉ DRAFT
                       ================================================= */}

                    {isDraft && (
                      <button
                        type="button"
                        className="icon-button edit"
                        title="Chỉnh sửa"
                        onClick={() => onEdit?.(tour)}
                      >
                        <Pencil size={17} />
                      </button>
                    )}

                    {/* =================================================
                        DELETE
                        Chỉ DRAFT
                       ================================================= */}

                    {isDraft && (
                      <button
                        type="button"
                        className="icon-button delete"
                        title="Xóa tour"
                        onClick={() => onDelete?.(tour)}
                      >
                        <Trash2 size={17} />
                      </button>
                    )}

                    {/* =================================================
                        SUBMIT FOR APPROVAL
                        Chỉ DRAFT
                       ================================================= */}

                    {isDraft && (
                      <button
                        type="button"
                        className="icon-button submit"
                        title="Gửi Operation duyệt"
                        onClick={() => onSubmitForApproval?.(tour)}
                      >
                        <Send size={17} />
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

export default TourTable;
