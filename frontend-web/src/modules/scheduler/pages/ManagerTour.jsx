import { useEffect, useState } from "react";
import { Plus, RefreshCw } from "lucide-react";
import { useNavigate } from "react-router-dom";

import useTours from "../hooks/useTours";
import TourTable from "../components/tour/TourTable";
import TourFormModal from "../components/tour/TourFormModal";

import "../styles/ManagerTour.css";
import "../styles/ManagerSchedule.css";

function ManagerTour() {
  const navigate = useNavigate();

  const {
    tours,
    loading,
    error,
    loadTours,
    createTour,
    updateTour,
    deleteTour,
    submitForApproval,
  } = useTours();

  const [showModal, setShowModal] = useState(false);
  const [selectedTour, setSelectedTour] = useState(null);

  /*
   * =====================================================
   * FILTER
   * =====================================================
   *
   * null = tất cả
   */
  const [statusFilter, setStatusFilter] = useState("");

  useEffect(() => {
    loadTours(statusFilter || null);
  }, [loadTours, statusFilter]);

  /*
   * =====================================================
   * CREATE
   * =====================================================
   */

  const handleCreate = () => {
    console.log("CLICK TAO TOUR");

    setSelectedTour(null);
    setShowModal(true);
  };

  /*
   * =====================================================
   * SUBMIT FOR APPROVAL
   * =====================================================
   */

  const handleSubmitForApproval = async (tour) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn gửi tour "${tour.name}" cho Operation duyệt không?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      await submitForApproval(tour.id);

      await loadTours(statusFilter || null);
    } catch (err) {
      console.error("SUBMIT TOUR FOR APPROVAL ERROR:", err);
    }
  };
  /*
   * =====================================================
   * EDIT
   * =====================================================
   */

  const handleEdit = (tour) => {
    setSelectedTour(tour);
    setShowModal(true);
  };

  /*
   * =====================================================
   * MANAGE SCHEDULE
   * =====================================================
   */

  const handleManageSchedule = (tour) => {
    if (!tour?.id) {
      console.error("TOUR ID KHÔNG TỒN TẠI:", tour);
      return;
    }

    navigate(`/scheduler/tours/${tour.id}/schedules`);
  };

  /*
   * =====================================================
   * CLOSE MODAL
   * =====================================================
   */

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedTour(null);
  };

  /*
   * =====================================================
   * SUBMIT
   * =====================================================
   */

  const handleSubmit = async (data) => {
    try {
      if (selectedTour) {
        await updateTour(selectedTour.id, data);
      } else {
        await createTour(data);
      }

      handleCloseModal();

      /*
       * Tải lại theo filter hiện tại.
       */
      await loadTours(statusFilter || null);
    } catch (err) {
      console.error("SAVE TOUR ERROR:", err);
    }
  };

  /*
   * =====================================================
   * DELETE
   * =====================================================
   */

  const handleDelete = async (tour) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa tour "${tour.name}" không?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      await deleteTour(tour.id);

      /*
       * Tải lại theo filter hiện tại.
       */
      await loadTours(statusFilter || null);
    } catch (err) {
      console.error("DELETE TOUR ERROR:", err);
    }
  };

  /*
   * =====================================================
   * CHANGE FILTER
   * =====================================================
   */

  const handleStatusFilterChange = (event) => {
    const value = event.target.value;

    console.log("FILTER STATUS:", value || "ALL");

    setStatusFilter(value);
  };

  /*
   * =====================================================
   * REFRESH
   * =====================================================
   */

  const handleRefresh = () => {
    loadTours(statusFilter || null);
  };

  return (
    <div className="scheduler-tour-page">
      {/* =====================================================
          HEADER
         ===================================================== */}

      <div className="scheduler-tour-header">
        <div>
          <h1>Quản lý Tour</h1>

          <p>Tạo và quản lý các tour, lịch trình và điểm dừng của tour.</p>
        </div>

        <div className="scheduler-tour-header-actions">
          <button
            type="button"
            className="scheduler-tour-refresh-button"
            onClick={handleRefresh}
            disabled={loading}
            title="Làm mới"
          >
            <RefreshCw size={18} className={loading ? "scheduler-spin" : ""} />

            <span>Làm mới</span>
          </button>

          <button
            type="button"
            className="scheduler-tour-create-button"
            onClick={handleCreate}
          >
            <Plus size={18} />

            <span>Tạo Tour</span>
          </button>
        </div>
      </div>

      {/* =====================================================
          FILTER
         ===================================================== */}

      <div className="scheduler-tour-filter">
        <label htmlFor="tour-status-filter">Trạng thái Tour</label>

        <select
          id="tour-status-filter"
          value={statusFilter}
          onChange={handleStatusFilterChange}
          disabled={loading}
        >
          <option value="">Tất cả</option>

          <option value="DRAFT">Đang cấu hình</option>

          <option value="APPROVAL_PENDING">Chờ duyệt</option>

          <option value="APPROVED">Đã được duyệt</option>

          <option value="IN_PROGRESS">Đang diễn ra</option>

          <option value="COMPLETED">Hoàn thành</option>

          <option value="CANCELLED">Đã hủy</option>
        </select>
      </div>

      {/* =====================================================
          ERROR
         ===================================================== */}

      {error && (
        <div className="scheduler-tour-error">
          <strong>Không thể tải dữ liệu.</strong>

          <span>{error}</span>
        </div>
      )}
      {/* =====================================================
    SUMMARY
   ===================================================== */}

      <div className="scheduler-tour-summary">
        {/* DRAFT */}
        <div className="scheduler-tour-summary-card draft">
          <span>Đang cấu hình</span>

          <strong>
            {tours.filter((tour) => tour.statusTrip === "DRAFT").length}
          </strong>
        </div>

        {/* APPROVAL_PENDING */}
        <div className="scheduler-tour-summary-card pending">
          <span>Chờ duyệt</span>

          <strong>
            {
              tours.filter((tour) => tour.statusTrip === "APPROVAL_PENDING")
                .length
            }
          </strong>
        </div>

        {/* APPROVED */}
        <div className="scheduler-tour-summary-card approved">
          <span>Đã được duyệt</span>

          <strong>
            {tours.filter((tour) => tour.statusTrip === "APPROVED").length}
          </strong>
        </div>

        {/* IN_PROGRESS */}
        <div className="scheduler-tour-summary-card in-progress">
          <span>Đang diễn ra</span>

          <strong>
            {tours.filter((tour) => tour.statusTrip === "IN_PROGRESS").length}
          </strong>
        </div>

        {/* COMPLETED */}
        <div className="scheduler-tour-summary-card completed">
          <span>Hoàn thành</span>

          <strong>
            {tours.filter((tour) => tour.statusTrip === "COMPLETED").length}
          </strong>
        </div>

        {/* CANCELLED */}
        <div className="scheduler-tour-summary-card cancelled">
          <span>Đã hủy</span>

          <strong>
            {tours.filter((tour) => tour.statusTrip === "CANCELLED").length}
          </strong>
        </div>
      </div>
      {/* =====================================================
          TOUR TABLE
         ===================================================== */}

      <div className="scheduler-tour-content">
        <TourTable
          tours={tours}
          loading={loading}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onManageSchedule={handleManageSchedule}
          onSubmitForApproval={handleSubmitForApproval}
        />
      </div>

      {/* =====================================================
          CREATE / EDIT TOUR MODAL
         ===================================================== */}

      <TourFormModal
        open={showModal}
        tour={selectedTour}
        loading={loading}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
      />
    </div>
  );
}

export default ManagerTour;
