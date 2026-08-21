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
