// src/modules/operation/hooks/useManagerTour.js
import { useCallback, useEffect, useMemo, useState } from "react";

import useOperationTours from "./useOperationTours";
import useTourCruiseAssignments from "./useTourCruiseAssignments";
import useActivityCruiseTourAssignments from "./useActivityCruiseTourAssignments";
import useProductTourAssignments from "./useProductTourAssignments";
import useServiceTourAssignments from "./useServiceTourAssignments";

const toDateStr = (value) =>
  value ? new Date(value).toISOString().split("T")[0] : "";

const useManagerTour = () => {
  // ---------- DATA HOOKS ----------
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

  // ---------- UI STATE ----------
  const [tourMode, setTourMode] = useState("APPROVAL_PENDING");
  const [keyword, setKeyword] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const [selectedTour, setSelectedTour] = useState(null);
  const [selectedCruiseId, setSelectedCruiseId] = useState(null);
  const [showCruiseModal, setShowCruiseModal] = useState(false);
  const [showAreaModal, setShowAreaModal] = useState(false);
  const [areaTour, setAreaTour] = useState(null);

  const reloadTours = useCallback(
    () =>
      Promise.all([loadPendingTours(), loadApprovedTours(), loadReadyTours()]),
    [loadPendingTours, loadApprovedTours, loadReadyTours],
  );

  useEffect(() => {
    reloadTours();
  }, [reloadTours]);

  // ---------- DERIVED ----------
  const allTours = useMemo(
    () => [
      ...(pendingTours || []),
      ...(approvedTours || []),
      ...(readyTours || []),
    ],
    [pendingTours, approvedTours, readyTours],
  );

  const currentTours = useMemo(
    () => allTours.filter((t) => (t.statusTrip || t.status) === tourMode),
    [allTours, tourMode],
  );

  const statusCounts = useMemo(
    () =>
      allTours.reduce((acc, tour) => {
        const status = tour.statusTrip || tour.status;
        if (status) acc[status] = (acc[status] || 0) + 1;
        return acc;
      }, {}),
    [allTours],
  );

  const filteredTours = useMemo(() => {
    const kw = keyword.trim().toLowerCase();

    return currentTours.filter((tour) => {
      if (kw) {
        const matched = [tour.name, tour.code, tour.description].some((v) =>
          String(v || "")
            .toLowerCase()
            .includes(kw),
        );
        if (!matched) return false;
      }

      if (startDate || endDate) {
        const tourStart = toDateStr(tour.startDate);
        const tourEnd = toDateStr(tour.endDate);

        if (startDate) {
          if (tourEnd && tourEnd < startDate) return false;
          if (!tourEnd && tourStart && tourStart < startDate) return false;
        }

        if (endDate) {
          if (tourStart && tourStart > endDate) return false;
          if (!tourStart && tourEnd && tourEnd > endDate) return false;
        }
      }

      return true;
    });
  }, [currentTours, keyword, startDate, endDate]);

  const clearFilter = () => {
    setKeyword("");
    setStartDate("");
    setEndDate("");
  };

  // ---------- CRUISE MODAL ----------
  const handleSelectCruise = async (tour) => {
    clearAllMessages();
    setSelectedTour(tour);
    setSelectedCruiseId(tour?.cruiseId || tour?.cruise?.id || null);
    setShowCruiseModal(true);

    const requests = [
      loadActivityAssignments(tour.id),
      loadProductAssignments(tour.id),
      loadServiceAssignments(tour.id),
    ];

    // Chỉ tour đang chờ duyệt mới cần danh sách tàu trống
    if (tourMode === "APPROVAL_PENDING") {
      requests.push(loadAvailableCruises(tour.id));
    }

    try {
      await Promise.all(requests);
    } catch (err) {
      console.error("Lỗi khi tải thông tin phân công / tàu trống:", err);
    }
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

  const handleCloseCruiseModal = () => {
    if (approving) return;

    setShowCruiseModal(false);
    setSelectedTour(null);
    setSelectedCruiseId(null);

    clearAvailableCruises();
    clearProductAssignments();
    clearServiceAssignments();
    clearAllMessages();
  };

  // ---------- AREA MODAL ----------
  const handleLoadAreaAssignments = useCallback(
    (tourId) =>
      Promise.all([
        loadActivityAssignments(tourId),
        loadProductAssignments(tourId),
        loadServiceAssignments(tourId),
      ]),
    [loadActivityAssignments, loadProductAssignments, loadServiceAssignments],
  );

  const handleAssignArea = async (tour) => {
    clearAllMessages();
    setAreaTour(tour);
    setShowAreaModal(true);

    await Promise.all([
      loadCruiseLayout(tour.id),
      handleLoadAreaAssignments(tour.id),
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

  // ---------- APPROVE / REJECT ----------
  const handleApproveTour = async (tour) => {
    if (!tour) return;

    const confirmed = window.confirm(
      `Bạn có chắc chắn muốn duyệt Tour "${tour.name}" không?`,
    );
    if (!confirmed) return;

    try {
      // Luôn tải phân công của CHÍNH tour này, tránh dùng nhầm state của tour khác
      const [activities, products] = await Promise.all([
        loadActivityAssignments(tour.id),
        loadProductAssignments(tour.id),
      ]);

      await approveTour(tour.id, {
        tourId: tour.id,
        assignments: activities || [],
        productAssignments: products || [],
      });

      await reloadTours();
    } catch (err) {
      console.error("APPROVE TOUR ERROR:", err);
    }
  };

  // ⚠️ Giữ nguyên như file gốc: hiện chưa gọi API từ chối, chỉ tải lại danh sách
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

  // ---------- RETURN ----------
  const asArray = (v) => (Array.isArray(v) ? v : []);

  return {
    // list
    tourMode,
    setTourMode,
    statusCounts,
    currentTours,
    filteredTours,
    toursLoading,

    // filter
    filter: {
      keyword,
      startDate,
      endDate,
      setKeyword,
      setStartDate,
      setEndDate,
      clear: clearFilter,
    },

    // messages
    error,
    success,

    // handler cho bảng
    handleSelectCruise,
    handleAssignArea,
    handleApproveTour,
    handleRejectTour,

    // props sẵn cho 2 modal (spread vào component)
    cruiseModalProps: {
      open: showCruiseModal,
      tour: selectedTour,
      assignments: [
        ...asArray(activityAssignments),
        ...asArray(productAssignments),
        ...asArray(serviceAssignments),
      ],
      cruises: availableCruises,
      loading: cruiseLoading || activityLoading || productLoading,
      approving,
      assigning,
      selectedCruiseId,
      onSelectCruise: setSelectedCruiseId,
      onAssignCruise: handleAssignCruise,
      onClose: handleCloseCruiseModal,
    },

    areaModalProps: {
      open: showAreaModal,
      tour: areaTour,
      cruiseLayout,
      activityAssignments,
      productAssignments,
      serviceAssignments,
      layoutLoading,
      assignmentLoading: activityLoading || productLoading || serviceLoading,
      onLoadLayout: loadCruiseLayout,
      onLoadAssignments: handleLoadAreaAssignments,
      onAssignArea: assignActivityArea,
      onAssignProduct: assignProduct,
      onAssignService: assignServiceArea,
      onDeleteActivityAssignment: deleteActivityAssignment,
      onDeleteProductAssignment: deleteProductAssignment,
      onDeleteServiceAssignment: deleteServiceAssignment,
      onClose: handleCloseAreaModal,
    },
  };
};

export default useManagerTour;
