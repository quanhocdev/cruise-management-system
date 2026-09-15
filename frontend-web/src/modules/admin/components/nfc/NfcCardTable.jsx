import React from "react";

export default function NfcCardTable({ cards, onEdit, onDelete }) {
  if (!cards || cards.length === 0) {
    return (
      <div
        style={{
          textAlign: "center",
          padding: "32px",
          color: "#6b7280",
          border: "1px dashed #d1d5db",
          borderRadius: "8px",
        }}
      >
        Không có dữ liệu thẻ NFC.
      </div>
    );
  }

  const getStatusBadge = (status) => {
    switch (status) {
      case "AVAILABLE":
        return <span className="badge badge-available">Có sẵn</span>;
      case "ASSIGNED":
        return <span className="badge badge-assigned">Đã gán</span>;
      case "INACTIVE":
        return <span className="badge badge-inactive">Không hoạt động</span>;
      default:
        return (
          <span
            className="badge"
            style={{ backgroundColor: "#f3f4f6", color: "#374151" }}
          >
            {status}
          </span>
        );
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    return date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
    <div className="nfc-table-responsive">
      <table className="nfc-table">
        <thead>
          <tr>
            <th>STT</th>
            <th>Mã UID</th>
            <th>Trạng thái</th>
            <th>Ngày tạo</th>
            <th>Cập nhật lần cuối</th>
            <th style={{ textAlign: "right" }}>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {cards.map((card, index) => (
            <tr key={card.id}>
              <td style={{ color: "#6b7280" }}>{index + 1}</td>
              <td style={{ fontWeight: 500, color: "#1f2937" }}>
                {card.cardUid}
              </td>
              <td>{getStatusBadge(card.status)}</td>
              <td style={{ color: "#6b7280" }}>{formatDate(card.createdAt)}</td>
              <td style={{ color: "#6b7280" }}>{formatDate(card.updatedAt)}</td>
              <td style={{ textAlign: "right" }}>
                <button
                  onClick={() => onEdit(card)}
                  className="btn-action-edit"
                  title="Chỉnh sửa"
                >
                  Sửa
                </button>
                <button
                  onClick={() => onDelete(card.id)}
                  className="btn-action-delete"
                  title="Xóa"
                >
                  Xóa
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
