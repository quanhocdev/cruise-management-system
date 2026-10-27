import React from "react";

export const ASSIGNMENT_STATUS = {
  PENDING: {
    label: "Chờ xác nhận",
    className: "pending",
  },
  ACCEPTED: {
    label: "Đã chấp nhận",
    className: "accepted",
  },
  REJECTED: {
    label: "Đã từ chối",
    className: "rejected",
  },
  COMPLETED: {
    label: "Hoàn thành",
    className: "completed",
  },
  CANCELLED: {
    label: "Đã hủy",
    className: "cancelled",
  },
};

export function AssignmentStatus({ status }) {
  const config = ASSIGNMENT_STATUS[status] || {
    label: status || "Không xác định",
    className: "unknown",
  };

  return (
    <span className={`onboard-status onboard-status-${config.className}`}>
      {config.label}
    </span>
  );
}

export function EmptyState({ children }) {
  return (
    <div className="onboard-empty-state">
      <div className="onboard-empty-icon">📋</div>
      <p>{children}</p>
    </div>
  );
}

export function LoadingState({ children = "Đang tải dữ liệu..." }) {
  return (
    <div className="onboard-loading-state">
      <span className="onboard-spinner" />
      <span>{children}</span>
    </div>
  );
}

export function AssignmentSummary({ assignments = [] }) {
  const total = assignments.length;

  const accepted = assignments.filter(
    (item) => item.status === "ACCEPTED",
  ).length;

  const pending = assignments.filter(
    (item) => item.status === "PENDING",
  ).length;

  const completed = assignments.filter(
    (item) => item.status === "COMPLETED",
  ).length;

  return (
    <div className="onboard-assignment-summary">
      <div>
        <span>Tổng nhân viên</span>
        <strong>{total}</strong>
      </div>

      <div>
        <span>Chờ xác nhận</span>
        <strong>{pending}</strong>
      </div>

      <div>
        <span>Đã chấp nhận</span>
        <strong>{accepted}</strong>
      </div>

      <div>
        <span>Hoàn thành</span>
        <strong>{completed}</strong>
      </div>
    </div>
  );
}
