import { Button, Spinner, Table } from "react-bootstrap";

export default function ServiceTable({ services, loading, onEdit, onDelete }) {
  if (loading) {
    return (
      <div className="service-table-loading">
        <Spinner animation="border" />
        <span>Đang tải danh sách dịch vụ...</span>
      </div>
    );
  }

  if (!services || services.length === 0) {
    return (
      <div className="service-table-empty">
        <div className="service-table-empty-icon">🛎️</div>

        <h5>Chưa có dịch vụ</h5>

        <p>Hiện chưa có dịch vụ nào trong hệ thống.</p>
      </div>
    );
  }

  const formatPrice = (price) => {
    if (price === null || price === undefined) {
      return "-";
    }

    return Number(price).toLocaleString("vi-VN") + " ₫";
  };

  const formatDuration = (minutes) => {
    if (minutes === null || minutes === undefined) {
      return "-";
    }

    if (minutes < 60) {
      return `${minutes} phút`;
    }

    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    if (remainingMinutes === 0) {
      return `${hours} giờ`;
    }

    return `${hours} giờ ${remainingMinutes} phút`;
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case "ACTIVE":
        return "Đang hoạt động";

      case "INACTIVE":
        return "Ngừng hoạt động";

      default:
        return status || "-";
    }
  };

  return (
    <div className="service-table-wrapper">
      <Table responsive hover bordered className="service-table align-middle">
        <thead>
          <tr>
            <th className="service-table-image-column">Hình ảnh</th>

            <th>Dịch vụ</th>

            <th>Mô tả</th>

            <th>Giá</th>

            <th>Thời lượng</th>

            <th>Số khách tối đa</th>

            <th>Trạng thái</th>

            <th className="service-table-action-column">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {services.map((service) => (
            <tr key={service.id}>
              {/* IMAGE */}
              <td>
                {service.imageUrl ? (
                  <img
                    src={service.imageUrl}
                    alt={service.name}
                    className="service-table-image"
                  />
                ) : (
                  <div className="service-table-no-image">🛎️</div>
                )}
              </td>

              {/* NAME */}
              <td>
                <div className="service-table-service-name">{service.name}</div>
              </td>

              {/* DESCRIPTION */}
              <td>
                <div className="service-table-description">
                  {service.description || "-"}
                </div>
              </td>

              {/* PRICE */}
              <td>
                <strong>{formatPrice(service.price)}</strong>
              </td>

              {/* DURATION */}
              <td>{formatDuration(service.durationMinutes)}</td>

              {/* MAX PASSENGERS */}
              <td>{service.maxPassengers ?? 0}</td>

              {/* STATUS */}
              <td>
                <span
                  className={`service-status-badge ${
                    service.status === "ACTIVE"
                      ? "service-status-active"
                      : "service-status-inactive"
                  }`}
                >
                  {getStatusLabel(service.status)}
                </span>
              </td>

              {/* ACTION */}
              <td>
                <div className="service-table-actions">
                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit(service)}
                  >
                    Sửa
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDelete(service)}
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
