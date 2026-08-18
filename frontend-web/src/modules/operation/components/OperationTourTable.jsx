// src/modules/operation/components/OperationTourTable.jsx

import { CheckCircle, Ship, CalendarDays, Eye, XCircle } from "lucide-react";
import "../styles/OperationTourTable.css";
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

function OperationTourTable({
  tours,
  loading,
  mode = "pending",

  onSelectCruise,
  onApprove,
  onReject,
  onView,
  onAssignArea,
}) {
  const isPending = mode === "pending";

  // =====================================================
  // LOADING
  // =====================================================

  if (loading) {
    return (
      <div className="operation-tour-table-state">
        Đang tải danh sách Tour...
      </div>
    );
  }

  // =====================================================
  // EMPTY
  // =====================================================

  if (!tours || tours.length === 0) {
    return (
      <div className="operation-tour-table-state empty">
        <CheckCircle size={40} />

        <h3>
          {isPending ? "Không có Tour chờ duyệt" : "Không có Tour đã duyệt"}
        </h3>

        <p>
          {isPending
            ? "Hiện tại không có Tour nào đang chờ Operation xử lý."
            : "Hiện tại chưa có Tour nào được Operation duyệt."}
        </p>
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

            <th>Phân công</th>

            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {tours.map((tour) => {
            const hasCruise = !!tour.cruise;

            return (
              <tr key={tour.id}>
                {/* =================================================
                    TOUR
                    ================================================= */}

                <td>
                  <div className="operation-tour-name">
                    <strong>{tour.name}</strong>

                    {tour.description && <span>{tour.description}</span>}
                  </div>
                </td>

                {/* =================================================
                    CODE
                    ================================================= */}

                <td>
                  <span className="operation-tour-code">
                    {tour.code || "-"}
                  </span>
                </td>

                {/* =================================================
                    DATE
                    ================================================= */}

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

                {/* =================================================
                    CRUISE
                    ================================================= */}

                <td>
                  {hasCruise ? (
                    <div className="operation-tour-cruise">
                      <Ship size={16} />

                      <div>
                        <strong>{tour.cruise.name}</strong>

                        <span>{tour.cruise.code}</span>
                      </div>

                      {/* Chỉ Tour chờ duyệt mới được đổi tàu */}
                      {isPending && (
                        <button
                          type="button"
                          className="operation-tour-change-cruise-button"
                          onClick={() => onSelectCruise?.(tour)}
                        >
                          Thay đổi
                        </button>
                      )}
                    </div>
                  ) : (
                    <button
                      type="button"
                      className="operation-tour-assign-cruise-button"
                      onClick={() => isPending && onSelectCruise?.(tour)}
                      disabled={!isPending}
                    >
                      <Ship size={16} />

                      <span>Gán du thuyền</span>
                    </button>
                  )}
                </td>

                {/* =================================================
                    ASSIGNMENT
                    ================================================= */}

                <td>
                  <button
                    type="button"
                    className="operation-tour-assignment-button"
                    onClick={() => onAssignArea?.(tour)}
                    disabled={!tour.cruise}
                  >
                    {tour.cruise ? "Phân công khu vực" : "Chưa có du thuyền"}
                  </button>
                </td>

                {/* =================================================
                    ACTION
                    ================================================= */}

                <td>
                  <div className="operation-tour-actions">
                    {isPending ? (
                      <>
                        <button
                          type="button"
                          className="operation-tour-approve-button"
                          onClick={() => onApprove?.(tour)}
                          title="Duyệt Tour"
                        >
                          <CheckCircle size={16} />
                          <span>Duyệt</span>
                        </button>

                        <button
                          type="button"
                          className="operation-tour-reject-button"
                          onClick={() => onReject?.(tour)}
                        >
                          <XCircle size={16} />

                          <span>Từ chối</span>
                        </button>
                      </>
                    ) : (
                      <button
                        type="button"
                        className="operation-tour-view-button"
                        onClick={() => onView?.(tour)}
                      >
                        <Eye size={16} />

                        <span>Xem</span>
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

export default OperationTourTable;
