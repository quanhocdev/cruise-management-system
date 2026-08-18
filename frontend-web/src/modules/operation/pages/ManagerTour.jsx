// src/modules/operation/pages/ManagerTour.jsx

import { useEffect, useMemo, useState } from "react";
import { RefreshCw, Ship, CheckCircle, AlertCircle } from "lucide-react";

import useOperationTours from "../hooks/useOperationTour";

import OperationTourFilter from "../components/OperationTourFilter";
import OperationTourTable from "../components/OperationTourTable";
import CruiseSelectModal from "../components/CruiseSelectModal";
import CruiseAreaAssignmentModal from "../components/CruiseAreaAssignmentModal";

import "../styles/ManagerTour.css";

function ManagerTour() {
  const {
    pendingTours,
    approvedTours,

    availableCruises,
    cruiseLayout,
    assignments,

    loading,
    cruiseLoading,
    approving,
    assigning,
    layoutLoading,
    assignmentLoading,

    error,
    success,

    loadPendingTours,
    loadApprovedTours,
    loadAvailableCruises,
    loadCruiseLayout,
    loadAssignments,
    assignActivityCruiseArea,
    deleteActivityCruiseAssignment,
    assignCruise,
    approveTour,

    clearAvailableCruises,
    clearCruiseLayout,
    clearAssignments,
    clearMessages,
  } = useOperationTours();

  // =====================================================
  // VIEW MODE
  // =====================================================

  const [tourMode, setTourMode] = useState("pending");

  // =====================================================
  // FILTER
  // =====================================================

  const [keyword, setKeyword] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  // =====================================================
  // CRUISE MODAL
  // =====================================================

  const [selectedTour, setSelectedTour] = useState(null);
  const [selectedCruiseId, setSelectedCruiseId] = useState(null);
  const [showCruiseModal, setShowCruiseModal] = useState(false);

  // =====================================================
  // AREA MODAL
  // =====================================================

  const [showAreaModal, setShowAreaModal] = useState(false);
  const [areaTour, setAreaTour] = useState(null);

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadPendingTours();
    loadApprovedTours();
  }, [loadPendingTours, loadApprovedTours]);

  // =====================================================
  // CURRENT TOURS
  // =====================================================

  const currentTours = useMemo(() => {
    return tourMode === "pending" ? pendingTours : approvedTours;
  }, [tourMode, pendingTours, approvedTours]);

  // =====================================================
  // FILTER TOURS
  // =====================================================

  const filteredTours = useMemo(() => {
    const normalizedKeyword = keyword.trim().toLowerCase();

    return currentTours.filter((tour) => {
      // -----------------------------------------------
      // KEYWORD
      // -----------------------------------------------

      if (normalizedKeyword) {
        const name = String(tour.name || "").toLowerCase();
        const code = String(tour.code || "").toLowerCase();
        const description = String(tour.description || "").toLowerCase();

        const matchedKeyword =
          name.includes(normalizedKeyword) ||
          code.includes(normalizedKeyword) ||
          description.includes(normalizedKeyword);

        if (!matchedKeyword) {
          return false;
        }
      }

      // -----------------------------------------------
      // DATE RANGE FILTER
      // -----------------------------------------------

      if (startDate || endDate) {
        const tourStart = tour.startDate ? new Date(tour.startDate) : null;
        const tourEnd = tour.endDate ? new Date(tour.endDate) : null;

        const tourStartStr = tourStart
          ? tourStart.toISOString().split("T")[0]
          : "";
        const tourEndStr = tourEnd ? tourEnd.toISOString().split("T")[0] : "";

        if (startDate) {
          if (tourEndStr && tourEndStr < startDate) return false;
          if (!tourEndStr && tourStartStr && tourStartStr < startDate)
            return false;
        }

        if (endDate) {
          if (tourStartStr && tourStartStr > endDate) return false;
          if (!tourStartStr && tourEndStr && tourEndStr > endDate) return false;
        }
      }

      return true;
    });
  }, [currentTours, keyword, startDate, endDate]);

  // =====================================================
  // CHANGE MODE
  // =====================================================

  const handleChangeMode = (mode) => {
    clearMessages();
    setTourMode(mode);
  };

  // =====================================================
  // CLEAR FILTER
  // =====================================================

  const handleClearFilter = () => {
    setKeyword("");
    setStartDate("");
    setEndDate("");
  };

  // =====================================================
  // SELECT CRUISE MODAL
  // =====================================================

  const handleSelectCruise = async (tour) => {
    clearMessages();
    setSelectedTour(tour);
    setSelectedCruiseId(null);
    setShowCruiseModal(true);

    await loadAvailableCruises(tour.id);
  };

  // =====================================================
  // ASSIGN AREA MODAL (MỞ MODAL PHÂN CÔNG KHU VỰC)
  // =====================================================

  const handleAssignArea = async (tour) => {
    clearMessages();
    setAreaTour(tour);
    setShowAreaModal(true);

    await Promise.all([loadCruiseLayout(tour.id), loadAssignments(tour.id)]);
  };

  // =====================================================
  // CLOSE AREA MODAL
  // =====================================================

  const handleCloseAreaModal = () => {
    if (assignmentLoading || layoutLoading) {
      return;
    }

    setShowAreaModal(false);
    setAreaTour(null);

    clearCruiseLayout();
    clearAssignments();
    clearMessages();
  };

  // =====================================================
  // SELECT CRUISE ID
  // =====================================================

  const handleSelectCruiseId = (cruiseId) => {
    setSelectedCruiseId(cruiseId);
  };

  // =====================================================
  // ASSIGN CRUISE
  // =====================================================

  const handleAssignCruise = async (cruiseId) => {
    if (!selectedTour || !cruiseId) return;

    try {
      await assignCruise(selectedTour.id, cruiseId);
      setSelectedCruiseId(cruiseId);
    } catch (err) {
      console.error("ASSIGN CRUISE ERROR:", err);
    }
  };

  // =====================================================
  // APPROVE TOUR
  // =====================================================

  const handleApproveTour = async (tour) => {
    if (!tour) return;

    const confirmed = window.confirm(
      `Bạn có chắc chắn muốn duyệt Tour "${tour.name}" không?`,
    );

    if (!confirmed) return;

    try {
      let currentTourAssignments = assignments;
      if (!assignments || assignments.length === 0) {
        currentTourAssignments = await loadAssignments(tour.id);
      }

      const payload = {
        tourId: tour.id,
        assignments: currentTourAssignments,
      };

      await approveTour(tour.id, payload);

      await Promise.all([loadPendingTours(), loadApprovedTours()]);
    } catch (err) {
      console.error("APPROVE TOUR ERROR:", err);
    }
  };

  // =====================================================
  // REJECT TOUR
  // =====================================================

  const handleRejectTour = async (tour) => {
    if (!tour) return;

    const confirmed = window.confirm(
      `Bạn có chắc chắn muốn từ chối Tour "${tour.name}" không?`,
    );

    if (!confirmed) return;

    try {
      console.log("Từ chối tour:", tour.id);
      await loadPendingTours();
    } catch (err) {
      console.error("REJECT TOUR ERROR:", err);
    }
  };

  // =====================================================
  // CLOSE CRUISE MODAL
  // =====================================================

  const handleCloseModal = () => {
    if (approving) return;

    setShowCruiseModal(false);
    setSelectedTour(null);
    setSelectedCruiseId(null);

    clearAvailableCruises();
    clearMessages();
  };

  // =====================================================
  // REFRESH
  // =====================================================

  const handleRefresh = async () => {
    clearMessages();
    await Promise.all([loadPendingTours(), loadApprovedTours()]);
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="operation-tour-page">
      <div className="operation-tour-header">
        <div>
          <div className="operation-tour-title">
            <Ship size={28} />
            <h1>Quản lý Tour</h1>
          </div>
          <p>
            Quản lý Tour chờ duyệt, Tour đã duyệt và phân công khu vực hoạt động
            trên du thuyền.
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

      <div className="operation-tour-toolbar">
        <div className="operation-tour-modes">
          <button
            type="button"
            className={`operation-tour-summary-card warning ${
              tourMode === "pending" ? "active" : ""
            }`}
            onClick={() => handleChangeMode("pending")}
          >
            <div className="operation-tour-summary-icon">
              <AlertCircle size={22} />
            </div>
            <div className="operation-tour-summary-info">
              <span>Tour chờ duyệt</span>
              <strong>{pendingTours.length}</strong>
            </div>
          </button>

          <button
            type="button"
            className={`operation-tour-summary-card success ${
              tourMode === "approved" ? "active" : ""
            }`}
            onClick={() => handleChangeMode("approved")}
          >
            <div className="operation-tour-summary-icon">
              <CheckCircle size={22} />
            </div>
            <div className="operation-tour-summary-info">
              <span>Tour đã duyệt</span>
              <strong>{approvedTours.length}</strong>
            </div>
          </button>
        </div>

        <div className="operation-tour-filter-container">
          <OperationTourFilter
            keyword={keyword}
            startDate={startDate}
            endDate={endDate}
            onKeywordChange={setKeyword}
            onStartDateChange={setStartDate}
            onEndDateChange={setEndDate}
            onClear={handleClearFilter}
          />
        </div>
      </div>

      {success && (
        <div className="operation-tour-message success">
          <CheckCircle size={18} />
          <span>{success}</span>
        </div>
      )}

      {error && (
        <div className="operation-tour-message error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      <div className="operation-tour-list-header">
        <div>
          <h2>{tourMode === "pending" ? "Tour chờ duyệt" : "Tour đã duyệt"}</h2>
          <span>
            Hiển thị {filteredTours.length} / {currentTours.length} Tour
          </span>
        </div>
      </div>

      <div className="operation-tour-content">
        <OperationTourTable
          tours={filteredTours}
          loading={loading}
          mode={tourMode}
          onSelectCruise={handleSelectCruise}
          onAssignArea={handleAssignArea}
          onApprove={handleApproveTour}
          onReject={handleRejectTour}
        />
      </div>

      <CruiseSelectModal
        open={showCruiseModal}
        tour={selectedTour}
        cruises={availableCruises}
        loading={cruiseLoading}
        approving={approving}
        assigning={assigning}
        selectedCruiseId={selectedCruiseId}
        onSelectCruise={handleSelectCruiseId}
        onAssignCruise={handleAssignCruise}
        onClose={handleCloseModal}
      />

      <CruiseAreaAssignmentModal
        open={showAreaModal}
        tour={areaTour}
        cruiseLayout={cruiseLayout}
        assignments={assignments}
        layoutLoading={layoutLoading}
        assignmentLoading={assignmentLoading}
        onLoadLayout={loadCruiseLayout}
        onLoadAssignments={loadAssignments}
        onAssignArea={assignActivityCruiseArea}
        onDeleteAssignment={deleteActivityCruiseAssignment}
        onClose={handleCloseAreaModal}
      />
    </div>
  );
}

export default ManagerTour;
