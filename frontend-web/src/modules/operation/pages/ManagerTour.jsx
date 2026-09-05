// src/modules/operation/pages/ManagerTour.jsx

import { useNavigate } from "react-router-dom";
import { useCallback, useEffect, useMemo, useState } from "react";
import { CheckCircle, AlertCircle } from "lucide-react";

import useOperationTours from "../hooks/useOperationTours";
import useTourCruiseAssignments from "../hooks/useTourCruiseAssignments";
import useActivityCruiseTourAssignments from "../hooks/useActivityCruiseTourAssignments";
import useProductTourAssignments from "../hooks/useProductTourAssignments";
import useServiceTourAssignments from "../hooks/useServiceTourAssignments";

import OperationTourHeaderToolbar from "../components/OperationTourHeaderToolbar";
import OperationTourTable from "../components/OperationTourTable";
import CruiseSelectModal from "../components/CruiseSelectModal";
import CruiseAreaAssignmentModal from "../components/CruiseAreaAssignmentModal";

import "../styles/ManagerTour.css";

function ManagerTour() {
  const navigate = useNavigate();

  const handleViewTour = (tour) => {
    const targetId = tour.id || tour.tourId || tour.tripId;
    navigate(`/operation/tour-configuration?tourId=${targetId}`);
  };

  const {
    pendingTours,
    approvedTours,
    readyTours,
    loading: toursLoading,
    approving,
    error: tourError,
    success: tourSuccess,
    loadPendingTours,
    loadApprovedTours,
    loadReadyTours,
    approveTour,
    clearMessages: clearTourMessages,
  } = useOperationTours();

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
  } = useActivityCruiseTourAssignments();

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

  const [tourMode, setTourMode] = useState("APPROVAL_PENDING");
  const [keyword, setKeyword] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const [selectedTour, setSelectedTour] = useState(null);
  const [selectedCruiseId, setSelectedCruiseId] = useState(null);
  const [showCruiseModal, setShowCruiseModal] = useState(false);
  const [showAreaModal, setShowAreaModal] = useState(false);
  const [areaTour, setAreaTour] = useState(null);

  // Gọi tải dữ liệu đầy đủ các tab quan trọng khi vừa vào trang
  useEffect(() => {
    loadPendingTours();
    loadApprovedTours();
    loadReadyTours();
  }, [loadPendingTours, loadApprovedTours, loadReadyTours]);

  // Gom toàn bộ tour vào client state
  const allTours = useMemo(() => {
    return [
      ...(pendingTours || []), 
      ...(approvedTours || []),
      ...(readyTours || [])
    ];
  }, [pendingTours, approvedTours, readyTours]);

  const currentTours = useMemo(() => {
    return allTours.filter(
      (tour) => (tour.statusTrip || tour.status) === tourMode,
    );
  }, [allTours, tourMode]);

  const statusCounts = useMemo(() => {
    return allTours.reduce((acc, tour) => {
      const status = tour.statusTrip || tour.status;
      if (status) {
        acc[status] = (acc[status] || 0) + 1;
      }
      return acc;
    }, {});
  }, [allTours]);

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

        if (!matchedKeyword) return false;
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

  const handleClearFilter = () => {
    setKeyword("");
    setStartDate("");
    setEndDate("");
  };

  const handleSelectCruise = async (tour) => {
    clearAllMessages();
    setSelectedTour(tour);

    const currentCruiseId = tour?.cruiseId || tour?.cruise?.id || null;
    setSelectedCruiseId(currentCruiseId);
    setShowCruiseModal(true);

    if (tourMode === "APPROVAL_PENDING") {
      try {
        await Promise.all([
          loadAvailableCruises(tour.id),
          loadActivityAssignments(tour.id),
          loadProductAssignments(tour.id),
          loadServiceAssignments(tour.id),
        ]);
      } catch (err) {
        console.error("Lỗi khi tải thông tin phân công / tàu trống:", err);
      }
    } else {
      try {
        await Promise.all([
          loadActivityAssignments(tour.id),
          loadProductAssignments(tour.id),
          loadServiceAssignments(tour.id),
        ]);
      } catch (err) {
        console.error("Lỗi khi tải thông tin phân công:", err);
      }
    }
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

  const handleSelectCruiseId = (cruiseId) => {
    setSelectedCruiseId(cruiseId);
  };

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

  const handleApproveTour = async (tour) => {
    if (!tour) return;

    const confirmed = window.confirm(
      `Bạn có chắc chắn muốn duyệt Tour "${tour.name}" không?`,
    );

    if (!confirmed) return;

    try {
      let currentActivityAssignments = activityAssignments;
      if (
        !currentActivityAssignments ||
        currentActivityAssignments.length === 0
      ) {
        currentActivityAssignments = await loadActivityAssignments(tour.id);
      }

      let currentProductAssignments = productAssignments;
      if (
        !currentProductAssignments ||
        currentProductAssignments.length === 0
      ) {
        currentProductAssignments = await loadProductAssignments(tour.id);
      }

      const payload = {
        tourId: tour.id,
        assignments: currentActivityAssignments || [],
        productAssignments: currentProductAssignments || [],
      };

      await approveTour(tour.id, payload);
      await Promise.all([loadPendingTours(), loadApprovedTours(), loadReadyTours()]);
    } catch (err) {
      console.error("APPROVE TOUR ERROR:", err);
    }
  };

  const handleRejectTour = async (tour) => {
    if (!tour) return;

    const confirmed = window.confirm(
      `Bạn có chắc chắn muốn từ chối Tour "${tour.name}" không?`,
    );

    if (!confirmed) return;

    try {
      await loadPendingTours();
    } catch (err) {
      console.error("REJECT TOUR ERROR:", err);
    }
  };

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

  return (
    <div className="operation-tour-page">
      <OperationTourHeaderToolbar
        tourMode={tourMode}
        statusCounts={statusCounts}
        onChangeMode={(newStatus) => setTourMode(newStatus)}
        keyword={keyword}
        startDate={startDate}
        endDate={endDate}
        onKeywordChange={setKeyword}
        onStartDateChange={setStartDate}
        onEndDateChange={setEndDate}
        onClearFilter={handleClearFilter}
      />

      {success && (
        <div className="operation-tour-success">
          <CheckCircle size={18} />
          <span>{success}</span>
        </div>
      )}

      {error && (
        <div className="operation-tour-error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      <div className="operation-tour-list-header">
        <div>
          <h2>Danh sách Tour ({tourMode})</h2>
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
          onView={handleViewTour}
          onSelectCruise={handleSelectCruise}
          onAssignArea={handleAssignArea}
          onApprove={handleApproveTour}
          onReject={handleRejectTour}
        />
      </div>

      <CruiseSelectModal
        open={showCruiseModal}
        tour={selectedTour}
        assignments={[
          ...(Array.isArray(activityAssignments) ? activityAssignments : []),
          ...(Array.isArray(productAssignments) ? productAssignments : []),
          ...(Array.isArray(serviceAssignments) ? serviceAssignments : []),
        ]}
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