// src/modules/operation/components/packages/TourPackageTable.jsx

import React, { useState } from "react";
import { Edit2, Trash2, Package, CheckCircle2, Eye } from "lucide-react";
import "../../styles/packages/TourPackageTable.css";
import PackageBenefitDetailModal from "./PackageBenefitDetailModal";

const TourPackageTable = ({
  packages = [],
  roomTypes = [],
  loading,
  onEdit,
  onDelete,
}) => {
  const [viewingPackageBenefits, setViewingPackageBenefits] = useState(null);

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

  // Hàm tìm tên hạng phòng dựa vào roomTypeId
  const getRoomTypeName = (roomTypeId) => {
    if (!roomTypeId) return "Áp dụng toàn tour";

    // Tìm kiếm chính xác phòng theo id (chuyển về string để tránh lệch kiểu UUID/String)
    const found = roomTypes.find((rt) => String(rt.id) === String(roomTypeId));

    // Nếu tìm thấy trả về tên phòng, nếu không thấy trả về chuỗi thông báo hoặc ID rút gọn để debug
    return found ? found.name : "Chưa xác định";
  };
  // Đặt đoạn này ngay trước lệnh return trong TourPackageTable.jsx
  console.log("🔍 Kiểm tra dữ liệu RoomTypes:", roomTypes);
  packages.forEach((pkg) => {
    console.log(
      `📦 Package: ${pkg.name} | roomTypeId của gói:`,
      pkg.roomTypeId,
    );
  });
  return (
    <>
      <div className="tour-package-table-wrapper">
        <table className="tour-package-table">
          <thead>
            <tr>
              <th>Tên Gói Tour</th>
              <th>Hạng phòng</th>
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
                  </div>
                </td>
                <td>
                  <span className="room-tag-cell">
                    {getRoomTypeName(pkg.roomTypeId)}
                  </span>
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
                  <button
                    type="button"
                    className="benefit-badge-btn"
                    onClick={() => setViewingPackageBenefits(pkg)}
                    title="Xem chi tiết quyền lợi"
                  >
                    <CheckCircle2 size={14} />
                    <span>
                      {pkg.benefits ? pkg.benefits.length : 0} quyền lợi
                    </span>
                    <Eye size={14} style={{ marginLeft: "4px" }} />
                  </button>
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

      <PackageBenefitDetailModal
        packageData={viewingPackageBenefits}
        onClose={() => setViewingPackageBenefits(null)}
      />
    </>
  );
};

export default TourPackageTable;
