// src/modules/scheduler/pages/ManagerTour.jsx
// (SỬA file có sẵn) Khối lập tuyến đường đã tách sang components/tour/RoutePlannerSection.jsx

import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { RefreshCw, X } from "lucide-react";

import useTours from "../hooks/useTours";
import useSchedulerRoute from "../hooks/useSchedulerRoute";

import TourTable from "../components/tour/TourTable";
import TourFormModal from "../components/tour/TourFormModal";
import RoutePlannerSection from "../components/tour/RoutePlannerSection";

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

  const {
    tours,
    loading,
    saving,
    error,
    success,
    setError,
    setSuccess,
    loadTours,
    createTour,
    updateTour,
    deleteTour,
    submitForApproval,
  } = useTours();

  const route = useSchedulerRoute();

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

  const filteredTours =
    statusFilter === "ALL"
      ? tours
      : tours.filter((tour) => tour.status === statusFilter);

  const countByStatus = (status) =>
    status
      ? tours.filter((tour) => tour.status === status).length
      : tours.length;

  // ---------- FORM ----------
  const openCreateForm = () => {
    if (!route.departurePortId || !route.destinationPortId) {
      setError?.("Vui lòng chọn cảng khởi hành và cảng đến.");
      return;
    }

    if (!route.routePreview) {
      setError?.("Vui lòng tính tuyến đường trước khi tạo Tour.");
      return;
    }

    setSelectedTour(null);
    setShowForm(true);
  };

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
    setFormLoading(true);

    try {
      if (selectedTour) {
        await updateTour(selectedTour.id, formData);
      } else {
        // Tạo Tour và lưu tuyến đường đã tính
        await route.handleCreateTourWithRoute(formData);
      }

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

  return (
    <div className="manager-tour-page">
      {/* PAGE HEADER */}
      <div className="manager-tour-header">
        <div>
          <h2>Quản lý Tour</h2>
          <p>Lập tuyến đường, tạo Tour và quản lý các hành trình du thuyền.</p>
        </div>

        <button
          type="button"
          className="manager-tour-refresh-btn"
          onClick={refreshTours}
          disabled={loading}
        >
          <RefreshCw size={16} />
          Làm mới
        </button>
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

      {/* ROUTE PLANNER */}
      <RoutePlannerSection
        ports={route.ports}
        departurePort={route.departurePort}
        destinationPort={route.destinationPort}
        departurePortId={route.departurePortId}
        destinationPortId={route.destinationPortId}
        routePreview={route.routePreview}
        loadingPorts={route.loadingPorts}
        routeLoading={route.routeLoading}
        routeError={route.routeError}
        onSelectPort={route.handleSelectPort}
        onPreview={route.handlePreviewRoute}
        onClear={route.clearRouteSelection}
        onCreate={openCreateForm}
      />

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
