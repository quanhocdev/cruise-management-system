import { Button, Card, Spinner, Table } from "react-bootstrap";

export default function AccountTable({
  accounts,
  loading,
  onEdit,
  onDeactivate,
}) {
  if (loading) {
    return (
      <Card>
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
      <Card>
        <Card.Body>
          <div className="text-center text-muted py-5">
            Chưa có tài khoản nhân viên nào.
          </div>
        </Card.Body>
      </Card>
    );
  }

  return (
    <Card>
      <Card.Body>
        <div className="table-responsive">
          <Table hover bordered align="middle" className="mb-0">
            <thead>
              <tr>
                <th>#</th>
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

                const status = account.status || account.accountStatus || "—";

                const isActive = status === "ACTIVE";

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

                    <td>
                      {isActive ? (
                        <span className="badge bg-success">ACTIVE</span>
                      ) : (
                        <span className="badge bg-secondary">{status}</span>
                      )}
                    </td>

                    <td>
                      <div className="d-flex gap-2 justify-content-center">
                        <Button
                          size="sm"
                          variant="outline-primary"
                          onClick={() => onEdit(account)}
                        >
                          Sửa
                        </Button>

                        {isActive && (
                          <Button
                            size="sm"
                            variant="outline-danger"
                            onClick={() => onDeactivate(account)}
                          >
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
