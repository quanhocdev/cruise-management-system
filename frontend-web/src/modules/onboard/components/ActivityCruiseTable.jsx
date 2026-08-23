import React from "react";
import { Edit2, Trash2, Image as ImageIcon } from "lucide-react";
import "../styles/ActivityCruise.css";

function ActivityCruiseTable({ activities, onEdit, onDelete, loading }) {
  if (loading) {
    return <div className="onboard-activity-loading">Đang tải dữ liệu...</div>;
  }

  if (!activities || activities.length === 0) {
    return (
      <div className="onboard-activity-empty">
        Chưa có hoạt động nào được thêm.
      </div>
    );
  }

  return (
    <div className="onboard-activity-table-container">
      <table className="onboard-activity-table">
        <thead>
          <tr>
            <th>STT</th>
            <th>Hình ảnh</th>
            <th>Tên hoạt động</th>
            <th>Mô tả</th>
            <th>Trạng thái</th>
            <th className="text-center">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {activities.map((item, index) => (
            <tr key={item.id || index}>
              <td>{index + 1}</td>
              <td>
                {item.imageUrl ? (
                  <img
                    src={item.imageUrl}
                    alt={item.name}
                    className="onboard-activity-thumbnail"
                  />
                ) : (
                  <div className="onboard-activity-no-image">
                    <ImageIcon size={18} />
                  </div>
                )}
              </td>
              <td className="font-semibold">{item.name}</td>
              <td className="onboard-activity-desc">
                {item.description || "N/A"}
              </td>
              <td>
                <span
                  className={`onboard-activity-badge ${item.status?.toLowerCase()}`}
                >
                  {item.status === "ACTIVE" ? "Đang hoạt động" : "Tạm dừng"}
                </span>
              </td>
              <td>
                <div className="onboard-activity-actions">
                  <button
                    type="button"
                    className="onboard-activity-btn-action edit"
                    onClick={() => onEdit(item)}
                    title="Chỉnh sửa"
                  >
                    <Edit2 size={16} />
                  </button>
                  <button
                    type="button"
                    className="onboard-activity-btn-action delete"
                    onClick={() => onDelete(item.id)}
                    title="Xóa"
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
}

export default ActivityCruiseTable;
