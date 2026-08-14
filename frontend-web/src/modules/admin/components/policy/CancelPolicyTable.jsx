import { Button, Spinner, Table } from "react-bootstrap";

export default function CancelPolicyTable({
  rules,
  loading,
  onEdit,
  onDelete,
}) {
  const getStatusLabel = (status) => {
    return status === "ACTIVE" ? "Đang hoạt động" : "Ngừng hoạt động";
  };

  if (loading) {
    return (
      <div className="text-center py-4">
        <Spinner animation="border" size="sm" />

        <div className="mt-2">Đang tải chính sách hủy / hoàn tiền...</div>
      </div>
    );
  }

  if (!rules || rules.length === 0) {
    return (
      <div className="text-center py-4 text-muted">
        Chưa có mức hoàn tiền nào.
      </div>
    );
  }

  return (
    <Table responsive bordered hover className="policy-rule-table align-middle">
      <thead>
        <tr>
          <th>Số ngày trước khởi hành</th>

          <th>Hoàn tiền</th>

          <th>Trạng thái</th>

          <th className="text-center">Thao tác</th>
        </tr>
      </thead>

      <tbody>
        {rules.map((rule) => (
          <tr key={rule.id}>
            <td>
              <strong>{rule.daysBefore}</strong> ngày
            </td>

            <td>
              <strong className="text-primary">{rule.refundPercent}%</strong>
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
              <div className="d-flex justify-content-center gap-2">
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
  );
}
