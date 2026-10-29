import React, { useEffect } from "react";
import useOnboardAssignment from "../../hooks/useOnboardAssignment";

import {
  AssignmentStatus,
  AssignmentSummary,
  EmptyState,
  LoadingState,
} from "./OnboardAssignmentShared";

function OnboardStaffAssignmentModal({
  open,
  assignment,
  assignmentType,
  onClose,
}) {
  const {
    assignments,
    availableStaff,
    selectedUserIds,
    search,
    setSearch,
    loading,
    submitting,
    error,
    result,
    toggleStaff,
    assignSelectedStaff,

    cancelingId,
    cancelError,
    cancelSuccess,
    cancelAssignment,
  } = useOnboardAssignment({
    assignment,
    assignmentType,
    open,
  });

  useEffect(() => {
    if (!open) {
      setSearch("");
    }
  }, [open, setSearch]);

  if (!open || !assignment) {
    return null;
  }

  const handleSubmit = async () => {
    try {
      await assignSelectedStaff();
    } catch {
      // Error đã được hook xử lý.
    }
  };

  const handleCancelAssignment = async (item) => {
    if (item.status !== "PENDING") {
      return;
    }

    const staffName = item.fullName || item.username || "nhân viên này";

    const confirmed = window.confirm(
      `Bạn có chắc chắn muốn hủy phân công cho ${staffName}?`,
    );

    if (!confirmed) {
      return;
    }

    await cancelAssignment(item.id);
  };

  return (
    <div
      className="onboard-modal-overlay"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) {
          onClose();
        }
      }}
    >
      <div
        className="onboard-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="onboard-modal-title"
      >
        <div className="onboard-modal-header">
          <div>
            <span className="onboard-modal-eyebrow">
              {assignmentType === "PATROL"
                ? "PHÂN CÔNG TUẦN TRA"
                : "PHÂN CÔNG HOẠT ĐỘNG"}
            </span>

            <h2 id="onboard-modal-title">Phân công nhân viên ONBOARD</h2>

            <p>{assignment.title}</p>

            {assignment.subtitle && <small>{assignment.subtitle}</small>}
          </div>

          <button
            type="button"
            className="onboard-modal-close"
            onClick={onClose}
            aria-label="Đóng"
          >
            ×
          </button>
        </div>

        <div className="onboard-modal-body">
          {error && (
            <div className="onboard-alert onboard-alert-error">{error}</div>
          )}

          {cancelError && (
            <div className="onboard-alert onboard-alert-error">
              {cancelError}
            </div>
          )}

          {cancelSuccess && (
            <div className="onboard-alert onboard-alert-success">
              {cancelSuccess}
            </div>
          )}

          {result && (
            <div className="onboard-alert onboard-alert-success">
              Đã gửi yêu cầu phân công.
              {result.assigned?.length > 0 &&
                ` Thành công: ${result.assigned.length} nhân viên.`}
              {result.alreadyAssigned?.length > 0 &&
                ` Đã được phân công trước đó: ${result.alreadyAssigned.length}.`}
              {result.invalidUserIds?.length > 0 &&
                ` Không hợp lệ: ${result.invalidUserIds.length}.`}
            </div>
          )}

          <section className="onboard-modal-section">
            <div className="onboard-section-heading">
              <h3>Nhân viên đã phân công</h3>
              <span>{assignments.length} người</span>
            </div>

            <AssignmentSummary assignments={assignments} />

            {assignments.length > 0 ? (
              <div className="onboard-assigned-list">
                {assignments.map((item) => (
                  <div className="onboard-assigned-row" key={item.id}>
                    <div className="onboard-staff-identity">
                      <div className="onboard-avatar">
                        {(item.fullName || item.username || "N")
                          .charAt(0)
                          .toUpperCase()}
                      </div>

                      <div>
                        <strong>
                          {item.fullName || item.username || "Nhân viên"}
                        </strong>
                        <span>{item.email || item.phone || ""}</span>
                      </div>
                    </div>

                    <div className="onboard-assigned-actions">
                      <AssignmentStatus status={item.status} />

                      {item.status === "PENDING" && (
                        <button
                          type="button"
                          className="onboard-assignment-cancel"
                          onClick={() => handleCancelAssignment(item)}
                          disabled={Boolean(cancelingId) || submitting}
                          title="Hủy phân công"
                        >
                          {String(cancelingId) === String(item.id)
                            ? "Đang hủy..."
                            : "Hủy"}
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState>
                Chưa có nhân viên nào được phân công vào vị trí này.
              </EmptyState>
            )}
          </section>

          <section className="onboard-modal-section">
            <div className="onboard-section-heading">
              <h3>Chọn nhân viên mới</h3>
              <span>Đã chọn {selectedUserIds.length}</span>
            </div>

            <input
              type="text"
              className="onboard-search-input"
              placeholder="Tìm theo tên, username, email hoặc số điện thoại..."
              value={search}
              onChange={(event) => setSearch(event.target.value)}
            />

            {loading ? (
              <LoadingState>Đang tải nhân viên...</LoadingState>
            ) : availableStaff.length > 0 ? (
              <div className="onboard-staff-select-list">
                {availableStaff.map((staff) => {
                  const userId = staff.userId || staff.id;
                  const checked = selectedUserIds.includes(userId);

                  return (
                    <label
                      className={`onboard-staff-option ${
                        checked ? "selected" : ""
                      }`}
                      key={userId}
                    >
                      <input
                        type="checkbox"
                        checked={checked}
                        onChange={() => toggleStaff(userId)}
                      />

                      <div className="onboard-avatar">
                        {(staff.fullName || staff.username || "N")
                          .charAt(0)
                          .toUpperCase()}
                      </div>

                      <div className="onboard-staff-option-info">
                        <strong>{staff.fullName || staff.username}</strong>
                        <span>{staff.email}</span>
                        {staff.phone && <small>{staff.phone}</small>}
                      </div>
                    </label>
                  );
                })}
              </div>
            ) : (
              <EmptyState>
                Không tìm thấy nhân viên phù hợp hoặc tất cả nhân viên đã được
                phân công.
              </EmptyState>
            )}
          </section>
        </div>

        <div className="onboard-modal-footer">
          <button
            type="button"
            className="onboard-button onboard-button-outline"
            onClick={onClose}
          >
            Đóng
          </button>

          <button
            type="button"
            className="onboard-button onboard-button-primary"
            onClick={handleSubmit}
            disabled={submitting || loading || selectedUserIds.length === 0}
          >
            {submitting
              ? "Đang phân công..."
              : `Phân công ${selectedUserIds.length} nhân viên`}
          </button>
        </div>
      </div>
    </div>
  );
}

export default OnboardStaffAssignmentModal;
