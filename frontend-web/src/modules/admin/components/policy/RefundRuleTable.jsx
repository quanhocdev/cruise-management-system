// src/modules/admin/components/policy/RefundRuleTable.jsx

import { Button, Spinner, Table } from "react-bootstrap";

import { formatDayRange } from "../../utils/refundRuleUtils";

export default function RefundRuleTable({
  rules,
  loading,
  disabled,
  onEdit,
  onDelete,
}) {
  if (loading) {
    return (
      <div className="refund-rule-state">
        <Spinner animation="border" />
        <span>Đang tải luật hoàn tiền...</span>
      </div>
    );
  }

  if (!rules || rules.length === 0) {
    return (
      <div className="refund-rule-state refund-rule-empty">
        <div className="refund-rule-empty-icon">↩️</div>
        <h5>Chưa có luật hoàn tiền</h5>
        <p>Hãy thêm luật hoàn tiền theo số ngày trước khi khởi hành.</p>
      </div>
    );
  }

  return (
    <div className="refund-rule-table-wrapper">
      <Table hover bordered className="refund-rule-table align-middle">
        <thead>
          <tr>
            <th>Số ngày trước khởi hành</th>
            <th>Hoàn tiền</th>
            <th>Mô tả</th>
            <th className="refund-rule-action-column">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {rules.map((rule) => (
            <tr key={rule.id}>
              <td data-label="Số ngày trước khởi hành">
                <strong>{formatDayRange(rule)}</strong>
              </td>

              <td data-label="Hoàn tiền">
                <span className="refund-rule-percent">
                  {Number(rule.refundPercentage).toLocaleString("vi-VN")}%
                </span>
              </td>

              <td data-label="Mô tả">
                <div className="refund-rule-description">
                  {rule.description || "-"}
                </div>
              </td>

              <td data-label="Thao tác">
                <div className="refund-rule-actions">
                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit(rule)}
                    disabled={disabled}
                  >
                    Sửa
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDelete(rule)}
                    disabled={disabled}
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
