import React, { useState, useMemo } from "react"; // 1. Bổ sung useMemo
import { Plus, RefreshCw, AlertCircle } from "lucide-react";
import { useActivityCruise } from "../hooks/useActivityCruise";
import ActivityCruiseTable from "../components/ActivityCruiseTable";
import ActivityCruiseFormModal from "../components/ActivityCruiseFormModal";
import ActivityCruiseFilter from "../components/ActivityCruiseFilter"; // 2. Bổ sung import
import "../styles/ActivityCruise.css";

function ActivityCruise() {
  const {
    activities,
    loading,
    error,
    refresh,
    createActivity,
    updateActivity,
    deleteActivity,
  } = useActivityCruise();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedActivity, setSelectedActivity] = useState(null);

  // States quản lý lọc và tìm kiếm
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  // Mở modal để tạo mới
  const handleOpenCreateModal = () => {
    setSelectedActivity(null);
    setIsModalOpen(true);
  };

  // Mở modal để chỉnh sửa
  const handleOpenEditModal = (activity) => {
    setSelectedActivity(activity);
    setIsModalOpen(true);
  };

  // Đóng modal
  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedActivity(null);
  };

  // Xử lý Submit Form (Tạo mới hoặc Cập nhật)
  const handleSubmitForm = async (formData) => {
    try {
      if (selectedActivity) {
        await updateActivity(selectedActivity.id, formData);
      } else {
        await createActivity(formData);
      }
      handleCloseModal();
    } catch (err) {
      console.error("Lỗi khi lưu hoạt động:", err);
    }
  };

  // Xử lý Xóa hoạt động
  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa hoạt động này?")) {
      try {
        await deleteActivity(id);
      } catch (err) {
        console.error("Lỗi khi xóa hoạt động:", err);
      }
    }
  };

  // Logic lọc danh sách theo từ khóa và trạng thái
  const filteredActivities = useMemo(() => {
    if (!activities) return [];

    return activities.filter((item) => {
      const matchesSearch =
        item.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.description?.toLowerCase().includes(searchTerm.toLowerCase());

      const matchesStatus =
        statusFilter === "ALL" || item.status === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [activities, searchTerm, statusFilter]);

  return (
    <div className="onboard-activity-page">
      {/* HEADER TRANG */}
      <div
        className="onboard-activity-header"
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "20px",
        }}
      >
        <div>
          <h2
            style={{
              margin: 0,
              fontSize: "20px",
              fontWeight: 700,
              color: "#0f172a",
            }}
          >
            Quản lý Hoạt động Du thuyền
          </h2>
          <p style={{ margin: "4px 0 0", fontSize: "13px", color: "#64748b" }}>
            Danh sách các chương trình và dịch vụ giải trí trên tàu
          </p>
        </div>

        <div style={{ display: "flex", gap: "10px" }}>
          <button
            type="button"
            className="onboard-activity-btn cancel"
            onClick={refresh}
            title="Tải lại dữ liệu"
            style={{ display: "flex", alignItems: "center", gap: "6px" }}
          >
            <RefreshCw size={16} /> Làm mới
          </button>
          <button
            type="button"
            className="onboard-activity-btn submit"
            onClick={handleOpenCreateModal}
            style={{ display: "flex", alignItems: "center", gap: "6px" }}
          >
            <Plus size={16} /> Thêm hoạt động
          </button>
        </div>
      </div>

      {/* GỌI COMPONENT LỌC / TÌM KIẾM */}
      <ActivityCruiseFilter
        searchTerm={searchTerm}
        onSearchChange={setSearchTerm}
        statusFilter={statusFilter}
        onStatusChange={setStatusFilter}
      />

      {/* THÔNG BÁO LỖI */}
      {error && (
        <div
          style={{
            padding: "12px 16px",
            backgroundColor: "#fef2f2",
            border: "1px solid #fecaca",
            borderRadius: "8px",
            color: "#991b1b",
            marginBottom: "16px",
            display: "flex",
            alignItems: "center",
            gap: "8px",
            fontSize: "14px",
          }}
        >
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* BẢNG DANH SÁCH (Sửa lại activities -> filteredActivities) */}
      <ActivityCruiseTable
        activities={filteredActivities}
        loading={loading}
        onEdit={handleOpenEditModal}
        onDelete={handleDelete}
      />

      {/* MODAL THÊM / SỬA */}
      <ActivityCruiseFormModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        onSubmit={handleSubmitForm}
        initialData={selectedActivity}
      />
    </div>
  );
}

export default ActivityCruise;
