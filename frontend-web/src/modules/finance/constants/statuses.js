// src/modules/finance/constants/statuses.js
export const ACTIVE_STATUS_OPTIONS = [
  { value: "ACTIVE", label: "Đang khai thác" },
  { value: "INACTIVE", label: "Ngưng khai thác" },
];

export const SCHEDULE_STATUS_OPTIONS = ACTIVE_STATUS_OPTIONS;

export const ROOM_STATUS_OPTIONS = ACTIVE_STATUS_OPTIONS;
export const DECK_STATUS_OPTIONS = ACTIVE_STATUS_OPTIONS;
export const AREA_STATUS_OPTIONS = ACTIVE_STATUS_OPTIONS;
export const CRUISE_STATUS_OPTIONS = ACTIVE_STATUS_OPTIONS;

// NfcCardStatus
export const NFC_STATUS_OPTIONS = [
  { value: "AVAILABLE", label: "Sẵn sàng" },
  { value: "ASSIGNED", label: "Đã gán hành khách" },
  { value: "INACTIVE", label: "Đã hủy" },
];

// TourStatusTrip
export const TOUR_STATUS_TRIP_LABELS = {
  DRAFT: "Nháp",
  APPROVAL_PENDING: "Chờ duyệt",
  APPROVED: "Đã duyệt",
  READY: "Sẵn sàng",
  IN_PROGRESS: "Đang diễn ra",
  COMPLETED: "Hoàn thành",
  CANCELLED: "Đã hủy",
};

export const FINANCE_VISIBLE_TOUR_STATUSES = [
  "READY",
  "IN_PROGRESS",
  "COMPLETED",
];

// Thêm vào src/modules/finance/constants/statuses.js
export const BOOKING_STATUS_OPTIONS = [
  { value: "PENDING_PAYMENT", label: "Chờ thanh toán" },
  { value: "CONFIRMED", label: "Đã xác nhận" },
  { value: "CANCELLED", label: "Đã hủy" },
];

// Helper dùng chung cho mọi cột trạng thái
export const labelOf = (options, value) =>
  options.find((o) => o.value === value)?.label ?? value ?? "—";
