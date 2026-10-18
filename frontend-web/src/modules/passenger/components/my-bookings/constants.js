// Trạng thái đơn vé (booking.status)
export const BOOKING_STATUS = {
  PENDING_PAYMENT: { label: "Chờ thanh toán", tone: "pending" },
  CONFIRMED: { label: "Đã xác nhận", tone: "confirmed" },
  CANCELLED: { label: "Đã hủy", tone: "cancelled" },
};

// Trạng thái hành trình (statusTrip của tour)
export const TRIP_STATUS = {
  READY: "Sắp diễn ra",
  IN_PROGRESS: "Đang diễn ra",
  COMPLETED: "Đã hoàn thành",
};

export const BOOKING_FILTERS = [
  { value: "ALL", label: "Tất cả" },
  { value: "PENDING_PAYMENT", label: BOOKING_STATUS.PENDING_PAYMENT.label },
  { value: "CONFIRMED", label: BOOKING_STATUS.CONFIRMED.label },
  { value: "CANCELLED", label: BOOKING_STATUS.CANCELLED.label },
];

export const TRIP_FILTERS = [
  { value: "ALL", label: "Tất cả" },
  { value: "READY", label: TRIP_STATUS.READY },
  { value: "IN_PROGRESS", label: TRIP_STATUS.IN_PROGRESS },
  { value: "COMPLETED", label: TRIP_STATUS.COMPLETED },
];
