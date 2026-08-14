import { Button, Spinner, Table } from "react-bootstrap";

export default function RoomTypeTable({
  roomTypes,
  loading,
  onEdit,
  onDelete,
}) {
  // =====================================================
  // LOADING
  // =====================================================

  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" />

        <p className="mt-2 text-muted">Đang tải danh sách loại phòng...</p>
      </div>
    );
  }

  // =====================================================
  // EMPTY
  // =====================================================

  if (!roomTypes || roomTypes.length === 0) {
    return (
      <div className="text-center py-5 text-muted">Chưa có loại phòng nào.</div>
    );
  }

  // =====================================================
  // TABLE
  // =====================================================

  return (
    <div className="table-responsive">
      <Table bordered hover responsive className="align-middle">
        <thead>
          <tr>
            <th style={{ width: "80px" }}>STT</th>

            <th style={{ width: "250px" }}>Tên loại phòng</th>

            <th>Mô tả</th>

            <th className="text-center" style={{ width: "220px" }}>
              Thao tác
            </th>
          </tr>
        </thead>

        <tbody>
          {roomTypes.map((roomType, index) => (
            <tr key={roomType.id}>
              <td>{index + 1}</td>

              <td>
                <strong>{roomType.name}</strong>
              </td>

              <td>
                {roomType.description ? (
                  roomType.description
                ) : (
                  <span className="text-muted">Không có mô tả</span>
                )}
              </td>

              <td>
                <div className="d-flex justify-content-center gap-2">
                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit(roomType)}
                  >
                    Sửa
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDelete(roomType)}
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
