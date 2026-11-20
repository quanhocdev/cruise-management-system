import { Button, Image, Spinner, Table } from "react-bootstrap";
import { Images } from "lucide-react";

export default function RoomTypeTable({
  roomTypes,
  loading,
  onEdit,
  onDelete,
  onManageImages,
}) {
  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" />
        <p className="mt-2 text-muted">Đang tải danh sách loại phòng...</p>
      </div>
    );
  }

  if (!roomTypes || roomTypes.length === 0) {
    return (
      <div className="text-center py-5 text-muted">Chưa có loại phòng nào.</div>
    );
  }

  return (
    <div className="table-responsive">
      <Table bordered hover responsive className="align-middle">
        <thead>
          <tr>
            <th style={{ width: "70px" }}>STT</th>
            <th style={{ width: "110px" }} className="text-center">
              Ảnh
            </th>
            <th style={{ width: "220px" }}>Tên loại phòng</th>
            <th style={{ width: "130px" }} className="text-center">
              Sức chứa
            </th>
            <th>Mô tả</th>
            <th className="text-center" style={{ width: "260px" }}>
              Thao tác
            </th>
          </tr>
        </thead>

        <tbody>
          {roomTypes.map((roomType, index) => (
            <tr key={roomType.id}>
              <td>{index + 1}</td>
              <td className="text-center">
                {roomType.imageUrl ? (
                  <Image
                    src={roomType.imageUrl}
                    alt={roomType.name}
                    rounded
                    style={{
                      width: "72px",
                      height: "48px",
                      objectFit: "cover",
                    }}
                  />
                ) : (
                  <span className="text-muted small">Chưa có</span>
                )}
              </td>
              <td>
                <strong>{roomType.name}</strong>
              </td>
              <td className="text-center">
                <span className="badge bg-info text-dark">
                  {roomType.capacity || 1} người
                </span>
              </td>
              <td>
                {roomType.description ? (
                  roomType.description
                ) : (
                  <span className="text-muted">Không có mô tả</span>
                )}
              </td>
              <td>
                <div className="d-flex justify-content-center flex-wrap gap-2">
                  {onManageImages && (
                    <Button
                      size="sm"
                      variant="outline-secondary"
                      className="d-inline-flex align-items-center gap-1"
                      onClick={() => onManageImages(roomType)}
                    >
                      <Images size={14} />
                      <span>Ảnh chi tiết</span>
                    </Button>
                  )}
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
