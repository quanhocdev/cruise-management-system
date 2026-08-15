import { useEffect, useMemo, useState } from "react";
import { ArrowLeft, CalendarDays, RefreshCw } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";

import useSchedules from "../hooks/useSchedules";
import scheduleService from "../services/scheduleService";
import tourService from "../services/tourService";

import ScheduleFormModal from "../components/schedule/ScheduleFormModal";
import ScheduleCard from "../components/schedule/ScheduleCard";

import "../styles/ManagerSchedule.css";

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

  const [tour, setTour] = useState(null);
  const [tourLoading, setTourLoading] = useState(false);

  const [showModal, setShowModal] = useState(false);
  const [selectedSchedule, setSelectedSchedule] = useState(null);

  const [selectedDay, setSelectedDay] = useState(null);

  useEffect(() => {
    loadTour();
    loadSchedules();
  }, [tourId]);

  const loadTour = async () => {
    if (!tourId) {
      return;
    }

    setTourLoading(true);

    try {
      const data = await tourService.getTourById(tourId);

      setTour(data);
    } catch (err) {
      console.error("LOAD TOUR ERROR:", err);
    } finally {
      setTourLoading(false);
    }
  };

  const tourDays = useMemo(() => {
    if (!tour?.startDate || !tour?.endDate) {
      return [];
    }

    const result = [];

    const start = new Date(`${tour.startDate}T00:00:00`);
    const end = new Date(`${tour.endDate}T00:00:00`);

    let current = new Date(start);
    let dayNumber = 1;

    while (current <= end) {
      const year = current.getFullYear();
      const month = String(current.getMonth() + 1).padStart(2, "0");
      const day = String(current.getDate()).padStart(2, "0");

      const realDay = `${year}-${month}-${day}`;

      result.push({
        dayNumber,
        realDay,
      });

      current.setDate(current.getDate() + 1);
      dayNumber += 1;
    }

    return result;
  }, [tour]);

  const getScheduleForDay = (dayNumber) => {
    return schedules.find((schedule) => schedule.dayNumber === dayNumber);
  };

  const formatDate = (dateString) => {
    if (!dateString) {
      return "-";
    }

    const date = new Date(`${dateString}T00:00:00`);

    return date.toLocaleDateString("vi-VN", {
      weekday: "long",
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

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
      if (selectedSchedule) {
        const result = await updateSchedule(selectedSchedule.id, {
          name: data.name,
          description: data.description,
        });

        if (result) {
          handleCloseModal();
        }

        return;
      }

      const result = await createSchedule({
        name: data.name,
        description: data.description,
        dayNumber: data.dayNumber,
        realDay: data.realDay,
      });

      if (result) {
        handleCloseModal();
      }
    } catch (err) {
      console.error("SAVE SCHEDULE ERROR:", err);
    }
  };

  const handleDelete = async (schedule) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa lịch trình "${schedule.name}" không?`,
    );

    if (!confirmed) {
      return;
    }

    await deleteSchedule(schedule.id);
  };

  const handleViewStops = (schedule) => {
    // Ép Number() để tránh lỗi so sánh kiểu String vs Number
    const day = tourDays.find(
      (item) => Number(item.dayNumber) === Number(schedule.dayNumber),
    );

    navigate(`/scheduler/tours/${tourId}/schedules/${schedule.id}/stops`, {
      state: {
        schedule: {
          ...schedule,
          realDay:
            day?.realDay || schedule.realDay || schedule.scheduleDate || "",
        },
        scheduleDate:
          day?.realDay || schedule.realDay || schedule.scheduleDate || "",
      },
    });
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
          onClick={() => {
            loadTour();
            loadSchedules();
          }}
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
          <div>
            <span>Tour</span>
            <strong>{tour.code}</strong>
          </div>

          <div>
            <span>Tên tour</span>
            <strong>{tour.name}</strong>
          </div>

          <div>
            <span>Bắt đầu</span>
            <strong>{formatDate(tour.startDate)}</strong>
          </div>

          <div>
            <span>Kết thúc</span>
            <strong>{formatDate(tour.endDate)}</strong>
          </div>

          <div>
            <span>Số ngày</span>
            <strong>{tourDays.length} ngày</strong>
          </div>
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
