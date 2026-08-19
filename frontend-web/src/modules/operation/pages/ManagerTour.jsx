// src/modules/operation/pages/ManagerTour.jsx

import { useCallback, useEffect, useMemo, useState } from "react";
import { RefreshCw, Ship, CheckCircle, AlertCircle } from "lucide-react";

// Import các hook hiện có
import useOperationTours from "../hooks/useOperationTours";
import useTourCruiseAssignments from "../hooks/useTourCruiseAssignments";
import useActivityTourAssignments from "../hooks/useActivityTourAssignments";
import useProductTourAssignments from "../hooks/useProductTourAssignments";
import useServiceTourAssignments from "../hooks/useServiceTourAssignments";

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

  // 4. Hook quản lý Phân công Sản phẩm
  const {
    productAssignments,
    productLoading,
    error: productError,
    success: productSuccess,
    loadProductAssignments,
    assignProduct,
    deleteProductAssignment,
    clearProductAssignments,
    clearMessages: clearProductMessages,
  } = useProductTourAssignments();

  // 5. Hook quản lý Phân công Dịch vụ
  const {
    serviceAssignments,
    serviceLoading,
    serviceError,
    serviceSuccess,
    loadServiceAssignments,
    assignServiceArea,
    deleteServiceAssignment,
    clearServiceAssignments,
    clearMessages: clearServiceMessages,
  } = useServiceTourAssignments();

  // Gom các thông báo Lỗi & Thành công
  const error =
    tourError || cruiseError || activityError || productError || serviceError;

  const success =
    tourSuccess ||
    cruiseSuccess ||
    activitySuccess ||
    productSuccess ||
    serviceSuccess;

  const clearAllMessages = () => {
    clearTourMessages();
    clearCruiseMessages();
    clearActivityMessages();
    clearProductMessages();
    clearServiceMessages();
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

    const currentCruiseId = tour?.cruiseId || tour?.cruise?.id || null;
    setSelectedCruiseId(currentCruiseId);

    setShowCruiseModal(true);

    // Tải song song danh sách tàu trống, khu vực và sản phẩm
    await Promise.all([
      loadAvailableCruises(tour.id),
      loadActivityAssignments(tour.id),
      loadProductAssignments(tour.id),
      loadServiceAssignments(tour.id),
    ]);
  };

  const handleLoadAreaAssignments = useCallback(
    async (tourId) => {
      await Promise.all([
        loadActivityAssignments(tourId),
        loadProductAssignments(tourId),
        loadServiceAssignments(tourId),
      ]);
    },
    [loadActivityAssignments, loadProductAssignments, loadServiceAssignments],
  );

  // =====================================================
  // ASSIGN AREA MODAL
  // =====================================================
  const handleAssignArea = async (tour) => {
    clearAllMessages();
    setAreaTour(tour);
    setShowAreaModal(true);

    await Promise.all([
      loadCruiseLayout(tour.id),
      loadActivityAssignments(tour.id),
      loadProductAssignments(tour.id),
      loadServiceAssignments(tour.id),
    ]);
  };

  // =====================================================
  // CLOSE AREA MODAL
  // =====================================================
  const handleCloseAreaModal = () => {
    if (activityLoading || productLoading || serviceLoading || layoutLoading) {
      return;
    }

    setShowAreaModal(false);
    setAreaTour(null);

    clearCruiseLayout();
    clearActivityAssignments();
    clearProductAssignments();
    clearServiceAssignments();
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
      // 1. Lấy dữ liệu Hoạt động
      let currentActivityAssignments = activityAssignments;
      if (
        !currentActivityAssignments ||
        currentActivityAssignments.length === 0
      ) {
        currentActivityAssignments = await loadActivityAssignments(tour.id);
      }

      // 2. Lấy dữ liệu Sản phẩm
      let currentProductAssignments = productAssignments;
      if (
        !currentProductAssignments ||
        currentProductAssignments.length === 0
      ) {
        currentProductAssignments = await loadProductAssignments(tour.id);
      }

      // 3. Đóng gói Payload lưu vào Database
      const payload = {
        tourId: tour.id,
        assignments: currentActivityAssignments || [],
        productAssignments: currentProductAssignments || [],
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
    clearProductAssignments();
    clearServiceAssignments();
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
            Quản lý Tour chờ duyệt, Tour đã duyệt và phân công khu vực hoạt
            động, sản phẩm trên du thuyền.
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
        assignments={activityAssignments}
        cruises={availableCruises}
        loading={cruiseLoading || activityLoading || productLoading}
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
        activityAssignments={activityAssignments}
        productAssignments={productAssignments}
        serviceAssignments={serviceAssignments}
        layoutLoading={layoutLoading}
        assignmentLoading={activityLoading || productLoading || serviceLoading}
        onLoadLayout={loadCruiseLayout}
        onLoadAssignments={handleLoadAreaAssignments}
        onAssignArea={assignActivityArea}
        onAssignProduct={assignProduct}
        onAssignService={assignServiceArea}
        onDeleteActivityAssignment={deleteActivityAssignment}
        onDeleteProductAssignment={deleteProductAssignment}
        onDeleteServiceAssignment={deleteServiceAssignment}
        onClose={handleCloseAreaModal}
      />
    </div>
  );
}

export default ManagerTour;
