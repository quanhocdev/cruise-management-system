export const REFUND_STATUS_OPTIONS = [
  { value: "ALL", label: "Tất cả trạng thái" },
  { value: "PENDING", label: "Chờ xác thực" },
  { value: "APPROVED", label: "Đã duyệt" },
  { value: "PROCESSING", label: "Đang xử lý" },
  { value: "COMPLETED", label: "Đã hoàn tiền" },
  { value: "REJECTED", label: "Đã từ chối" },
  { value: "FAILED", label: "Hoàn tiền thất bại" },
];

export const REFUND_STATUS_LABELS = {
  PENDING: "Chờ xác thực",
  APPROVED: "Đã duyệt",
  PROCESSING: "Đang xử lý",
  COMPLETED: "Đã hoàn tiền",
  REJECTED: "Đã từ chối",
  FAILED: "Hoàn tiền thất bại",
};

export const getRefundStatusLabel = (status) =>
  REFUND_STATUS_LABELS[status] || status || "Không xác định";
