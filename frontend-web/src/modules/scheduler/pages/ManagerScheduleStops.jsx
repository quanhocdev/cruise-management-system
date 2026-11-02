// src/modules/scheduler/pages/ManagerScheduleStops.jsx
// (SỬA file có sẵn) Logic nằm trong hooks/useScheduleStopsPage.js

import { ArrowLeft, CalendarDays, MapPin, Plus, RefreshCw } from "lucide-react";
import { useNavigate } from "react-router-dom";

import useScheduleStopsPage from "../hooks/useScheduleStopsPage";

import ScheduleStopTable from "../components/schedule/ScheduleStopTable";
import ScheduleStopFormModal from "../components/schedule/ScheduleStopFormModal";

import "../styles/ManagerScheduleStops.css";

const formatScheduleDate = (value) => {
  if (!value) return "Chưa xác định";

  const [year, month, day] = value.split("-").map(Number);
  if (!year || !month || !day) return value;

  return new Date(year, month - 1, day).toLocaleDateString("vi-VN", {
    weekday: "long",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

// Khung cảnh báo (icon + tiêu đề + nội dung)
function Warning({ icon, title, children }) {
  return (
    <div className="schedule-stop-port-warning">
      {icon}
      <div>
        <strong>{title}</strong>
        <p>{children}</p>
      </div>
    </div>
  );
}

function ManagerScheduleStops() {
  const navigate = useNavigate();
  const p = useScheduleStopsPage();

  const handleBack = () => {
    navigate(
      p.tourId ? `/scheduler/tours/${p.tourId}/schedules` : "/scheduler/tours",
    );
  };

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
          onClick={p.refresh}
          disabled={p.loading || p.saving || p.portsLoading}
        >
          <RefreshCw
            size={18}
            className={p.loading || p.portsLoading ? "schedule-stop-spin" : ""}
          />
          <span>Làm mới</span>
        </button>
      </div>

      {p.error && (
        <div className="schedule-stop-alert error">
          <strong>Không thể thực hiện thao tác.</strong>
          <span>{p.error}</span>
        </div>
      )}

      {p.success && (
        <div className="schedule-stop-alert success">
          <strong>Thành công.</strong>
          <span>{p.success}</span>
        </div>
      )}

      {/* THÔNG TIN LỊCH TRÌNH */}
      {p.schedule ? (
        <section className="schedule-stop-info">
          <div className="schedule-stop-info-main">
            <div className="schedule-stop-info-icon">
              <CalendarDays size={24} />
            </div>

            <div className="schedule-stop-info-content">
              <span className="schedule-stop-info-label">Lịch trình</span>
              <h2>{p.schedule.name || "Lịch trình"}</h2>
              {p.schedule.description && <p>{p.schedule.description}</p>}
            </div>
          </div>

          <div className="schedule-stop-info-meta">
            <div className="schedule-stop-meta-item">
              <span>Ngày</span>
              <strong>
                {p.schedule.dayNumber ? `Ngày ${p.schedule.dayNumber}` : "-"}
              </strong>
            </div>

            <div className="schedule-stop-meta-item">
              <span>Ngày thực tế</span>
              <strong>{formatScheduleDate(p.scheduleDate)}</strong>
            </div>

            <div className="schedule-stop-meta-item">
              <span>Số điểm dừng</span>
              <strong>{p.scheduleStops.length}</strong>
            </div>
          </div>
        </section>
      ) : (
        <Warning
          icon={<CalendarDays size={20} />}
          title="Không có thông tin lịch trình"
        >
          Không lấy được thông tin lịch trình từ trang trước.
        </Warning>
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
            onClick={p.openCreate}
            disabled={p.saving || p.portsLoading || !p.ports.length}
          >
            <Plus size={18} />
            <span>Thêm điểm dừng</span>
          </button>
        </div>

        {!p.portsLoading && !p.ports.length && (
          <Warning icon={<MapPin size={20} />} title="Chưa có cảng để lựa chọn">
            Hệ thống chưa có cảng đang hoạt động. Vui lòng liên hệ Admin để cấu
            hình cảng trước khi thêm điểm dừng.
          </Warning>
        )}

        {p.schedule && !p.scheduleDate && (
          <Warning
            icon={<CalendarDays size={20} />}
            title="Chưa xác định được ngày lịch trình"
          >
            Không thể lấy ngày thực tế từ dữ liệu lịch trình được truyền từ
            trang trước.
          </Warning>
        )}

        <ScheduleStopTable
          stops={p.scheduleStops}
          loading={p.loading}
          onEdit={p.openEdit}
          onDelete={p.handleDelete}
        />
      </section>

      <ScheduleStopFormModal
        open={p.showModal}
        stop={p.selectedStop}
        schedule={p.schedule}
        scheduleDate={p.scheduleDate}
        ports={p.ports}
        loading={p.saving || p.portsLoading}
        onClose={p.closeModal}
        onSubmit={p.handleSubmit}
      />
    </div>
  );
}

export default ManagerScheduleStops;
