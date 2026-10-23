export const formatMoney = (value) =>
  `${Number(value || 0).toLocaleString("vi-VN")} đ`;

export const formatDateTime = (value) => {
  if (!value) return "—";
  const d = new Date(value);
  if (isNaN(d.getTime())) return value;
  return d.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

// Backend có thể trả trạng thái hành trình ở 2 vị trí khác nhau
export const getTripStatus = (booking) =>
  booking.tourStatusTrip || booking.tour?.statusTrip || null;

export const getTourName = (booking) =>
  booking.tourName || booking.tour?.name || "Hành trình du thuyền";
