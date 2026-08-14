import { Button, Spinner, Table } from "react-bootstrap";

export default function PolicyTable({
  policies,
  loading,
  onEdit,
  onDelete,
  onManageRules,
}) {
  const getTypeLabel = (type) => {
    if (type === "BOOKING") {
      return "Đăng ký";
    }

    if (type === "CANCEL") {
      return "Hủy / hoàn tiền";
    }

    return type || "-";
  };

  const getStatusLabel = (status) => {
    if (status === "ACTIVE") {
      return "Đang hoạt động";
    }

    if (status === "INACTIVE") {
      return "Ngừng hoạt động";
    }

    return status || "-";
  };

  const formatDate = (value) => {
    if (!value) {
      return "-";
    }

    return new Date(value).toLocaleString("vi-VN");
  };

  if (loading) {
    return (
      <div className="policy-table-loading text-center py-5">
        <Spinner animation="border" />
        <div className="mt-2">Đang tải chính sách...</div>
      </div>
    );
  }

  if (!policies || policies.length === 0) {
    return (
      <div className="policy-table-empty text-center py-5">
        <div className="mb-2">Chưa có chính sách nào.</div>

        <small className="text-muted">
          Hãy tạo chính sách đăng ký hoặc hủy / hoàn tiền.
        </small>
      </div>
    );
  }

  return (
    <div className="policy-table-wrapper">
      <Table
        responsive
        hover
        bordered
        className="policy-table align-middle mb-0"
      >
        <thead>
          <tr>
            <th>Loại</th>
            <th>Tiêu đề</th>
            <th>Nội dung</th>
            <th>Trạng thái</th>
            <th>Cập nhật</th>
            <th className="text-center">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {policies.map((policy) => (
            <tr key={policy.id}>
              <td>
                <span
                  className={`policy-type-badge ${
                    policy.type === "BOOKING"
                      ? "policy-type-booking"
                      : "policy-type-cancel"
                  }`}
                >
                  {getTypeLabel(policy.type)}
                </span>
              </td>

              <td>
                <strong>{policy.title}</strong>
              </td>

              <td>
                <div className="policy-content-preview">
                  {policy.content || "-"}
                </div>
              </td>

              <td>
                <span
                  className={`policy-status-badge ${
                    policy.status === "ACTIVE"
                      ? "policy-status-active"
                      : "policy-status-inactive"
                  }`}
                >
                  {getStatusLabel(policy.status)}
                </span>
              </td>

              <td>
                <small>{formatDate(policy.updatedAt)}</small>
              </td>

              <td>
                <div className="d-flex justify-content-center gap-2 flex-wrap">
                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit(policy)}
                  >
                    Sửa
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-secondary"
                    onClick={() => onManageRules(policy)}
                  >
                    Quản lý mức
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDelete(policy)}
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
