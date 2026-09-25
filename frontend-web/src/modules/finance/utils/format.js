// src/modules/finance/utils/format.js
export const formatDate = (value) => {
  if (!value) return "—";
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? value : d.toLocaleDateString("vi-VN");
};

export const formatDateTime = (value) => {
  if (!value) return "—";
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? value : d.toLocaleString("vi-VN");
};

export const formatMoney = (value) =>
  value == null
    ? "—"
    : new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(value);

// Dùng chung cho mọi bộ lọc tìm kiếm
export const matchesSearch = (term, ...fields) => {
  if (!term?.trim()) return true;
  const q = term.trim().toLowerCase();
  return fields.some((f) =>
    String(f ?? "")
      .toLowerCase()
      .includes(q),
  );
};
