// src/modules/operation/components/packages/TourPackageTable.jsx

import React from "react";
import { Edit2, Trash2, Package, CheckCircle2 } from "lucide-react";
import "../../styles/packages/TourPackageTable.css";

const TourPackageTable = ({ packages = [], loading, onEdit, onDelete }) => {
  if (loading && packages.length === 0) {
    return (
      <div className="tour-package-table-loading">
        Đang tải danh sách gói tour...
      </div>
    );
  }

  if (packages.length === 0) {
    return (
      <div className="tour-package-table-empty">
        <Package size={48} />
        <h3>Chưa có gói tour nào</h3>
        <p>Hãy tạo gói tour đầu tiên để bắt đầu phân phối dịch vụ.</p>
      </div>
    );
  }

  const formatCurrency = (amount) => {
    if (amount == null) return "—";
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  return (
    <div className="tour-package-table-wrapper">
      <table className="tour-package-table">
        <thead>
          <tr>
            <th>Tên Gói Tour</th>
            <th>Mô tả</th>
            <th>Giá</th>
            <th>Khách tối đa</th>
            <th>Quyền lợi đi kèm</th>
            <th>Trạng thái</th>
            <th className="actions-col">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {packages.map((pkg) => (
            <tr key={pkg.id}>
              <td>
                <div className="package-name-cell">
                  <strong>{pkg.name}</strong>
                  {pkg.roomTypeId && (
                    <span className="room-tag">Hạng phòng định danh</span>
                  )}
                </div>
              </td>
              <td>
                <span className="package-desc" title={pkg.description}>
                  {pkg.description || "—"}
                </span>
              </td>
              <td>
                <span className="package-price">
                  {formatCurrency(pkg.price)}
                </span>
              </td>
              <td>
                {pkg.maxPassengers != null
                  ? `${pkg.maxPassengers} khách`
                  : "Không giới hạn"}
              </td>
              <td>
                <span className="benefit-badge">
                  <CheckCircle2 size={14} />
                  {pkg.benefits ? pkg.benefits.length : 0} quyền lợi
                </span>
              </td>
              <td>
                <span
                  className={`package-status-badge ${pkg.status?.toLowerCase()}`}
                >
                  {pkg.status || "ACTIVE"}
                </span>
              </td>
              <td className="actions-col">
                <div className="action-buttons">
                  <button
                    type="button"
                    className="btn-action edit"
                    onClick={() => onEdit(pkg)}
                    title="Chỉnh sửa"
                  >
                    <Edit2 size={16} />
                  </button>
                  <button
                    type="button"
                    className="btn-action delete"
                    onClick={() => onDelete(pkg.id)}
                    title="Xóa gói"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TourPackageTable;
