// src/modules/admin/components/policy/BookingPolicyTable.jsx

import { Button, Spinner, Table } from "react-bootstrap";

export default function BookingPolicyTable({
  rules,
  loading,
  onEdit,
  onDelete,
}) {
  if (loading) {
    return (
      <div className="policy-rule-table-loading">
        <Spinner animation="border" />
        <span>Đang tải quy tắc đặt tour...</span>
      </div>
    );
  }

  if (!rules || rules.length === 0) {
    return (
      <div className="policy-rule-table-empty">
        <div className="policy-rule-empty-icon">📅</div>

        <h5>Chưa có quy tắc đặt tour</h5>

        <p>Hãy thêm quy tắc giảm giá theo số ngày trước khi khởi hành.</p>
      </div>
    );
  }

  const getStatusLabel = (status) => {
    switch (status) {
      case "ACTIVE":
        return "Đang hoạt động";

      case "INACTIVE":
        return "Ngừng hoạt động";

      default:
        return status || "-";
    }
  };

  return (
    <div className="policy-rule-table-wrapper">
      <Table
        responsive
        hover
        bordered
        className="policy-rule-table align-middle"
      >
        <thead>
          <tr>
            <th>Số ngày trước khởi hành</th>

            <th>Giảm giá</th>

            <th>Trạng thái</th>

            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {rules.map((rule) => (
            <tr key={rule.id}>
              <td>
                <strong>{rule.daysBeforeDeparture}</strong> ngày
              </td>

              <td>
                <strong>
                  {Number(rule.discountPercent).toLocaleString("vi-VN")}%
                </strong>
              </td>

              <td>
                <span
                  className={`policy-status-badge ${
                    rule.status === "ACTIVE"
                      ? "policy-status-active"
                      : "policy-status-inactive"
                  }`}
                >
                  {getStatusLabel(rule.status)}
                </span>
              </td>

              <td>
                <div className="policy-rule-table-actions">
                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit(rule)}
                  >
                    Sửa
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDelete(rule)}
                  >
                    Xóa
                  </Button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
}
