// src/modules/convenience/components/tour-config/ConvenienceStaffAssignmentModal.jsx
import { useMemo, useState } from "react";
import {
  X,
  Users,
  Search,
  UserPlus,
  RefreshCw,
  AlertCircle,
  CheckCircle2,
  Clock3,
  XCircle,
  Ban,
  Info,
} from "lucide-react";

import useConvenienceStaffAssignment, {
  getUserId,
} from "../../hooks/useConvenienceStaffAssignment";

import "../../styles/tour-config/ConvenienceStaffAssignmentModal.css";

const STATUS_LABELS = {
  PENDING: "Chờ xác nhận",
  ACCEPTED: "Đã chấp nhận",
  REJECTED: "Từ chối",
  COMPLETED: "Hoàn thành",
  CANCELLED: "Đã hủy",
};

const STATUS_ICONS = {
  PENDING: Clock3,
  ACCEPTED: CheckCircle2,
  REJECTED: XCircle,
  COMPLETED: CheckCircle2,
  CANCELLED: Ban,
};

function formatShortId(value) {
  if (!value) return "—";
  const id = String(value);
  return id.length > 8 ? `${id.slice(0, 8)}...` : id;
}
function getStaffName(staff) {
  return (
    staff?.fullName ||
    staff?.username ||
    staff?.email ||
    formatShortId(getUserId(staff))
  );
}

function getAssignedName(item) {
  return (
    item?.fullName ||
    item?.username ||
    item?.email ||
    formatShortId(item?.userId)
  );
}

function ConvenienceStaffAssignmentModal({
  assignment,
  assignmentType,
  onClose,
}) {
  const [cancelNotice, setCancelNotice] = useState("");

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
    reload,
  } = useConvenienceStaffAssignment({
    assignment,
    assignmentType,
  });

  const isProduct = assignmentType === "PRODUCT";

  const assignedCount = assignments.length;

  const selectedCount = selectedUserIds.length;

  const assignmentTitle = isProduct
    ? "Phân công nhân viên sản phẩm"
    : "Phân công nhân viên dịch vụ";

  const assignedRows = useMemo(
    () =>
      assignments.map((item) => {
        const StatusIcon = STATUS_ICONS[item.status] || Clock3;

        return {
          ...item,
          StatusIcon,
        };
      }),
    [assignments],
  );

  if (!assignment) return null;

  const handleClose = () => {
    if (submitting) return;
    onClose?.();
  };

  const handleAssign = async () => {
    await assignSelectedStaff();
  };

  return (
    <div
      className="convenience-assignment-overlay"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !submitting) {
          handleClose();
        }
      }}
    >
      <div className="convenience-assignment-modal">
        <div className="convenience-assignment-header">
          <div className="convenience-assignment-header-icon">
            <Users size={21} />
          </div>

          <div className="convenience-assignment-header-content">
            <h2>{assignmentTitle}</h2>
            <p>Quản lý nhân viên Convenience phụ trách đối tượng trong Tour.</p>
          </div>

          <button
            type="button"
            className="convenience-assignment-close"
            onClick={handleClose}
            disabled={submitting}
            title="Đóng"
          >
            <X size={20} />
          </button>
        </div>

        <div className="convenience-assignment-info">
          <div>
            <span>Tour</span>
            <strong>
              {assignment.tourCode ||
                assignment.tourName ||
                formatShortId(assignment.tourId)}
            </strong>
          </div>

          <div>
            <span>Khu vực</span>
            <strong>{assignment.cruiseAreaName || "—"}</strong>
          </div>

          <div>
            <span>{isProduct ? "Sản phẩm" : "Dịch vụ"}</span>
            <strong>
              {isProduct
                ? assignment.productName || assignment.name || "—"
                : assignment.serviceName || assignment.name || "—"}
            </strong>
          </div>
        </div>

        {error && (
          <div className="convenience-assignment-error">
            <AlertCircle size={17} />
            <span>{error}</span>
          </div>
        )}

        {result && (
          <div className="convenience-assignment-result">
            <CheckCircle2 size={17} />
            <div>
              <strong>Đã xử lý yêu cầu phân công.</strong>
              <p>
                Thành công: {result.assigned?.length ?? 0} nhân viên. Đã được
                phân công trước đó: {result.alreadyAssigned?.length ?? 0}. Không
                hợp lệ: {result.invalidUserIds?.length ?? 0}.
              </p>
            </div>
          </div>
        )}

        <div className="convenience-assignment-body">
          <section className="convenience-assignment-section">
            <div className="convenience-assignment-section-heading">
              <div>
                <h3>
                  <UserPlus size={17} />
                  Thêm nhân viên
                </h3>
                <p>Chọn nhân viên Convenience cần phụ trách.</p>
              </div>

              <span className="convenience-assignment-count">
                Đã chọn: {selectedCount}
              </span>
            </div>

            <div className="convenience-assignment-search">
              <Search size={17} />
              <input
                type="text"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                placeholder="Tìm theo tên, username hoặc email..."
              />
            </div>

            {loading ? (
              <div className="convenience-assignment-loading">
                <RefreshCw
                  size={20}
                  className="convenience-assignment-spinner"
                />
                <span>Đang tải danh sách nhân viên...</span>
              </div>
            ) : availableStaff.length === 0 ? (
              <div className="convenience-assignment-empty">
                <Users size={25} />
                <strong>Không có nhân viên phù hợp</strong>
                <span>
                  Có thể không còn nhân viên để phân công hoặc không tìm thấy
                  kết quả phù hợp.
                </span>
              </div>
            ) : (
              <div className="convenience-assignment-staff-list">
                {availableStaff.map((staff) => {
                  const userId = getUserId(staff);
                  const checked = selectedUserIds.includes(userId);

                  return (
                    <label
                      key={userId}
                      className={`convenience-assignment-staff-card ${
                        checked ? "selected" : ""
                      }`}
                    >
                      <input
                        type="checkbox"
                        checked={checked}
                        onChange={() => toggleStaff(userId)}
                        disabled={submitting}
                      />

                      <div className="convenience-assignment-avatar">
                        {staff.avatarUrl ? (
                          <img
                            src={staff.avatarUrl}
                            alt={getStaffName(staff)}
                          />
                        ) : (
                          <Users size={19} />
                        )}
                      </div>

                      <div className="convenience-assignment-staff-info">
                        <strong>{getStaffName(staff)}</strong>
                        <span>{staff.email || staff.username || "—"}</span>
                        {staff.phone && <small>{staff.phone}</small>}
                      </div>
                    </label>
                  );
                })}
              </div>
            )}

            <div className="convenience-assignment-form-actions">
              <button
                type="button"
                className="convenience-assignment-refresh"
                onClick={reload}
                disabled={loading || submitting}
              >
                <RefreshCw size={15} />
                Làm mới
              </button>

              <button
                type="button"
                className="convenience-assignment-submit"
                onClick={handleAssign}
                disabled={loading || submitting || selectedCount === 0}
              >
                {submitting ? (
                  <>
                    <RefreshCw
                      size={16}
                      className="convenience-assignment-spinner"
                    />
                    Đang phân công...
                  </>
                ) : (
                  <>
                    <UserPlus size={16} />
                    Phân công ({selectedCount})
                  </>
                )}
              </button>
            </div>
          </section>

          <section className="convenience-assignment-section assigned-section">
            <div className="convenience-assignment-section-heading">
              <div>
                <h3>
                  <Users size={17} />
                  Nhân viên đã phân công
                </h3>
                <p>Danh sách nhân viên hiện đang được gán cho đối tượng.</p>
              </div>

              <span className="convenience-assignment-count">
                {assignedCount} nhân viên
              </span>
            </div>

            {loading ? (
              <div className="convenience-assignment-loading">
                <RefreshCw
                  size={20}
                  className="convenience-assignment-spinner"
                />
                <span>Đang tải...</span>
              </div>
            ) : assignedRows.length === 0 ? (
              <div className="convenience-assignment-empty compact">
                <Users size={23} />
                <strong>Chưa có nhân viên được phân công</strong>
              </div>
            ) : (
              <div className="convenience-assignment-assigned-list">
                {assignedRows.map((item) => {
                  const StatusIcon = item.StatusIcon;

                  return (
                    <div
                      className="convenience-assignment-assigned-card"
                      key={item.id}
                    >
                      <div className="convenience-assignment-avatar">
                        {item.avatarUrl ? (
                          <img
                            src={item.avatarUrl}
                            alt={getAssignedName(item)}
                          />
                        ) : (
                          <Users size={19} />
                        )}
                      </div>

                      <div className="convenience-assignment-staff-info">
                        <strong>{getAssignedName(item)}</strong>
                        <span>{item.email || item.username || "—"}</span>
                      </div>

                      <span
                        className={`convenience-assignment-status status-${(
                          item.status || "PENDING"
                        ).toLowerCase()}`}
                      >
                        <StatusIcon size={13} />
                        {STATUS_LABELS[item.status] ||
                          item.status ||
                          "Chờ xác nhận"}
                      </span>

                      <button
                        type="button"
                        className="convenience-assignment-cancel"
                        onClick={() =>
                          setCancelNotice(
                            "Chức năng hủy phân công chưa được kết nối với backend.",
                          )
                        }
                        title="Hủy phân công"
                      >
                        Hủy
                      </button>
                    </div>
                  );
                })}
              </div>
            )}

            {cancelNotice && (
              <div className="convenience-assignment-info-notice">
                <Info size={15} />
                <span>{cancelNotice}</span>
                <button
                  type="button"
                  onClick={() => setCancelNotice("")}
                  aria-label="Đóng thông báo"
                >
                  <X size={14} />
                </button>
              </div>
            )}
          </section>
        </div>

        <div className="convenience-assignment-footer">
          <span>
            <Info size={14} />
            Nhân viên đã được phân công sẽ không xuất hiện trong danh sách chọn
            thêm.
          </span>

          <button type="button" onClick={handleClose} disabled={submitting}>
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}

export default ConvenienceStaffAssignmentModal;
