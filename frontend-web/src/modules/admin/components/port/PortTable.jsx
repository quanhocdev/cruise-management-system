// src/modules/admin/components/port/PortTable.jsx

import { Button, Spinner, Table } from "react-bootstrap";

export default function PortTable({
  ports = [],
  loading = false,
  onEdit,
  onDelete,
}) {
  const formatCoordinate = (value) => {
    if (value == null) {
      return "-";
    }

    return Number(value).toFixed(6);
  };

  const formatDateTime = (value) => {
    if (!value) {
      return "-";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleString("vi-VN");
  };

  if (loading) {
    return (
      <div className="port-table-loading">
        <Spinner animation="border" />

        <span>Đang tải danh sách cảng...</span>
      </div>
    );
  }

  if (!ports.length) {
    return (
      <div className="port-table-empty">
        <div className="port-table-empty-icon">⚓</div>

        <h5>Chưa có cảng</h5>

        <p>Hệ thống chưa có cảng nào được tạo.</p>
      </div>
    );
  }

  return (
    <div className="port-table-wrapper">
      <Table responsive hover bordered className="port-table align-middle">
        <thead>
          <tr>
            <th>Cảng</th>

            <th>Địa chỉ</th>

            <th>Tọa độ</th>

            <th>Trạng thái</th>

            <th>Cập nhật</th>

            <th className="port-table-action-column">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {ports.map((port) => (
            <tr key={port.id}>
              <td>
                <div className="port-table-name">{port.name}</div>

                <div className="port-table-country">
                  {port.city || "-"}, {port.country || "-"}
                </div>
              </td>

              <td>
                <div className="port-table-address">{port.address || "-"}</div>
              </td>

              <td>
                <div className="port-table-coordinates">
                  <div>Lat: {formatCoordinate(port.latitude)}</div>

                  <div>Lng: {formatCoordinate(port.longitude)}</div>
                </div>
              </td>

              <td>
                <span
                  className={`port-status-badge ${
                    port.status === "ACTIVE"
                      ? "port-status-active"
                      : "port-status-inactive"
                  }`}
                >
                  {port.status === "ACTIVE"
                    ? "Đang hoạt động"
                    : "Ngừng hoạt động"}
                </span>
              </td>

              <td>{formatDateTime(port.updatedAt)}</td>

              <td>
                <div className="port-table-actions">
                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit?.(port)}
                  >
                    Sửa
                  </Button>

                  {port.status === "ACTIVE" && (
                    <Button
                      size="sm"
                      variant="outline-danger"
                      onClick={() => onDelete?.(port)}
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
  );
}
