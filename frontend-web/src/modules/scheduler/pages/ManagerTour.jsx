import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Map,
  RefreshCw,
  Plus,
  Route,
  Trash2,
  X,
  Navigation,
} from "lucide-react";

import useTours from "../hooks/useTours";
import useSchedulerRoute from "../hooks/useSchedulerRoute";

import TourTable from "../components/tour/TourTable";
import TourFormModal from "../components/tour/TourFormModal";
import SchedulerRouteMap from "../components/tour/SchedulerRouteMap";

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

export default function ManagerTour() {
  const navigate = useNavigate();

  // Quản lý Tour: CRUD, duyệt Tour.
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

  // Quản lý cảng và tuyến đường: Page -> Hook -> Service.
  const {
    ports,
    departurePort,
    destinationPort,
    departurePortId,
    destinationPortId,
    routePreview,
    loadingPorts,
    routeLoading,
    routeError,
    handleSelectPort,
    clearRouteSelection,
    handlePreviewRoute,
    handleCreateTourWithRoute,
  } = useSchedulerRoute();

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

  const summary = {
    total: tours.length,
    draft: tours.filter((tour) => tour.status === "DRAFT").length,
    pending: tours.filter(
      (tour) => tour.status === "APPROVAL_PENDING"
    ).length,
    approved: tours.filter((tour) => tour.status === "APPROVED").length,
    ready: tours.filter((tour) => tour.status === "READY").length,
    inProgress: tours.filter(
      (tour) => tour.status === "IN_PROGRESS"
    ).length,
    completed: tours.filter(
      (tour) => tour.status === "COMPLETED"
    ).length,
  };

  const openCreateForm = () => {
    if (!departurePortId || !destinationPortId) {
      setError?.("Vui lòng chọn cảng khởi hành và cảng đến.");
      return;
    }

    if (!routePreview) {
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
        // Tạo Tour và lưu tuyến đường đã tính.
        await handleCreateTourWithRoute(formData);
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

  const handleDelete = async (tour) => {
    const confirmed = window.confirm(
      `Bạn có chắc chắn muốn xóa Tour "${tour.name}" không?`
    );

    if (!confirmed) return;

    try {
      await deleteTour(tour.id);
      await refreshTours();
    } catch (err) {
      console.error("Không thể xóa Tour:", err);
      setError?.(err.message || "Không thể xóa Tour.");
    }
  };

  const handleSubmitForApproval = async (tour) => {
    const confirmed = window.confirm(
      `Bạn có muốn gửi Tour "${tour.name}" để phê duyệt không?`
    );

    if (!confirmed) return;

    try {
      await submitForApproval(tour.id);
      await refreshTours();
    } catch (err) {
      console.error("Không thể gửi duyệt Tour:", err);
      setError?.(err.message || "Không thể gửi Tour để phê duyệt.");
    }
  };

  const handleManageSchedule = (tour) => {
    navigate(`/scheduler/tours/${tour.id}/schedules`);
  };

  const getPortName = (port) => {
    if (!port) return "Chưa chọn";
    return port.name || port.portName || port.code || "Cảng";
  };

  const formatDistance = (distance) => {
    if (distance == null || Number.isNaN(Number(distance))) {
      return "--";
    }

    return `${Number(distance).toLocaleString("vi-VN", {
      maximumFractionDigits: 2,
    })} km`;
  };

  return (
    <div className="manager-tour-page">
      {/* PAGE HEADER */}
      <div className="manager-tour-header">
        <div>
          <h2>Quản lý Tour</h2>
          <p>
            Lập tuyến đường, tạo Tour và quản lý các hành trình du thuyền.
          </p>
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
      <section className="scheduler-route-section">
        <div className="scheduler-route-heading">
          <div className="scheduler-route-heading-icon">
            <Route size={21} />
          </div>

          <div>
            <h3>Lập tuyến đường cho Tour</h3>
            <p>
              Chọn cảng khởi hành và cảng đến trên bản đồ để xem trước tuyến
              đường biển.
            </p>
          </div>
        </div>

        <div className="scheduler-route-layout">
          <div className="scheduler-route-map-container">
            {loadingPorts ? (
              <div className="scheduler-route-map-loading">
                <RefreshCw className="spin" size={22} />
                <span>Đang tải danh sách cảng...</span>
              </div>
            ) : (
              <SchedulerRouteMap
                ports={ports}
                departurePortId={departurePortId}
                destinationPortId={destinationPortId}
                routeGeoJson={routePreview?.geoJson || null}
                onSelectPort={handleSelectPort}
              />
            )}
          </div>

          <div className="scheduler-route-sidebar">
            <div className="scheduler-route-port-card">
              <div className="scheduler-route-port-label">
                <span className="scheduler-route-dot departure-dot" />
                Cảng khởi hành
              </div>

              <strong>
                {getPortName(departurePort)}
              </strong>

              {departurePort?.code && (
                <small>{departurePort.code}</small>
              )}
            </div>

            <div className="scheduler-route-port-card">
              <div className="scheduler-route-port-label">
                <span className="scheduler-route-dot destination-dot" />
                Cảng đến
              </div>

              <strong>
                {getPortName(destinationPort)}
              </strong>

              {destinationPort?.code && (
                <small>{destinationPort.code}</small>
              )}
            </div>

            <div className="scheduler-route-actions">
              <button
                type="button"
                className="scheduler-route-preview-btn"
                onClick={handlePreviewRoute}
                disabled={
                  !departurePortId ||
                  !destinationPortId ||
                  routeLoading ||
                  departurePortId === destinationPortId
                }
              >
                {routeLoading ? (
                  <>
                    <RefreshCw size={16} className="spin" />
                    Đang tính tuyến...
                  </>
                ) : (
                  <>
                    <Navigation size={16} />
                    Tính tuyến đường
                  </>
                )}
              </button>

              <button
                type="button"
                className="scheduler-route-clear-btn"
                onClick={clearRouteSelection}
                disabled={
                  !departurePortId && !destinationPortId
                }
              >
                <Trash2 size={15} />
                Xóa lựa chọn
              </button>
            </div>

            {routeError && (
              <div className="scheduler-route-error">
                {routeError}
              </div>
            )}

            {routePreview && (
              <div className="scheduler-route-result">
                <div className="scheduler-route-result-heading">
                  <Route size={17} />
                  Kết quả tuyến đường
                </div>

                <div className="scheduler-route-distance">
                  <span>Khoảng cách tuyến đường</span>
                  <strong>
                    {formatDistance(routePreview.distanceKm)}
                  </strong>
                </div>

                <div className="scheduler-route-distance-detail">
                  <div>
                    <span>Từ cảng khởi hành</span>
                    <strong>
                      {formatDistance(
                        routePreview.distanceFromDepartureKm
                      )}
                    </strong>
                  </div>

                  <div>
                    <span>Đến cảng đích</span>
                    <strong>
                      {formatDistance(
                        routePreview.distanceToDestinationKm
                      )}
                    </strong>
                  </div>
                </div>

                <p>
                  Tuyến đường đang ở chế độ xem trước. Hệ thống chỉ lưu
                  tuyến đường khi bạn xác nhận tạo Tour.
                </p>
              </div>
            )}

            <div className="scheduler-route-legend">
              <div>
                <span className="scheduler-route-dot departure-dot" />
                Cảng khởi hành
              </div>

              <div>
                <span className="scheduler-route-dot destination-dot" />
                Cảng đến
              </div>

              <div>
                <span className="scheduler-route-dot normal-port-dot" />
                Cảng khác
              </div>
            </div>
          </div>
        </div>

        <div className="scheduler-route-create-footer">
          <div>
            <strong>
              {routePreview
                ? "Tuyến đường đã sẵn sàng"
                : "Chưa có tuyến đường được xác nhận"}
            </strong>

            <p>
              Chỉ có thể tạo Tour sau khi chọn đủ hai cảng và tính tuyến
              đường thành công.
            </p>
          </div>

          <button
            type="button"
            className="scheduler-route-create-btn"
            onClick={openCreateForm}
            disabled={!routePreview || !departurePortId || !destinationPortId}
          >
            <Plus size={17} />
            Tạo Tour từ tuyến đường
          </button>
        </div>
      </section>

      {/* TOUR SUMMARY */}
      <section className="manager-tour-summary">
        <div className="manager-tour-summary-card">
          <span>Tổng số Tour</span>
          <strong>{summary.total}</strong>
        </div>

        <div className="manager-tour-summary-card">
          <span>Bản nháp</span>
          <strong>{summary.draft}</strong>
        </div>

        <div className="manager-tour-summary-card">
          <span>Chờ phê duyệt</span>
          <strong>{summary.pending}</strong>
        </div>

        <div className="manager-tour-summary-card">
          <span>Đã duyệt</span>
          <strong>{summary.approved}</strong>
        </div>

        <div className="manager-tour-summary-card">
          <span>Sẵn sàng</span>
          <strong>{summary.ready}</strong>
        </div>

        <div className="manager-tour-summary-card">
          <span>Đang diễn ra</span>
          <strong>{summary.inProgress}</strong>
        </div>

        <div className="manager-tour-summary-card">
          <span>Hoàn thành</span>
          <strong>{summary.completed}</strong>
        </div>
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

      {/* CREATE / EDIT TOUR MODAL */}
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
