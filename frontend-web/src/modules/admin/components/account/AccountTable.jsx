import { Button, Card, Spinner, Table } from "react-bootstrap";

function getStatusBadge(status) {
  const statusConfig = {
    ACTIVE: {
      label: "Đang hoạt động",
      className: "bg-success",
    },
    INVITED: {
      label: "Chờ kích hoạt",
      className: "bg-info text-dark",
    },
    INACTIVE: {
      label: "Đã vô hiệu hóa",
      className: "bg-secondary",
    },
    BLOCKED: {
      label: "Đã khóa",
      className: "bg-danger",
    },
  };

  const config = statusConfig[status] || {
    label: status || "Không xác định",
    className: "bg-secondary",
  };

  return <span className={`badge ${config.className}`}>{config.label}</span>;
}

export default function AccountTable({
  accounts,
  loading,
  onEdit,
  onDeactivate,
  emptyMessage = "Không tìm thấy tài khoản phù hợp.",
}) {
  if (loading) {
    return (
      <Card className="manager-account-table-card">
        <Card.Body>
          <div className="text-center py-5">
            <Spinner animation="border" />
            <div className="mt-2">Đang tải danh sách tài khoản...</div>
          </div>
        </Card.Body>
      </Card>
    );
  }

  if (!accounts.length) {
    return (
      <Card className="manager-account-table-card">
        <Card.Body>
          <div className="text-center text-muted py-5">{emptyMessage}</div>
        </Card.Body>
      </Card>
    );
  }

  return (
    <Card className="manager-account-table-card">
      <Card.Body>
        <div className="table-responsive">
          <Table hover bordered align="middle" className="mb-0">
            <thead className="table-light">
              <tr>
                <th style={{ width: "55px" }}>#</th>
                <th>Tài khoản</th>
                <th>Email</th>
                <th>Vai trò</th>
                <th>Trạng thái</th>
                <th className="text-center" style={{ width: "190px" }}>
                  Thao tác
                </th>
              </tr>
            </thead>

            <tbody>
              {accounts.map((account, index) => {
                const role =
                  account.role?.name || account.roleName || account.role || "—";

                const status = account.status || "—";

                return (
                  <tr key={account.id}>
                    <td>{index + 1}</td>

                    <td>
                      <strong>{account.username || "—"}</strong>
                    </td>

                    <td>{account.email || "—"}</td>

                    <td>
                      <span className="badge bg-primary">{role}</span>
                    </td>

                    <td>{getStatusBadge(status)}</td>

                    <td>
                      <div className="d-flex gap-2 justify-content-center">
                        <Button
                          size="sm"
                          variant="outline-primary"
                          onClick={() => onEdit(account)}
                        >
                          <i className="bi bi-pencil me-1" />
                          Sửa
                        </Button>

                        {status === "ACTIVE" && (
                          <Button
                            size="sm"
                            variant="outline-danger"
                            onClick={() => onDeactivate(account)}
                          >
                            <i className="bi bi-person-slash me-1" />
                            Vô hiệu hóa
                          </Button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </Table>
        </div>
      </Card.Body>
    </Card>
  );
}
