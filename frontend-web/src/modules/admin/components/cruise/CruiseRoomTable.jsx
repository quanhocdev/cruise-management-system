import { Button, Spinner, Table } from "react-bootstrap";

export default function CruiseRoomTable({ rooms, loading, onEdit, onDelete }) {
  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" />
      </div>
    );
  }

  if (!rooms || rooms.length === 0) {
    return (
      <div className="cruise-empty-state">
        Chưa có phòng nào trong tầng này.
      </div>
    );
  }

  return (
    <div className="table-responsive">
      <Table bordered hover className="cruise-table align-middle">
        <thead>
          <tr>
            <th>STT</th>
            <th>Mã phòng</th>
            <th>Loại phòng</th>
            <th>Trạng thái</th>
            <th className="text-center">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {rooms.map((room, index) => (
            <tr key={room.id}>
              <td>{index + 1}</td>

              <td>
                <strong>{room.code}</strong>
              </td>

              <td>{room.roomTypeName || "-"}</td>

              <td>
                <span
                  className={
                    room.status === "ACTIVE"
                      ? "badge bg-success"
                      : "badge bg-secondary"
                  }
                >
                  {room.status === "ACTIVE" ? "Hoạt động" : "Không hoạt động"}
                </span>
              </td>

              <td className="text-center">
                <div className="d-flex justify-content-center gap-2">
                  <Button
                    variant="outline-primary"
                    size="sm"
                    onClick={() => onEdit(room)}
                  >
                    Sửa
                  </Button>

                  <Button
                    variant="outline-danger"
                    size="sm"
                    onClick={() => onDelete(room)}
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
