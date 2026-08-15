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
  } = useTours();

  const [showModal, setShowModal] = useState(false);
  const [selectedTour, setSelectedTour] = useState(null);

  useEffect(() => {
    loadTours();
  }, [loadTours]);

  const handleCreate = () => {
    console.log("CLICK TAO TOUR");

    setSelectedTour(null);
    setShowModal(true);
  };

  const handleEdit = (tour) => {
    setSelectedTour(tour);
    setShowModal(true);
  };

  /*
   * =====================================================
   * ĐI ĐẾN QUẢN LÝ LỊCH TRÌNH CỦA TOUR
   * =====================================================
   */
  const handleManageSchedule = (tour) => {
    if (!tour?.id) {
      console.error("TOUR ID KHÔNG TỒN TẠI:", tour);
      return;
    }

    navigate(`/scheduler/tours/${tour.id}/schedules`);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedTour(null);
  };

  const handleSubmit = async (data) => {
    try {
      if (selectedTour) {
        await updateTour(selectedTour.id, data);
      } else {
        await createTour(data);
      }

      handleCloseModal();
      await loadTours();
    } catch (err) {
      console.error("SAVE TOUR ERROR:", err);
    }
  };

  const handleDelete = async (tour) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa tour "${tour.name}" không?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      await deleteTour(tour.id);
      await loadTours();
    } catch (err) {
      console.error("DELETE TOUR ERROR:", err);
    }
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
            onClick={loadTours}
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
        <div className="scheduler-tour-summary-card">
          <span>Tổng số Tour</span>
          <strong>{tours.length}</strong>
        </div>

        <div className="scheduler-tour-summary-card pending">
          <span>Chờ Operation duyệt</span>

          <strong>
            {
              tours.filter((tour) => tour.statusTrip === "APPROVAL_PENDING")
                .length
            }
          </strong>
        </div>

        <div className="scheduler-tour-summary-card approved">
          <span>Đã được duyệt</span>

          <strong>
            {tours.filter((tour) => tour.statusTrip === "APPROVED").length}
          </strong>
        </div>

        <div className="scheduler-tour-summary-card completed">
          <span>Hoàn thành</span>

          <strong>
            {tours.filter((tour) => tour.statusTrip === "COMPLETED").length}
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
