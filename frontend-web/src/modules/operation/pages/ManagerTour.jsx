// src/modules/operation/pages/ManagerTour.jsx

import { useEffect, useMemo, useState } from "react";
import { RefreshCw, Ship, CheckCircle, AlertCircle } from "lucide-react";

// Import 3 hook độc lập
import useOperationTours from "../hooks/useOperationTours";
import useTourCruiseAssignments from "../hooks/useTourCruiseAssignments";
import useActivityTourAssignments from "../hooks/useActivityTourAssignments";

import OperationTourFilter from "../components/OperationTourFilter";
import OperationTourTable from "../components/OperationTourTable";
import CruiseSelectModal from "../components/CruiseSelectModal";
import CruiseAreaAssignmentModal from "../components/CruiseAreaAssignmentModal";

import "../styles/ManagerTour.css";

function ManagerTour() {
  // 1. Hook quản lý Tour (Pending / Approved & Duyệt)
  const {
    pendingTours,
    approvedTours,
    loading: toursLoading,
    approving,
    error: tourError,
    success: tourSuccess,
    loadPendingTours,
    loadApprovedTours,
    approveTour,
    clearMessages: clearTourMessages,
  } = useOperationTours();

  // 2. Hook quản lý Du thuyền (Tìm du thuyền trống, Layout & Gán du thuyền)
  const {
    availableCruises,
    cruiseLayout,
    cruiseLoading,
    layoutLoading,
    assigning,
    error: cruiseError,
    success: cruiseSuccess,
    loadAvailableCruises,
    loadCruiseLayout,
    assignCruise,
    clearAvailableCruises,
    clearCruiseLayout,
    clearMessages: clearCruiseMessages,
  } = useTourCruiseAssignments();

  // 3. Hook quản lý Phân công Khu vực Hoạt động
  const {
    activityAssignments,
    activityLoading,
    error: activityError,
    success: activitySuccess,
    loadActivityAssignments,
    assignActivityArea,
    deleteActivityAssignment,
    clearActivityAssignments,
    clearMessages: clearActivityMessages,
  } = useActivityTourAssignments();

  // Gom các thông báo Lỗi & Thành công từ cả 3 hooks
  const error = tourError || cruiseError || activityError;
  const success = tourSuccess || cruiseSuccess || activitySuccess;

  const clearAllMessages = () => {
    clearTourMessages();
    clearCruiseMessages();
    clearActivityMessages();
  };

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
    clearAllMessages();
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
    clearAllMessages();
    setSelectedTour(tour);

    // 1. Lấy ID du thuyền đã gán trước đó để highlight
    const currentCruiseId = tour?.cruiseId || tour?.cruise?.id || null;
    setSelectedCruiseId(currentCruiseId);

    // 2. Mở Modal
    setShowCruiseModal(true);

    // 3. Tải song song cả danh sách tàu trống VÀ danh sách khu vực đã phân công của Tour
    await Promise.all([
      loadAvailableCruises(tour.id),
      loadActivityAssignments(tour.id), // Fetch phân công để Modal biết tour đã gán khu vực chưa
    ]);
  };
  // =====================================================
  // ASSIGN AREA MODAL (MỞ MODAL PHÂN CÔNG KHU VỰC)
  // =====================================================
  const handleAssignArea = async (tour) => {
    clearAllMessages();
    setAreaTour(tour);
    setShowAreaModal(true);

    await Promise.all([
      loadCruiseLayout(tour.id),
      loadActivityAssignments(tour.id),
    ]);
  };

  // =====================================================
  // CLOSE AREA MODAL
  // =====================================================
  const handleCloseAreaModal = () => {
    if (activityLoading || layoutLoading) {
      return;
    }

    setShowAreaModal(false);
    setAreaTour(null);

    clearCruiseLayout();
    clearActivityAssignments();
    clearAllMessages();
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
      await loadPendingTours();
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
      let currentTourAssignments = activityAssignments;
      if (!activityAssignments || activityAssignments.length === 0) {
        currentTourAssignments = await loadActivityAssignments(tour.id);
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
    clearAllMessages();
  };

  // =====================================================
  // REFRESH
  // =====================================================
  const handleRefresh = async () => {
    clearAllMessages();
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
          disabled={toursLoading}
        >
          <RefreshCw
            size={18}
            className={toursLoading ? "operation-tour-spin" : ""}
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
          loading={toursLoading}
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
        assignments={activityAssignments} // 👈 TRUYỀN DANH SÁCH PHÂN CÔNG VÀO ĐÂY
        cruises={availableCruises}
        loading={cruiseLoading || activityLoading} // 👈 Kết hợp loading để giao diện mượt hơn
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
        assignments={activityAssignments}
        layoutLoading={layoutLoading}
        assignmentLoading={activityLoading}
        onLoadLayout={loadCruiseLayout}
        onLoadAssignments={loadActivityAssignments}
        onAssignArea={assignActivityArea}
        onDeleteAssignment={deleteActivityAssignment}
        onClose={handleCloseAreaModal}
      />
    </div>
  );
}

export default ManagerTour;
