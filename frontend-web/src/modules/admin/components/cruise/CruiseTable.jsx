import { Button, Spinner, Table } from "react-bootstrap";

export default function CruiseTable({ cruises, loading, onEdit, onDelete }) {
  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" />
        <p className="mt-2 text-muted">Đang tải danh sách du thuyền...</p>
      </div>
    );
  }

  if (!cruises || cruises.length === 0) {
    return (
      <div className="text-center py-5 text-muted">Chưa có du thuyền nào.</div>
    );
  }

  return (
    <div className="table-responsive">
      <Table bordered hover responsive className="align-middle cruise-table">
        <thead>
          <tr>
            <th style={{ width: "70px" }}>STT</th>

            <th style={{ width: "100px" }}>Hình ảnh</th>

            <th>Tên du thuyền</th>

            <th>Mã</th>

            <th>Sức chứa</th>

            <th>Trạng thái</th>

            <th className="text-center" style={{ width: "220px" }}>
              Thao tác
            </th>
          </tr>
        </thead>

        <tbody>
          {cruises.map((cruise, index) => (
            <tr key={cruise.id}>
              <td>{index + 1}</td>

              <td>
                {cruise.imageUrl ? (
                  <img
                    src={cruise.imageUrl}
                    alt={cruise.name}
                    className="cruise-table-image"
                  />
                ) : (
                  <div className="cruise-table-image-placeholder">No image</div>
                )}
              </td>

              <td>
                <strong>{cruise.name}</strong>
              </td>

              <td>
                <code>{cruise.code}</code>
              </td>

              <td>{cruise.maxPassengers}</td>

              <td>
                <span
                  className={
                    cruise.status === "ACTIVE"
                      ? "badge bg-success"
                      : "badge bg-secondary"
                  }
                >
                  {cruise.status}
                </span>
              </td>

              <td>
                <div className="d-flex justify-content-center gap-2">
                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit(cruise)}
                  >
                    Sửa
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDelete(cruise)}
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
