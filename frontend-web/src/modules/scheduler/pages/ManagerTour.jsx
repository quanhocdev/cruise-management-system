// src/modules/scheduler/pages/ManagerTour.jsx
// (SỬA file có sẵn) Trang danh sách Tour: thống kê + bảng + nút "Tạo Tour mới".
// Phần lập tuyến đường đã chuyển sang pages/TourRoutePlanner.jsx
// (route: /scheduler/tours/new).

import { useCallback, useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Plus, RefreshCw, X } from "lucide-react";

import useTours from "../hooks/useTours";

import TourTable from "../components/tour/TourTable";
import TourFormModal from "../components/tour/TourFormModal";

import "../styles/ManagerTour.css";
import "../styles/ManagerSchedule.css";

const STATUS_OPTIONS = [
  { value: "ALL", label: "Tất cả trạng thái" },
  { value: "DRAFT", label: "Bản nháp" },
  { value: "APPROVAL_PENDING", label: "Chờ duyệt" },
  { value: "APPROVED", label: "Đã duyệt" },
  { value: "READY", label: "Sẵn sàng" },
  { value: "IN_PROGRESS", label: "Đang diễn ra" },
  { value: "COMPLETED", label: "Hoàn thành" },
  { value: "CANCELLED", label: "Đã hủy" },
];

// Các ô thống kê: [nhãn, trạng thái cần đếm] (null = tổng)
const SUMMARY_ITEMS = [
  ["Tổng số Tour", null],
  ["Bản nháp", "DRAFT"],
  ["Chờ phê duyệt", "APPROVAL_PENDING"],
  ["Đã duyệt", "APPROVED"],
  ["Sẵn sàng", "READY"],
  ["Đang diễn ra", "IN_PROGRESS"],
  ["Hoàn thành", "COMPLETED"],
];

export default function ManagerTour() {
  const navigate = useNavigate();
  const location = useLocation();

  const {
    tours,
    loading,
    saving,
    error,
    success,
    setError,
    setSuccess,
    loadTours,
    updateTour,
    deleteTour,
    submitForApproval,
  } = useTours();

  const [statusFilter, setStatusFilter] = useState("ALL");
  const [showForm, setShowForm] = useState(false);
  const [selectedTour, setSelectedTour] = useState(null);
  const [formLoading, setFormLoading] = useState(false);

  const refreshTours = useCallback(async () => {
    try {
      await loadTours();
    } catch (err) {
      console.error("Không thể tải danh sách Tour:", err);
    }
  }, [loadTours]);

  useEffect(() => {
    refreshTours();
  }, [refreshTours]);

  // Nhận thông báo thành công từ trang lập tuyến (navigate kèm state),
  // hiển thị một lần rồi xóa state để F5 không hiện lại.
  useEffect(() => {
    const message = location.state?.successMessage;
    if (!message) return;

    setSuccess?.(message);
    navigate(location.pathname, { replace: true, state: null });
  }, [location.state, location.pathname, navigate, setSuccess]);

  const filteredTours =
    statusFilter === "ALL"
      ? tours
      : tours.filter((tour) => tour.status === statusFilter);

  const countByStatus = (status) =>
    status
      ? tours.filter((tour) => tour.status === status).length
      : tours.length;

  // ---------- FORM (chỉ còn dùng để SỬA Tour) ----------
  const handleEdit = (tour) => {
    setSelectedTour(tour);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    if (formLoading) return;

    setShowForm(false);
    setSelectedTour(null);
  };

  const handleSubmitForm = async (formData) => {
    if (!selectedTour) return;

    setFormLoading(true);

    try {
      await updateTour(selectedTour.id, formData);

      setShowForm(false);
      setSelectedTour(null);

      await refreshTours();
    } catch (err) {
      console.error("Không thể lưu Tour:", err);
      setError?.(err.message || "Không thể lưu thông tin Tour.");
    } finally {
      setFormLoading(false);
    }
  };

  // ---------- ACTIONS TRÊN BẢNG ----------
  const runWithConfirm = async (message, action, fallbackError) => {
    if (!window.confirm(message)) return;

    try {
      await action();
      await refreshTours();
    } catch (err) {
      console.error(fallbackError, err);
      setError?.(err.message || fallbackError);
    }
  };

  const handleDelete = (tour) =>
    runWithConfirm(
      `Bạn có chắc chắn muốn xóa Tour "${tour.name}" không?`,
      () => deleteTour(tour.id),
      "Không thể xóa Tour.",
    );

  const handleSubmitForApproval = (tour) =>
    runWithConfirm(
      `Bạn có muốn gửi Tour "${tour.name}" để phê duyệt không?`,
      () => submitForApproval(tour.id),
      "Không thể gửi Tour để phê duyệt.",
    );

  const handleManageSchedule = (tour) => {
    navigate(`/scheduler/tours/${tour.id}/schedules`);
  };

  const handleCreateTour = () => {
    navigate("/scheduler/tours/new");
  };

  return (
    <div className="manager-tour-page">
      {/* PAGE HEADER */}
      <div className="manager-tour-header">
        <div>
          <h2>Quản lý Tour</h2>
          <p>Theo dõi trạng thái và quản lý các hành trình du thuyền.</p>
        </div>

        <div className="manager-tour-header-actions">
          <button
            type="button"
            className="manager-tour-refresh-btn"
            onClick={refreshTours}
            disabled={loading}
          >
            <RefreshCw size={16} className={loading ? "spin" : undefined} />
            Làm mới
          </button>

          <button
            type="button"
            className="manager-tour-create-btn"
            onClick={handleCreateTour}
          >
            <Plus size={17} />
            Tạo Tour mới
          </button>
        </div>
      </div>

      {/* NOTIFICATIONS */}
      {error && (
        <div className="manager-tour-alert manager-tour-alert-error">
          <span>{error}</span>
          <button
            type="button"
            onClick={() => setError?.("")}
            aria-label="Đóng thông báo lỗi"
          >
            <X size={16} />
          </button>
        </div>
      )}

      {success && (
        <div className="manager-tour-alert manager-tour-alert-success">
          <span>{success}</span>
          <button
            type="button"
            onClick={() => setSuccess?.("")}
            aria-label="Đóng thông báo thành công"
          >
            <X size={16} />
          </button>
        </div>
      )}

      {/* TOUR SUMMARY */}
      <section className="manager-tour-summary">
        {SUMMARY_ITEMS.map(([label, status]) => (
          <div key={label} className="manager-tour-summary-card">
            <span>{label}</span>
            <strong>{countByStatus(status)}</strong>
          </div>
        ))}
      </section>

      {/* TOUR LIST */}
      <section className="manager-tour-list-section">
        <div className="manager-tour-list-header">
          <div>
            <h3>Danh sách Tour</h3>
            <p>Quản lý thông tin và trạng thái các Tour đã tạo.</p>
          </div>

          <div className="manager-tour-list-actions">
            <select
              value={statusFilter}
              onChange={(event) => setStatusFilter(event.target.value)}
              className="manager-tour-status-filter"
            >
              {STATUS_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        <TourTable
          tours={filteredTours}
          loading={loading}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onManageSchedule={handleManageSchedule}
          onSubmitForApproval={handleSubmitForApproval}
        />
      </section>

      <TourFormModal
        open={showForm}
        tour={selectedTour}
        loading={formLoading || saving}
        onClose={handleCloseForm}
        onSubmit={handleSubmitForm}
      />
    </div>
  );
}
