// src/modules/scheduler/pages/ManagerSchedule.jsx
// (SỬA file có sẵn) Không còn gọi tourService trực tiếp: dùng hooks/useTourDetail.js

import { useEffect, useState } from "react";
import { ArrowLeft, CalendarDays, RefreshCw } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";

import useSchedules from "../hooks/useSchedules";
import useTourDetail from "../hooks/useTourDetail";
import "../styles/TourTable.css";
import ScheduleFormModal from "../components/schedule/ScheduleFormModal";
import ScheduleCard from "../components/schedule/ScheduleCard";

import "../styles/ManagerSchedule.css";

const formatDate = (dateString) => {
  if (!dateString) return "-";

  return new Date(`${dateString}T00:00:00`).toLocaleDateString("vi-VN", {
    weekday: "long",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

function ManagerSchedule() {
  const { tourId } = useParams();
  const navigate = useNavigate();

  const {
    schedules,
    loading,
    saving,
    error,
    loadSchedules,
    createSchedule,
    updateSchedule,
    deleteSchedule,
  } = useSchedules(tourId);

  const {
    tour,
    tourDays,
    loading: tourLoading,
    reload: reloadTour,
  } = useTourDetail(tourId);

  const [showModal, setShowModal] = useState(false);
  const [selectedSchedule, setSelectedSchedule] = useState(null);
  const [selectedDay, setSelectedDay] = useState(null);

  useEffect(() => {
    loadSchedules();
    // chỉ tải lại khi đổi tour
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tourId]);

  const getScheduleForDay = (dayNumber) =>
    schedules.find((schedule) => schedule.dayNumber === dayNumber);

  // ---------- MODAL ----------
  const handleCreateForDay = (day) => {
    setSelectedSchedule(null);
    setSelectedDay(day);
    setShowModal(true);
  };

  const handleEdit = (schedule) => {
    setSelectedSchedule(schedule);
    setSelectedDay({
      dayNumber: schedule.dayNumber,
      realDay: schedule.realDay,
    });
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedSchedule(null);
    setSelectedDay(null);
  };

  const handleSubmit = async (data) => {
    try {
      const result = selectedSchedule
        ? await updateSchedule(selectedSchedule.id, {
            name: data.name,
            description: data.description,
          })
        : await createSchedule({
            name: data.name,
            description: data.description,
            dayNumber: data.dayNumber,
            realDay: data.realDay,
          });

      if (result) handleCloseModal();
    } catch (err) {
      console.error("SAVE SCHEDULE ERROR:", err);
    }
  };

  const handleDelete = async (schedule) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa lịch trình "${schedule.name}" không?`,
    );
    if (!confirmed) return;

    await deleteSchedule(schedule.id);
  };

  const handleViewStops = (schedule) => {
    // Ép Number() để tránh lỗi so sánh String vs Number
    const day = tourDays.find(
      (item) => Number(item.dayNumber) === Number(schedule.dayNumber),
    );
    const realDay =
      day?.realDay || schedule.realDay || schedule.scheduleDate || "";

    navigate(`/scheduler/tours/${tourId}/schedules/${schedule.id}/stops`, {
      state: { schedule: { ...schedule, realDay }, scheduleDate: realDay },
    });
  };

  const handleRefresh = () => {
    reloadTour();
    loadSchedules();
  };

  if (tourLoading) {
    return (
      <div className="scheduler-schedule-page">
        <div className="schedule-list-state">
          <div className="tour-table-spinner" />
          <span>Đang tải thông tin tour...</span>
        </div>
      </div>
    );
  }

  const tourInfo = tour
    ? [
        ["Tour", tour.code],
        ["Tên tour", tour.name],
        ["Bắt đầu", formatDate(tour.startDate)],
        ["Kết thúc", formatDate(tour.endDate)],
        ["Số ngày", `${tourDays.length} ngày`],
      ]
    : [];

  return (
    <div className="scheduler-schedule-page">
      <div className="scheduler-schedule-header">
        <div className="scheduler-schedule-header-left">
          <button
            type="button"
            className="scheduler-back-button"
            onClick={() => navigate("/scheduler/tours")}
          >
            <ArrowLeft size={18} />
            Quay lại
          </button>

          <div>
            <h1>Quản lý lịch trình</h1>
            <p>
              {tour?.code} — {tour?.name}
            </p>
          </div>
        </div>

        <button
          type="button"
          className="scheduler-tour-refresh-button"
          onClick={handleRefresh}
          disabled={loading}
        >
          <RefreshCw size={18} className={loading ? "scheduler-spin" : ""} />
          <span>Làm mới</span>
        </button>
      </div>

      {error && (
        <div className="scheduler-tour-error">
          <strong>Không thể tải lịch trình.</strong>
          <span>{error}</span>
        </div>
      )}

      {tour && (
        <div className="scheduler-tour-info">
          {tourInfo.map(([label, value]) => (
            <div key={label}>
              <span>{label}</span>
              <strong>{value}</strong>
            </div>
          ))}
        </div>
      )}

      <section className="schedule-day-list">
        <div className="schedule-list-header">
          <div>
            <div className="section-title">
              <CalendarDays size={21} />
              <h2>Các ngày trong tour</h2>
            </div>

            <p>
              Mỗi ngày trong khoảng thời gian của tour có thể được cấu hình
              thành một lịch trình.
            </p>
          </div>
        </div>

        {!tourDays.length ? (
          <div className="schedule-list-empty">
            <CalendarDays size={44} />
            <h3>Tour chưa có thời gian</h3>
            <p>
              Không thể tạo lịch trình khi tour chưa có ngày bắt đầu và ngày kết
              thúc.
            </p>
          </div>
        ) : (
          <div className="schedule-day-grid">
            {tourDays.map((day) => {
              const schedule = getScheduleForDay(day.dayNumber);

              return (
                <div
                  key={day.dayNumber}
                  className={`schedule-day-item ${
                    schedule ? "has-schedule" : "no-schedule"
                  }`}
                >
                  <div className="schedule-day-item-header">
                    <div className="schedule-day-number">
                      <CalendarDays size={19} />
                      <strong>Ngày {day.dayNumber}</strong>
                    </div>

                    <span>{formatDate(day.realDay)}</span>
                  </div>

                  {schedule ? (
                    <ScheduleCard
                      schedule={schedule}
                      onEdit={handleEdit}
                      onDelete={handleDelete}
                      onView={handleViewStops}
                    />
                  ) : (
                    <div className="schedule-day-empty">
                      <CalendarDays size={32} />
                      <h3>Chưa có lịch trình</h3>
                      <p>Chưa cấu hình nội dung cho ngày này.</p>

                      <button
                        type="button"
                        className="primary-button"
                        onClick={() => handleCreateForDay(day)}
                      >
                        Tạo lịch trình
                      </button>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </section>

      <ScheduleFormModal
        open={showModal}
        schedule={selectedSchedule}
        dayNumber={selectedDay?.dayNumber}
        realDay={selectedDay?.realDay || ""}
        loading={saving}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
      />
    </div>
  );
}

export default ManagerSchedule;
