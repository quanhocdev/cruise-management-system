export const formatCurrency = (value) =>
  `${Number(value || 0).toLocaleString("vi-VN")} đ`;
