// src/modules/operation/pages/ManagerTour.jsx

import { useCallback, useEffect, useMemo, useState } from "react";
import { CheckCircle, AlertCircle } from "lucide-react";

// Import các hook
import useOperationTours from "../hooks/useOperationTours";
import useTourCruiseAssignments from "../hooks/useTourCruiseAssignments";
import useActivityTourAssignments from "../hooks/useActivityTourAssignments";
import useProductTourAssignments from "../hooks/useProductTourAssignments";
import useServiceTourAssignments from "../hooks/useServiceTourAssignments";

// Import Components
import OperationTourHeaderToolbar from "../components/OperationTourHeaderToolbar";
import OperationTourTable from "../components/OperationTourTable";
import CruiseSelectModal from "../components/CruiseSelectModal";
import CruiseAreaAssignmentModal from "../components/CruiseAreaAssignmentModal";

import "../styles/ManagerTour.css";

function ManagerTour() {
  // 1. Hook quản lý Tour
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

  // 2. Hook quản lý Du thuyền
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

  // 3. Hook quản lý Phân công Hoạt động
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

  // State
  const [tourMode, setTourMode] = useState("APPROVAL_PENDING");
  const [keyword, setKeyword] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  // Modal State
  const [selectedTour, setSelectedTour] = useState(null);
  const [selectedCruiseId, setSelectedCruiseId] = useState(null);
  const [showCruiseModal, setShowCruiseModal] = useState(false);
  const [showAreaModal, setShowAreaModal] = useState(false);
  const [areaTour, setAreaTour] = useState(null);

  // Initial Load
  useEffect(() => {
    loadPendingTours();
    loadApprovedTours();
  }, [loadPendingTours, loadApprovedTours]);

  // Gom tất cả tour đang có trong client
  const allTours = useMemo(() => {
    return [...(pendingTours || []), ...(approvedTours || [])];
  }, [pendingTours, approvedTours]);

  // Current Tours
  const currentTours = useMemo(() => {
    return allTours.filter(
      (tour) => (tour.statusTrip || tour.status) === tourMode,
    );
  }, [allTours, tourMode]);

  // Đếm số lượng tour động theo từng Enum
  const statusCounts = useMemo(() => {
    return allTours.reduce((acc, tour) => {
      const status = tour.statusTrip || tour.status;
      if (status) {
        acc[status] = (acc[status] || 0) + 1;
      }
      return acc;
    }, {});
  }, [allTours]);

  // Filtered Tours
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

  // Handlers
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

  const handleViewTour = (tour) => {
    handleAssignArea(tour);
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
      await Promise.all([loadPendingTours(), loadApprovedTours()]);
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
      console.log("Từ chối tour:", tour.id);
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
      {/* TOOLBAR & FILTER CONTAINER */}
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

      {/* ALERT MESSAGES */}
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

      {/* TABLE HEADER */}
      <div className="operation-tour-list-header">
        <div>
          <h2>Danh sách Tour ({tourMode})</h2>
          <span>
            Hiển thị {filteredTours.length} / {currentTours.length} Tour
          </span>
        </div>
      </div>

      {/* TABLE CONTENT */}
      <div className="operation-tour-content">
        <OperationTourTable
          tours={filteredTours}
          loading={toursLoading}
          mode={tourMode}
          onSelectCruise={handleSelectCruise}
          onAssignArea={handleAssignArea}
          onApprove={handleApproveTour}
          onReject={handleRejectTour}
          onView={handleViewTour}
        />
      </div>

      {/* MODALS */}
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
