import { useEffect, useState, useMemo } from "react";
import { ArrowLeft, CalendarDays, MapPin, Plus, RefreshCw } from "lucide-react";
import { useLocation, useNavigate, useParams } from "react-router-dom";

import api from "../../../api/axios";
import useScheduleStops from "../hooks/useScheduleStops";

import ScheduleStopTable from "../components/schedule/ScheduleStopTable";
import ScheduleStopFormModal from "../components/schedule/ScheduleStopFormModal";

import "../styles/ManagerScheduleStops.css";

function ManagerScheduleStops() {
  const { tourId, scheduleId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  console.log("========== STOPS PAGE ==========");
  console.log("location.state:", location.state);
  console.log("location.state.schedule:", location.state?.schedule);
  console.log("location.state.scheduleDate:", location.state?.scheduleDate);

  const {
    scheduleStops,
    loading,
    saving,
    error,
    success,
    loadScheduleStops,
    createScheduleStop,
    updateScheduleStop,
    deleteScheduleStop,
  } = useScheduleStops(scheduleId);

  // =====================================================
  // SCHEDULE INFO & STATE
  // =====================================================

  const scheduleFromState = location.state?.schedule || null;
  const scheduleDateFromState = location.state?.scheduleDate || "";

  const [schedule, setSchedule] = useState(scheduleFromState);

  // =====================================================
  // PORTS STATE
  // =====================================================

  const [ports, setPorts] = useState([]);
  const [portsLoading, setPortsLoading] = useState(false);

  // =====================================================
  // MODAL STATE
  // =====================================================

  const [showModal, setShowModal] = useState(false);
  const [selectedStop, setSelectedStop] = useState(null);

  // =====================================================
  // COMPUTED SCHEDULE DATE (Tối ưu với useMemo)
  // =====================================================

  const scheduleDate = useMemo(() => {
    // 1. Ưu tiên ngày từ location.state truyền sang
    if (scheduleDateFromState) {
      return String(scheduleDateFromState).substring(0, 10);
    }

    // 2. Fallback lấy từ object schedule
    if (!schedule) {
      return "";
    }

    const value =
      schedule.realDay ||
      schedule.scheduleDate ||
      schedule.date ||
      schedule.startDate ||
      "";

    return value ? String(value).substring(0, 10) : "";
  }, [scheduleDateFromState, schedule]);

  // =====================================================
  // LOAD DATA & FALLBACK FETCH
  // =====================================================

  useEffect(() => {
    if (!scheduleId) return;

    loadPorts();
    loadScheduleStops();

    // Cập nhật schedule nếu có từ navigation state
    if (location.state?.schedule) {
      setSchedule(location.state.schedule);
    } else if (!scheduleFromState) {
      // Fallback API khi F5 reload trang
      api
        .get(`/scheduler/schedules/${scheduleId}`)
        .then((res) => {
          const data = res.data?.data || res.data;
          setSchedule(data);
        })
        .catch((err) => console.error("Lỗi fetch fallback schedule:", err));
    }
  }, [scheduleId, location.state]);

  // =====================================================
  // LOAD PORTS
  // =====================================================

  const loadPorts = async () => {
    setPortsLoading(true);

    try {
      const response = await api.get("/scheduler/ports");
      console.log("PORTS RESPONSE:", response.data);

      const data = Array.isArray(response.data)
        ? response.data
        : response.data?.content || response.data?.data || [];

      const activePorts = data.filter(
        (port) => !port.status || port.status === "ACTIVE",
      );

      setPorts(activePorts);
    } catch (err) {
      console.error("LOAD PORTS ERROR:", err);
      setPorts([]);
    } finally {
      setPortsLoading(false);
    }
  };

  // =====================================================
  // FORMAT DATE
  // =====================================================

  const formatScheduleDate = (value) => {
    if (!value) return "Chưa xác định";

    const parts = value.split("-");
    if (parts.length !== 3) return value;

    const year = Number(parts[0]);
    const month = Number(parts[1]);
    const day = Number(parts[2]);

    if (!year || !month || !day) return value;

    const date = new Date(year, month - 1, day);

    return date.toLocaleDateString("vi-VN", {
      weekday: "long",
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  // =====================================================
  // HANDLERS
  // =====================================================

  const handleRefresh = () => {
    loadPorts();
    loadScheduleStops();
  };

  const handleBack = () => {
    if (tourId) {
      navigate(`/scheduler/tours/${tourId}/schedules`);
      return;
    }
    navigate("/scheduler/tours");
  };

  const handleCreate = () => {
    setSelectedStop(null);
    setShowModal(true);
  };

  const handleEdit = (stop) => {
    setSelectedStop(stop);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedStop(null);
  };

  const handleSubmit = async (data) => {
    let result = null;

    if (selectedStop) {
      result = await updateScheduleStop(selectedStop.id, data);
    } else {
      result = await createScheduleStop(data);
    }

    if (result) {
      handleCloseModal();
    }
  };

  const handleDelete = async (stop) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa điểm dừng "${stop.portName || "này"}" không?`,
    );

    if (!confirmed) return;

    await deleteScheduleStop(stop.id);
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="schedule-stop-page">
      {/* HEADER */}
      <div className="schedule-stop-header">
        <div className="schedule-stop-header-left">
          <button
            type="button"
            className="schedule-stop-back-button"
            onClick={handleBack}
          >
            <ArrowLeft size={18} />
            Quay lại
          </button>

          <div>
            <div className="schedule-stop-title-row">
              <MapPin size={24} />
              <h1>Quản lý điểm dừng</h1>
            </div>
            <p>Quản lý các cảng cập bến trong lịch trình của ngày này.</p>
          </div>
        </div>

        <button
          type="button"
          className="schedule-stop-refresh-button"
          onClick={handleRefresh}
          disabled={loading || saving || portsLoading}
        >
          <RefreshCw
            size={18}
            className={loading || portsLoading ? "schedule-stop-spin" : ""}
          />
          <span>Làm mới</span>
        </button>
      </div>

      {/* ERROR */}
      {error && (
        <div className="schedule-stop-alert error">
          <strong>Không thể thực hiện thao tác.</strong>
          <span>{error}</span>
        </div>
      )}

      {/* SUCCESS */}
      {success && (
        <div className="schedule-stop-alert success">
          <strong>Thành công.</strong>
          <span>{success}</span>
        </div>
      )}

      {/* SCHEDULE INFORMATION */}
      {schedule && (
        <section className="schedule-stop-info">
          <div className="schedule-stop-info-main">
            <div className="schedule-stop-info-icon">
              <CalendarDays size={24} />
            </div>

            <div className="schedule-stop-info-content">
              <span className="schedule-stop-info-label">Lịch trình</span>
              <h2>{schedule.name || "Lịch trình"}</h2>
              {schedule.description && <p>{schedule.description}</p>}
            </div>
          </div>

          <div className="schedule-stop-info-meta">
            <div className="schedule-stop-meta-item">
              <span>Ngày</span>
              <strong>
                {schedule.dayNumber ? `Ngày ${schedule.dayNumber}` : "-"}
              </strong>
            </div>

            <div className="schedule-stop-meta-item">
              <span>Ngày thực tế</span>
              <strong>{formatScheduleDate(scheduleDate)}</strong>
            </div>

            <div className="schedule-stop-meta-item">
              <span>Số điểm dừng</span>
              <strong>{scheduleStops.length}</strong>
            </div>
          </div>
        </section>
      )}

      {/* NO SCHEDULE DATA */}
      {!schedule && (
        <div className="schedule-stop-port-warning">
          <CalendarDays size={20} />
          <div>
            <strong>Không có thông tin lịch trình</strong>
            <p>Không lấy được thông tin lịch trình từ trang trước.</p>
          </div>
        </div>
      )}

      {/* CONTENT */}
      <section className="schedule-stop-content">
        <div className="schedule-stop-content-header">
          <div>
            <div className="schedule-stop-section-title">
              <MapPin size={21} />
              <h2>Các cảng cập bến</h2>
            </div>
            <p>Thiết lập thứ tự và thời gian tàu đến, rời từng cảng.</p>
          </div>

          <button
            type="button"
            className="schedule-stop-add-button"
            onClick={handleCreate}
            disabled={saving || portsLoading || !ports.length}
          >
            <Plus size={18} />
            <span>Thêm điểm dừng</span>
          </button>
        </div>

        {/* PORT WARNING */}
        {!portsLoading && !ports.length && (
          <div className="schedule-stop-port-warning">
            <MapPin size={20} />
            <div>
              <strong>Chưa có cảng để lựa chọn</strong>
              <p>
                Hệ thống chưa có cảng đang hoạt động. Vui lòng liên hệ Admin để
                cấu hình cảng trước khi thêm điểm dừng.
              </p>
            </div>
          </div>
        )}

        {/* SCHEDULE DATE WARNING */}
        {schedule && !scheduleDate && (
          <div className="schedule-stop-port-warning">
            <CalendarDays size={20} />
            <div>
              <strong>Chưa xác định được ngày lịch trình</strong>
              <p>
                Không thể lấy ngày thực tế từ dữ liệu lịch trình được truyền từ
                trang trước.
              </p>
            </div>
          </div>
        )}

        {/* TABLE */}
        <ScheduleStopTable
          stops={scheduleStops}
          loading={loading}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      </section>

      {/* MODAL */}
      <ScheduleStopFormModal
        open={showModal}
        stop={selectedStop}
        schedule={schedule}
        scheduleDate={scheduleDate}
        ports={ports}
        loading={saving || portsLoading}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
      />
    </div>
  );
}

export default ManagerScheduleStops;
