// src/modules/scheduler/utils/vesselFormat.js
export const STATUS_LABEL = {
  READY: "Sẵn sàng",
  IN_PROGRESS: "Đang hành trình",
  COMPLETED: "Đã hoàn thành",
};

export const fmtDate = (v) =>
  v
    ? new Date(v).toLocaleDateString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
      })
    : "";

export const fmtDateTime = (v) =>
  v
    ? new Date(v).toLocaleString("vi-VN", {
        hour: "2-digit",
        minute: "2-digit",
        day: "2-digit",
        month: "2-digit",
      })
    : "";

export const fmtCoord = (value, pos, neg) =>
  `${Math.abs(value).toFixed(4)}° ${value >= 0 ? pos : neg}`;

export const minutesAgo = (v) =>
  v
    ? Math.max(0, Math.round((Date.now() - new Date(v).getTime()) / 60000))
    : null;
