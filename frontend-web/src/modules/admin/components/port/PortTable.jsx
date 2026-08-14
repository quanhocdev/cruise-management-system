import { Button, Card, Spinner, Table } from "react-bootstrap";

export default function PortTable({ ports, loading, onEdit, onDeactivate }) {
  return (
    <Card>
      <Card.Body>
        {loading ? (
          <div className="text-center py-5">
            <Spinner animation="border" />

            <div className="mt-2">Đang tải danh sách cảng...</div>
          </div>
        ) : ports.length === 0 ? (
          <div className="text-center text-muted py-5">Chưa có cảng nào.</div>
        ) : (
          <div className="table-responsive">
            <Table hover bordered align="middle">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Tên cảng</th>
                  <th>Địa chỉ</th>
                  <th>Thành phố</th>
                  <th>Quốc gia</th>
                  <th>Tọa độ</th>
                  <th>Trạng thái</th>
                  <th className="text-center port-actions-column">Thao tác</th>
                </tr>
              </thead>

              <tbody>
                {ports.map((port, index) => (
                  <tr key={port.id}>
                    <td>{index + 1}</td>

                    <td>
                      <strong>{port.name}</strong>
                    </td>

                    <td>{port.address || "—"}</td>

                    <td>{port.city || "—"}</td>

                    <td>{port.country || "—"}</td>

                    <td>
                      <small>
                        {port.latitude}
                        <br />
                        {port.longitude}
                      </small>
                    </td>

                    <td>
                      {port.status === "ACTIVE" ? (
                        <span className="badge bg-success">ACTIVE</span>
                      ) : (
                        <span className="badge bg-secondary">INACTIVE</span>
                      )}
                    </td>

                    <td>
                      <div className="d-flex gap-2 justify-content-center">
                        <Button
                          size="sm"
                          variant="outline-primary"
                          onClick={() => onEdit(port)}
                        >
                          Sửa
                        </Button>

                        {port.status === "ACTIVE" && (
                          <Button
                            size="sm"
                            variant="outline-danger"
                            onClick={() => onDeactivate(port)}
                          >
                            Vô hiệu hóa
                          </Button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </div>
        )}
      </Card.Body>
    </Card>
  );
}
