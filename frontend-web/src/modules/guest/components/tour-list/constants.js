// Trạng thái hành trình (statusTrip)
export const TRIP_STATUS = {
  READY: { label: "Sắp diễn ra", tone: "ready" },
  IN_PROGRESS: { label: "Đang diễn ra", tone: "progress" },
  COMPLETED: { label: "Đã hoàn thành", tone: "done" },
};

// Trạng thái mở bán vé (statusBooking)
export const BOOKING_STATUS = {
  OPEN: { label: "Đang mở bán", tone: "open" },
  WAITING: { label: "Sắp mở bán", tone: "waiting" },
  NOT_OPEN: { label: "Chưa mở bán", tone: "waiting" },
  CLOSED: { label: "Đã đóng", tone: "closed" },
};

export const TRIP_FILTERS = [
  { value: "ALL", label: "Tất cả" },
  { value: "READY", label: TRIP_STATUS.READY.label },
  { value: "IN_PROGRESS", label: TRIP_STATUS.IN_PROGRESS.label },
  { value: "COMPLETED", label: TRIP_STATUS.COMPLETED.label },
];

export const BOOKING_FILTERS = [
  { value: "ALL", label: "Tất cả" },
  { value: "OPEN", label: BOOKING_STATUS.OPEN.label },
  { value: "WAITING", label: BOOKING_STATUS.WAITING.label },
  { value: "NOT_OPEN", label: BOOKING_STATUS.NOT_OPEN.label },
  { value: "CLOSED", label: BOOKING_STATUS.CLOSED.label },
];
