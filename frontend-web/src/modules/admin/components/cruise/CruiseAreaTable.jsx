import { Button, Spinner, Table } from "react-bootstrap";

export default function CruiseAreaTable({ areas, loading, onEdit, onDelete }) {
  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" />

        <p className="mt-2 text-muted">Đang tải danh sách khu vực...</p>
      </div>
    );
  }

  if (!areas || areas.length === 0) {
    return (
      <div className="text-center py-5 text-muted">
        Chưa có khu vực nào trên tầng này.
      </div>
    );
  }

  return (
    <div className="table-responsive">
      <Table bordered hover className="align-middle cruise-area-table">
        <thead>
          <tr>
            <th style={{ width: "70px" }}>STT</th>

            <th>Tên khu vực</th>

            <th>Mô tả</th>

            <th>Trạng thái</th>

            <th className="text-center" style={{ width: "220px" }}>
              Thao tác
            </th>
          </tr>
        </thead>

        <tbody>
          {areas.map((area, index) => (
            <tr key={area.id}>
              <td>{index + 1}</td>

              <td>
                <strong>{area.name}</strong>
              </td>

              <td>
                {area.description || (
                  <span className="text-muted">Không có mô tả</span>
                )}
              </td>

              <td>
                <span
                  className={
                    area.status === "ACTIVE"
                      ? "badge bg-success"
                      : "badge bg-secondary"
                  }
                >
                  {area.status}
                </span>
              </td>

              <td>
                <div className="d-flex justify-content-center gap-2">
                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit(area)}
                  >
                    Sửa
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDelete(area)}
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
