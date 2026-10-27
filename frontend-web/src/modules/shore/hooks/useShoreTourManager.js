// src/modules/shore/hooks/useShoreTourManager.js

import { useCallback, useEffect, useMemo, useState } from "react";

import visitTourService from "../services/visitTourService";
import useVisitTours from "./useVisitTours";

const useShoreTourManager = () => {
  // ---------- DATA ----------
  const {
    visitTours,
    loading: visitToursLoading,
    error: visitToursError,
    reload: reloadVisitTours,
    createVisitTour,
    updateVisitTour,
  } = useVisitTours({ all: true });

  const [masterToursMap, setMasterToursMap] = useState(new Map());
  const [completedTourIds, setCompletedTourIds] = useState(new Set());
  const [extraLoading, setExtraLoading] = useState(false);

  // Master tours + lịch sử cấu hình (tour nào đã thực sự hoàn thành)
  const loadExtraData = useCallback(async () => {
    setExtraLoading(true);

    try {
      const masterData = await visitTourService.getAllMasterTours();
      const map = new Map();
      (masterData || []).forEach((tour) => map.set(tour.id, tour));
      setMasterToursMap(map);
    } catch (err) {
      console.error("🔥 LOAD MASTER TOURS ERROR:", err);
    }

    try {
      const historyData = await visitTourService.getConfigurationHistory();
      if (Array.isArray(historyData)) {
        setCompletedTourIds(new Set(historyData.map((item) => item.tourId)));
      }
    } catch (err) {
      console.error("🔥 LOAD HISTORY ERROR:", err);
    } finally {
      setExtraLoading(false);
    }
  }, []);

  useEffect(() => {
    loadExtraData();
  }, [loadExtraData]);

  const loading = visitToursLoading || extraLoading;

  const reload = useCallback(
    () => Promise.all([reloadVisitTours(), loadExtraData()]),
    [reloadVisitTours, loadExtraData],
  );

  // ---------- UI STATE ----------
  const [statusFilter, setStatusFilter] = useState("ALL");

  const [completeLoading, setCompleteLoading] = useState(null);
  const [completeError, setCompleteError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingVisitTour, setEditingVisitTour] = useState(null);
  const [selectedScheduleStop, setSelectedScheduleStop] = useState(null);
  const [visitTourSaving, setVisitTourSaving] = useState(false);

  // ---------- DERIVED ----------
  const filteredVisitTours = useMemo(() => {
    if (statusFilter === "ALL") return visitTours;
    return visitTours.filter((item) => item.status === statusFilter);
  }, [visitTours, statusFilter]);

  // Map<tourId, VisitTour[]>
  const tourGroups = useMemo(() => {
    const map = new Map();

    visitTours.forEach((visitTour) => {
      if (!visitTour.tourId) return;
      if (!map.has(visitTour.tourId)) map.set(visitTour.tourId, []);
      map.get(visitTour.tourId).push(visitTour);
    });

    return map;
  }, [visitTours]);

  const summary = useMemo(() => {
    const count = (status) =>
      visitTours.filter((item) => item.status === status).length;

    return {
      total: visitTours.length,
      waiting: count("WAITING_CONFIG"),
      configured: count("CONFIGURED"),
      inProgress: count("IN_PROGRESS"),
      completed: count("COMPLETED"),
    };
  }, [visitTours]);

  // Đủ điều kiện hoàn thành: có Visit Tour và tất cả đã được cấu hình (có tên)
  const canCompleteTour = useCallback(
    (tourId) => {
      const tours = tourGroups.get(tourId);
      return !!tours && tours.length > 0 && tours.every((t) => !!t.name);
    },
    [tourGroups],
  );

  // ---------- COMPLETE CONFIGURATION ----------
  const handleCompleteConfiguration = useCallback(
    async (tourId) => {
      if (!tourId) return;

      if (!canCompleteTour(tourId)) {
        setCompleteError(
          "Tất cả Visit Tour của Tour phải được cấu hình trước khi hoàn thành.",
        );
        return;
      }

      const confirmed = window.confirm(
        "Bạn có chắc muốn hoàn thành cấu hình Visit Tour cho Tour này?\n\nSau khi hoàn thành, hệ thống sẽ lưu lịch sử cấu hình và gửi thông báo sang các service liên quan.",
      );
      if (!confirmed) return;

      setCompleteLoading(tourId);
      setCompleteError(null);
      setSuccessMessage(null);

      try {
        await visitTourService.completeTourConfiguration(tourId);
        setSuccessMessage("Đã hoàn thành cấu hình Visit Tour thành công.");
        await reload();
      } catch (err) {
        console.error("🔥 COMPLETE VISIT TOUR CONFIGURATION ERROR:", err);
        setCompleteError(
          err.response?.data?.message ||
            "Không thể hoàn thành cấu hình Visit Tour.",
        );
      } finally {
        setCompleteLoading(null);
      }
    },
    [canCompleteTour, reload],
  );

  // ---------- MODAL ----------
  const openConfiguration = useCallback(
    (tourId, scheduleStopId) => {
      const masterTour = masterToursMap.get(tourId);

      let targetStop = null;
      let targetScheduleDate = "";

      for (const schedule of masterTour?.schedules || []) {
        const foundStop = (schedule.stops || []).find(
          (stop) =>
            stop.id === scheduleStopId ||
            stop.scheduleStopId === scheduleStopId,
        );

        if (foundStop) {
          targetStop = foundStop;
          targetScheduleDate = schedule.scheduleDate || "";
          break;
        }
      }

      const targetVisitTour = visitTours.find(
        (item) => item.scheduleStopId === scheduleStopId,
      );

      setSelectedScheduleStop({
        scheduleStopId,
        portName: targetStop?.portName || targetVisitTour?.portName || "",
        arriveAt: targetStop?.arriveAt || targetVisitTour?.arriveAt || "",
        leaveAt: targetStop?.leaveAt || targetVisitTour?.leaveAt || "",
        scheduleDate: targetScheduleDate || targetVisitTour?.scheduleDate || "",
      });
      setEditingVisitTour(targetVisitTour?.name ? targetVisitTour : null);
      setIsModalOpen(true);
    },
    [masterToursMap, visitTours],
  );

  const closeModal = useCallback(() => {
    setIsModalOpen(false);
    setEditingVisitTour(null);
    setSelectedScheduleStop(null);
  }, []);

  // Ném lỗi lên để modal tự hiển thị
  // ---------- SAVE VISIT TOUR + ASSIGN STAFF ----------
  const submitVisitTour = useCallback(
    async (formData, selectedUserIds = []) => {
      setVisitTourSaving(true);

      try {
        let savedVisitTour;

        if (editingVisitTour) {
          savedVisitTour = await updateVisitTour(editingVisitTour.id, formData);
        } else {
          savedVisitTour = await createVisitTour(
            selectedScheduleStop.scheduleStopId,
            formData,
          );

          // Nếu phân công lỗi sau khi tạo VisitTour,
          // lần thử lại sẽ cập nhật bản ghi vừa tạo thay vì tạo trùng.
          if (savedVisitTour?.id) {
            setEditingVisitTour(savedVisitTour);
          }
        }

        const visitTourId = savedVisitTour?.id || editingVisitTour?.id;

        if (!visitTourId) {
          throw new Error(
            "Đã lưu VisitTour nhưng không nhận được ID để phân công nhân viên.",
          );
        }

        if (selectedUserIds.length > 0) {
          const result = await visitTourService.assignStaff(
            visitTourId,
            selectedUserIds,
          );

          const assignedCount = result?.assigned?.length || 0;
          const invalidCount = result?.invalidUserIds?.length || 0;
          const duplicateCount = result?.alreadyAssigned?.length || 0;

          if (invalidCount > 0 || duplicateCount > 0) {
            setSuccessMessage(
              `Đã lưu VisitTour và phân công thêm ${assignedCount} nhân viên. ` +
                `${duplicateCount} nhân viên đã được phân công trước đó; ` +
                `${invalidCount} nhân viên không hợp lệ.`,
            );
          } else {
            setSuccessMessage(
              `Đã lưu cấu hình VisitTour và phân công thêm ${assignedCount} nhân viên.`,
            );
          }
        } else {
          setSuccessMessage("Đã lưu cấu hình VisitTour thành công.");
        }

        closeModal();
        await reload();
      } finally {
        setVisitTourSaving(false);
      }
    },
    [
      editingVisitTour,
      selectedScheduleStop,
      createVisitTour,
      updateVisitTour,
      closeModal,
      reload,
    ],
  );

  return {
    visitTours,
    filteredVisitTours,
    masterToursMap,
    completedTourIds,
    tourGroups,
    summary,

    loading,
    error: visitToursError ? "Không thể tải danh sách Visit Tour." : null,
    reload,

    statusFilter,
    setStatusFilter,

    canCompleteTour,
    completeLoading,
    completeError,
    clearCompleteError: () => setCompleteError(null),
    successMessage,
    clearSuccessMessage: () => setSuccessMessage(null),
    handleCompleteConfiguration,

    isModalOpen,
    editingVisitTour,
    selectedScheduleStop,
    visitTourSaving,
    openConfiguration,
    closeModal,
    submitVisitTour,
  };
};

export default useShoreTourManager;
