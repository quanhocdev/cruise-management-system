// src/modules/scheduler/hooks/useScheduleStopsPage.js
import { useCallback, useEffect, useMemo, useState } from "react";
import { useLocation, useParams } from "react-router-dom";

import useScheduleStops from "./useScheduleStops";
import scheduleService from "../services/scheduleService";
import { getActiveSchedulerPorts } from "../services/schedulerRouteService";

const useScheduleStopsPage = () => {
  const { tourId, scheduleId } = useParams();
  const location = useLocation();

  const stops = useScheduleStops(scheduleId);
  const { loadScheduleStops, createScheduleStop, updateScheduleStop } = stops;

  const stateSchedule = location.state?.schedule || null;
  const stateScheduleDate = location.state?.scheduleDate || "";

  const [schedule, setSchedule] = useState(stateSchedule);
  const [ports, setPorts] = useState([]);
  const [portsLoading, setPortsLoading] = useState(false);

  const [showModal, setShowModal] = useState(false);
  const [selectedStop, setSelectedStop] = useState(null);

  // Ngày thực tế: ưu tiên ngày truyền từ trang trước, sau đó lấy từ schedule
  const scheduleDate = useMemo(() => {
    const value =
      stateScheduleDate ||
      schedule?.realDay ||
      schedule?.scheduleDate ||
      schedule?.date ||
      schedule?.startDate ||
      "";

    return value ? String(value).substring(0, 10) : "";
  }, [stateScheduleDate, schedule]);

  const loadPorts = useCallback(async () => {
    setPortsLoading(true);

    try {
      const data = await getActiveSchedulerPorts();

      setPorts(data.filter((port) => !port.status || port.status === "ACTIVE"));
    } catch (err) {
      console.error("LOAD PORTS ERROR:", err);
      setPorts([]);
    } finally {
      setPortsLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!scheduleId) return;

    loadPorts();
    loadScheduleStops();

    if (location.state?.schedule) {
      setSchedule(location.state.schedule);
    } else {
      // F5 trang -> mất navigation state, tải lại từ API
      scheduleService
        .getScheduleById(tourId, scheduleId)
        .then(setSchedule)
        .catch((err) => console.error("LOAD SCHEDULE ERROR:", err));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [scheduleId, location.state]);

  const refresh = () => {
    loadPorts();
    loadScheduleStops();
  };

  // ---------- MODAL ----------
  const openCreate = () => {
    setSelectedStop(null);
    setShowModal(true);
  };

  const openEdit = (stop) => {
    setSelectedStop(stop);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedStop(null);
  };

  const handleSubmit = async (data) => {
    const result = selectedStop
      ? await updateScheduleStop(selectedStop.id, data)
      : await createScheduleStop(data);

    if (result) closeModal();
  };

  const handleDelete = async (stop) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa điểm dừng "${stop.portName || "này"}" không?`,
    );
    if (!confirmed) return;

    await stops.deleteScheduleStop(stop.id);
  };

  return {
    tourId,
    schedule,
    scheduleDate,
    ports,
    portsLoading,
    scheduleStops: stops.scheduleStops,
    loading: stops.loading,
    saving: stops.saving,
    error: stops.error,
    success: stops.success,
    refresh,

    showModal,
    selectedStop,
    openCreate,
    openEdit,
    closeModal,
    handleSubmit,
    handleDelete,
  };
};

export default useScheduleStopsPage;
