import { useEffect, useState } from "react";
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
  // SCHEDULE INFO
  // =====================================================

  /*
   * Lấy thông tin lịch trình từ state của trang trước.
   *
   * Trang trước cần navigate kèm:
   *
   * navigate(url, {
   *   state: {
   *     schedule: schedule
   *   }
   * });
   *
   * Không gọi GET /scheduler/schedules/{scheduleId}
   * ở đây nữa vì endpoint đó đang trả 500.
   */

  const scheduleFromState = location.state?.schedule || null;

  const [schedule, setSchedule] = useState(scheduleFromState);

  // =====================================================
  // PORTS
  // =====================================================

  const [ports, setPorts] = useState([]);
  const [portsLoading, setPortsLoading] = useState(false);

  // =====================================================
  // MODAL
  // =====================================================

  const [showModal, setShowModal] = useState(false);
  const [selectedStop, setSelectedStop] = useState(null);

  // =====================================================
  // LOAD DATA
  // =====================================================

  useEffect(() => {
    if (!scheduleId) {
      return;
    }

    loadPorts();
    loadScheduleStops();
  }, [scheduleId]);

  // =====================================================
  // UPDATE SCHEDULE FROM NAVIGATION STATE
  // =====================================================

  useEffect(() => {
    if (location.state?.schedule) {
      setSchedule(location.state.schedule);
    }
  }, [location.state]);

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
  // GET SCHEDULE DATE
  // =====================================================

  const getScheduleDate = () => {
    if (!schedule) {
      return "";
    }

    /*
     * Tùy DTO lịch trình của bạn đang trả field nào.
     *
     * Ưu tiên:
     * realDay
     * scheduleDate
     * date
     * startDate
     *
     * Ví dụ:
     * 2026-08-20
     * hoặc:
     * 2026-08-20T00:00:00
     */

    const value =
      schedule.realDay ||
      schedule.scheduleDate ||
      schedule.date ||
      schedule.startDate ||
      "";

    if (!value) {
      return "";
    }

    if (typeof value === "string") {
      return value.substring(0, 10);
    }

    return "";
  };

  const scheduleDate = getScheduleDate();

  // =====================================================
  // FORMAT DATE
  // =====================================================

  const formatScheduleDate = (value) => {
    if (!value) {
      return "Chưa xác định";
    }

    /*
     * Không dùng new Date() ở đây để tránh lệch ngày
     * do timezone.
     *
     * Backend trả:
     * 2026-08-20
     *
     * thì hiển thị đúng:
     * Thứ năm, 20/08/2026
     */

    const parts = value.split("-");

    if (parts.length !== 3) {
      return value;
    }

    const year = Number(parts[0]);
    const month = Number(parts[1]);
    const day = Number(parts[2]);

    if (!year || !month || !day) {
      return value;
    }

    const date = new Date(year, month - 1, day);

    return date.toLocaleDateString("vi-VN", {
      weekday: "long",
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  // =====================================================
  // REFRESH
  // =====================================================

  const handleRefresh = () => {
    loadPorts();
    loadScheduleStops();
  };

  // =====================================================
  // BACK
  // =====================================================

  const handleBack = () => {
    if (tourId) {
      navigate(`/scheduler/tours/${tourId}/schedules`);

      return;
    }

    navigate("/scheduler/tours");
  };

  // =====================================================
  // CREATE
  // =====================================================

  const handleCreate = () => {
    setSelectedStop(null);
    setShowModal(true);
  };

  // =====================================================
  // EDIT
  // =====================================================

  const handleEdit = (stop) => {
    setSelectedStop(stop);
    setShowModal(true);
  };

  // =====================================================
  // CLOSE MODAL
  // =====================================================

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedStop(null);
  };

  // =====================================================
  // SUBMIT
  // =====================================================

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

  // =====================================================
  // DELETE
  // =====================================================

  const handleDelete = async (stop) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa điểm dừng "${stop.portName || "này"}" không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteScheduleStop(stop.id);
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="schedule-stop-page">
      {/* =====================================================
          HEADER
         ===================================================== */}

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

      {/* =====================================================
          ERROR
         ===================================================== */}

      {error && (
        <div className="schedule-stop-alert error">
          <strong>Không thể thực hiện thao tác.</strong>

          <span>{error}</span>
        </div>
      )}

      {/* =====================================================
          SUCCESS
         ===================================================== */}

      {success && (
        <div className="schedule-stop-alert success">
          <strong>Thành công.</strong>

          <span>{success}</span>
        </div>
      )}

      {/* =====================================================
          SCHEDULE INFORMATION
         ===================================================== */}

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

      {/* =====================================================
          NO SCHEDULE DATA
         ===================================================== */}

      {!schedule && (
        <div className="schedule-stop-port-warning">
          <CalendarDays size={20} />

          <div>
            <strong>Không có thông tin lịch trình</strong>

            <p>Không lấy được thông tin lịch trình từ trang trước.</p>
          </div>
        </div>
      )}

      {/* =====================================================
          CONTENT
         ===================================================== */}

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
            disabled={saving || portsLoading}
          >
            <Plus size={18} />

            <span>Thêm điểm dừng</span>
          </button>
        </div>

        {/* =================================================
            PORT WARNING
           ================================================= */}

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

        {/* =================================================
            SCHEDULE DATE WARNING
           ================================================= */}

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

        {/* =================================================
            TABLE
           ================================================= */}

        <ScheduleStopTable
          stops={scheduleStops}
          loading={loading}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      </section>

      {/* =====================================================
          MODAL
         ===================================================== */}

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
