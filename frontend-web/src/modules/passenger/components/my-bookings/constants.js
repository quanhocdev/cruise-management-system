// Trạng thái đơn vé (booking.status)
export const BOOKING_STATUS = {
  PENDING_PAYMENT: {
    label: "Chờ thanh toán",
    tone: "pending",
  },
  CONFIRMED: {
    label: "Đã xác nhận",
    tone: "confirmed",
  },
  PENDING_REFUND: {
    label: "Chờ hoàn tiền",
    tone: "pending-refund",
  },
  REFUND_PROCESSING: {
    label: "Đang xử lý hoàn tiền",
    tone: "refund-processing",
  },
  REFUNDED: {
    label: "Đã hoàn tiền",
    tone: "refunded",
  },
  CANCELLED: {
    label: "Đã hủy",
    tone: "cancelled",
  },
};

// Trạng thái hành trình (statusTrip của tour)
export const TRIP_STATUS = {
  READY: "Sắp diễn ra",
  IN_PROGRESS: "Đang diễn ra",
  COMPLETED: "Đã hoàn thành",
};

// Bộ lọc trạng thái đơn vé
export const BOOKING_FILTERS = [
  { value: "ALL", label: "Tất cả" },
  {
    value: "PENDING_PAYMENT",
    label: BOOKING_STATUS.PENDING_PAYMENT.label,
  },
  {
    value: "CONFIRMED",
    label: BOOKING_STATUS.CONFIRMED.label,
  },
  {
    value: "PENDING_REFUND",
    label: BOOKING_STATUS.PENDING_REFUND.label,
  },
  {
    value: "REFUND_PROCESSING",
    label: BOOKING_STATUS.REFUND_PROCESSING.label,
  },
  {
    value: "REFUNDED",
    label: BOOKING_STATUS.REFUNDED.label,
  },
  {
    value: "CANCELLED",
    label: BOOKING_STATUS.CANCELLED.label,
  },
];

// Bộ lọc trạng thái hành trình
export const TRIP_FILTERS = [
  { value: "ALL", label: "Tất cả" },
  { value: "READY", label: TRIP_STATUS.READY },
  { value: "IN_PROGRESS", label: TRIP_STATUS.IN_PROGRESS },
  { value: "COMPLETED", label: TRIP_STATUS.COMPLETED },
];
