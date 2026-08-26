// src/modules/operation/utils/tourConfigurationUtils.js

export const isActivityConfigured = (item) =>
  item?.activityCruiseId != null &&
  item?.startTime != null &&
  item?.endTime != null &&
  item?.maxPassengers != null &&
  Number(item.maxPassengers) > 0 &&
  item?.price != null &&
  Number(item.price) >= 0;

export const isServiceConfigured = (item) =>
  item?.serviceId != null &&
  item?.maxPassengers != null &&
  Number(item.maxPassengers) > 0;

export const isProductConfigured = (item) =>
  item?.productId != null &&
  item?.quantity != null &&
  Number(item.quantity) > 0;

export const formatVND = (price) => {
  if (price == null || price === "") return "—";
  return `${Number(price).toLocaleString("vi-VN")} ₫`;
};

export const TOUR_STATUS_META = {
  WAITING_CONFIG: { label: "Chờ cấu hình", className: "pending" },
  CONFIGURED: { label: "Đã cấu hình", className: "configured" },
  NOT_STARTED: { label: "Chưa bắt đầu", className: "not-started" },
  IN_PROGRESS: { label: "Đang diễn ra", className: "in-progress" },
  COMPLETED: { label: "Đã hoàn thành", className: "completed" },
  OUT_OF_STOCK: { label: "Hết hàng", className: "danger" },
  DELAYED: { label: "Trì hoãn", className: "danger" },
  CANCELLED: { label: "Đã hủy", className: "danger" },
};

/** Một item được coi là "đã cấu hình" khi không còn ở trạng thái chờ cấu hình. */
export const isTourItemConfigured = (status) => {
  if (!status) return false;

  return status !== "WAITING_CONFIG";
};

/** Lấy nhãn + className hiển thị cho 1 status bất kỳ, fallback an toàn nếu status lạ. */
export const getTourStatusMeta = (status) =>
  TOUR_STATUS_META[status] || { label: status || "—", className: "pending" };
