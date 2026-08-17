import React from "react";
import { Clock, Users, Image as ImageIcon } from "lucide-react";

const ServiceTable = ({ services, loading }) => {
  if (loading) {
    return (
      <div className="convenience-loading">Đang tải danh sách dịch vụ...</div>
    );
  }

  if (!services || services.length === 0) {
    return <div className="convenience-empty">Chưa có dịch vụ nào.</div>;
  }

  return (
    <div className="table-responsive">
      <table className="convenience-table">
        <thead>
          <tr>
            <th>Hình ảnh</th>
            <th>Tên dịch vụ</th>
            <th>Mô tả</th>
            <th>Đơn giá</th>
            <th>Thời lượng</th>
            <th>Sức chứa tối đa</th>
          </tr>
        </thead>
        <tbody>
          {services.map((service) => (
            <tr key={service.id}>
              <td>
                {service.imageUrl ? (
                  <img
                    src={service.imageUrl}
                    alt={service.name}
                    className="service-image-thumb"
                  />
                ) : (
                  <div className="service-image-placeholder">
                    <ImageIcon size={20} />
                  </div>
                )}
              </td>
              <td>
                <span className="font-semibold">{service.name}</span>
              </td>
              <td className="text-muted">{service.description || "—"}</td>
              <td className="font-semibold">
                {service.price != null
                  ? `${Number(service.price).toLocaleString("vi-VN")} VNĐ`
                  : "—"}
              </td>
              <td>
                {service.durationMinutes ? (
                  <span className="flex-align-center">
                    <Clock size={14} className="icon-mr" />
                    {service.durationMinutes} phút
                  </span>
                ) : (
                  "—"
                )}
              </td>
              <td>
                {service.maxPassengers ? (
                  <span className="flex-align-center">
                    <Users size={14} className="icon-mr" />
                    {service.maxPassengers} khách
                  </span>
                ) : (
                  "—"
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ServiceTable;
