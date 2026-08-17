// src/modules/operation/pages/ManagerTour.jsx
import { useEffect, useState } from "react";
import { RefreshCw, Ship, CheckCircle, AlertCircle } from "lucide-react";

import useOperationTours from "../hooks/useOperationTour";

import OperationTourTable from "../components/OperationTourTable";
import CruiseSelectModal from "../components/CruiseSelectModal";

import "../styles/ManagerTour.css";
function ManagerTour() {
  const {
    tours,
    availableCruises,

    loading,
    cruiseLoading,
    approving,

    error,
    success,

    loadPendingTours,
    loadAvailableCruises,
    approveTour,

    clearAvailableCruises,
    clearMessages,
  } = useOperationTours();

  const [selectedTour, setSelectedTour] = useState(null);

  const [selectedCruiseId, setSelectedCruiseId] = useState(null);

  const [showCruiseModal, setShowCruiseModal] = useState(false);

  /**
   * =====================================================
   * INITIAL LOAD
   * =====================================================
   */

  useEffect(() => {
    loadPendingTours();
  }, [loadPendingTours]);

  /**
   * =====================================================
   * SELECT TOUR
   * =====================================================
   */

  const handleSelectCruise = async (tour) => {
    clearMessages();

    setSelectedTour(tour);

    setSelectedCruiseId(null);

    setShowCruiseModal(true);

    await loadAvailableCruises(tour.id);
  };

  /**
   * =====================================================
   * SELECT CRUISE
   * =====================================================
   */

  const handleSelectCruiseId = (cruiseId) => {
    setSelectedCruiseId(cruiseId);
  };

  /**
   * =====================================================
   * APPROVE
   * =====================================================
   */

  const handleApprove = async () => {
    if (!selectedTour) {
      return;
    }

    if (!selectedCruiseId) {
      window.alert("Vui lòng chọn du thuyền trước khi duyệt.");

      return;
    }

    const selectedCruise = availableCruises.find(
      (cruise) => cruise.id === selectedCruiseId,
    );

    const confirmed = window.confirm(
      `Bạn có chắc muốn gán du thuyền "${
        selectedCruise?.name || ""
      }" cho Tour "${selectedTour.name}" và duyệt Tour này không?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      await approveTour(selectedTour.id, selectedCruiseId);

      setShowCruiseModal(false);

      setSelectedTour(null);

      setSelectedCruiseId(null);

      clearAvailableCruises();

      await loadPendingTours();
    } catch (err) {
      console.error("APPROVE TOUR FROM OPERATION ERROR:", err);
    }
  };

  /**
   * =====================================================
   * CLOSE MODAL
   * =====================================================
   */

  const handleCloseModal = () => {
    if (approving) {
      return;
    }

    setShowCruiseModal(false);

    setSelectedTour(null);

    setSelectedCruiseId(null);

    clearAvailableCruises();

    clearMessages();
  };

  /**
   * =====================================================
   * REFRESH
   * =====================================================
   */

  const handleRefresh = async () => {
    clearMessages();

    await loadPendingTours();
  };

  return (
    <div className="operation-tour-page">
      {/* =================================================
            HEADER
            ================================================= */}

      <div className="operation-tour-header">
        <div>
          <div className="operation-tour-title">
            <Ship size={28} />

            <h1>Quản lý Tour</h1>
          </div>

          <p>
            Xem các Tour đang chờ duyệt, kiểm tra du thuyền khả dụng và gán du
            thuyền cho Tour.
          </p>
        </div>

        <button
          type="button"
          className="operation-tour-refresh-button"
          onClick={handleRefresh}
          disabled={loading}
        >
          <RefreshCw
            size={18}
            className={loading ? "operation-tour-spin" : ""}
          />

          <span>Làm mới</span>
        </button>
      </div>

      {/* =================================================
    SUMMARY CARDS (Tối ưu UI KPI)
    ================================================= */}
      <div className="operation-tour-summary">
        {/* Card 1: Chờ duyệt */}
        <div className="operation-tour-summary-card warning">
          <div className="operation-tour-summary-icon">
            <AlertCircle size={22} />
          </div>
          <div className="operation-tour-summary-info">
            <span>Tour chờ duyệt</span>
            <strong>{tours.length}</strong>
          </div>
        </div>

        {/* Card 2: Trạng thái hệ thống / Đồng bộ */}
        <div className="operation-tour-summary-card success">
          <div className="operation-tour-summary-icon">
            <CheckCircle size={22} />
          </div>
          <div className="operation-tour-summary-info">
            <span>Trạng thái kết nối</span>
            <div className="operation-status-badge">
              <span className="status-dot"></span>
              <strong>{loading ? "Đang cập nhật..." : "Sẵn sàng xử lý"}</strong>
            </div>
          </div>
        </div>
      </div>

      {/* =================================================
            SUCCESS
            ================================================= */}

      {success && (
        <div className="operation-tour-message success">
          <CheckCircle size={18} />

          <span>{success}</span>
        </div>
      )}

      {/* =================================================
            ERROR
            ================================================= */}

      {error && (
        <div className="operation-tour-message error">
          <AlertCircle size={18} />

          <span>{error}</span>
        </div>
      )}

      {/* =================================================
            TABLE
            ================================================= */}

      <div className="operation-tour-content">
        <OperationTourTable
          tours={tours}
          loading={loading}
          onSelectCruise={handleSelectCruise}
        />
      </div>

      {/* =================================================
            CRUISE SELECT MODAL
            ================================================= */}

      <CruiseSelectModal
        open={showCruiseModal}
        tour={selectedTour}
        cruises={availableCruises}
        loading={cruiseLoading}
        approving={approving}
        selectedCruiseId={selectedCruiseId}
        onSelectCruise={handleSelectCruiseId}
        onApprove={handleApprove}
        onClose={handleCloseModal}
      />
    </div>
  );
}

export default ManagerTour;
