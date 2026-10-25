// src/modules/shore/components/visit-tour/ShoreStaffAssignment.jsx
// (FILE MỚI) Phần "Phân công nhân viên Shore" trong VisitTourFormModal.
// Chỉ render, data lấy từ useShoreStaffAssignment (truyền qua props).
// CSS: các rule `.shore-staff-*` đang nằm trong styles/VisitTourFormModal.css,
//      bạn có thể cắt sang styles/ShoreStaffAssignment.css rồi import ở đây.

import { AlertCircle, Info, RefreshCw, UserRound, Users } from "lucide-react";

function ShoreStaffAssignment({
  isEdit,
  disabled,
  loading,
  error,
  staffList,
  availableStaff,
  assignments,
  selectedUserIds,
  onToggle,
  onRetry,
}) {
  return (
    <section className="shore-staff-assignment">
      <div className="shore-staff-assignment-heading">
        <Users size={20} />
        <div>
          <h3>Phân công nhân viên Shore</h3>
          <p>
            Chọn nhân viên phụ trách Visit Tour này. Có thể chọn nhiều người.
          </p>
        </div>
      </div>

      {!isEdit && (
        <div className="shore-staff-info">
          <Info size={15} />
          <span>
            Visit Tour sẽ được tạo trước, sau đó hệ thống lưu phân công nhân
            viên.
          </span>
        </div>
      )}

      {error && (
        <div className="shore-staff-error">
          <AlertCircle size={16} />
          <span>{error}</span>
          <button
            type="button"
            disabled={loading || disabled}
            onClick={onRetry}
          >
            Thử lại
          </button>
        </div>
      )}

      {isEdit && (
        <div className="shore-staff-current">
          <h4>Đã được phân công ({assignments.length})</h4>

          {loading ? (
            <p className="shore-staff-muted">
              <RefreshCw size={15} className="shore-staff-spinner" />
              Đang tải phân công...
            </p>
          ) : assignments.length === 0 ? (
            <p className="shore-staff-muted">
              Chưa có nhân viên được phân công.
            </p>
          ) : (
            <div className="shore-staff-list">
              {assignments.map((a) => (
                <div className="shore-staff-current-item" key={a.id}>
                  <UserRound size={19} />

                  <div className="shore-staff-details">
                    <strong>{a.fullName || a.username || a.userId}</strong>
                    <span>{a.email || "Không có email"}</span>
                  </div>

                  <span
                    className={`shore-staff-status shore-staff-status-${String(
                      a.status || "PENDING",
                    ).toLowerCase()}`}
                  >
                    {a.status || "PENDING"}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      <div className="shore-staff-available">
        <h4>
          Chọn nhân viên bổ sung
          {selectedUserIds.length > 0 && ` (${selectedUserIds.length} đã chọn)`}
        </h4>

        {loading ? (
          <p className="shore-staff-muted">
            <RefreshCw size={15} className="shore-staff-spinner" />
            Đang tải danh sách nhân viên...
          </p>
        ) : error && staffList.length === 0 ? (
          <p className="shore-staff-muted">
            Không thể tải danh sách. Bạn có thể lưu cấu hình Visit Tour rồi thử
            tải nhân viên lại.
          </p>
        ) : availableStaff.length === 0 ? (
          <p className="shore-staff-muted">
            {staffList.length === 0
              ? "Chưa có nhân viên SHORE_STAFF nào trong danh sách."
              : "Không còn nhân viên chưa được phân công."}
          </p>
        ) : (
          <div className="shore-staff-list">
            {availableStaff.map((staff) => {
              const checked = selectedUserIds.includes(staff.userId);

              return (
                <label
                  key={staff.userId}
                  className={`shore-staff-option ${checked ? "selected" : ""}`}
                >
                  <input
                    type="checkbox"
                    checked={checked}
                    disabled={disabled}
                    onChange={() => onToggle(staff.userId)}
                  />

                  <UserRound size={19} />

                  <div className="shore-staff-details">
                    <strong>
                      {staff.fullName || staff.username || staff.userId}
                    </strong>
                    <span>{staff.email || "Không có email"}</span>
                    {staff.phone && <span>{staff.phone}</span>}
                  </div>
                </label>
              );
            })}
          </div>
        )}
      </div>

      {selectedUserIds.length > 0 && (
        <p className="shore-staff-selection-summary">
          Đã chọn thêm {selectedUserIds.length} nhân viên cho Visit Tour.
        </p>
      )}
    </section>
  );
}

export default ShoreStaffAssignment;
