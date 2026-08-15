import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft, CalendarDays, RefreshCw, Ship, Clock3 } from "lucide-react";

import useTours from "../hooks/useTours";
import useSchedules from "../hooks/useSchedules";

import ScheduleList from "../components/tour/ScheduleList";
import ScheduleFormModal from "../components/tour/ScheduleFormModal";

import "../styles/TourDetail.css";

function TourDetail() {
  const { tourId } = useParams();
  const navigate = useNavigate();

  const {
    selectedTour,
    loading: tourLoading,
    error: tourError,
    getTourById,
  } = useTours();

  const {
    schedules,
    loading: scheduleLoading,
    error: scheduleError,
    loadSchedules,
    createSchedule,
    updateSchedule,
    deleteSchedule,
  } = useSchedules();

  const [showScheduleModal, setShowScheduleModal] = useState(false);
  const [selectedSchedule, setSelectedSchedule] = useState(null);

  useEffect(() => {
    if (!tourId) {
      return;
    }

    getTourById(tourId);
    loadSchedules(tourId);
  }, [tourId]);

  const handleRefresh = async () => {
    if (!tourId) {
      return;
    }

    await Promise.all([getTourById(tourId), loadSchedules(tourId)]);
  };

  const handleCreateSchedule = () => {
    setSelectedSchedule(null);
    setShowScheduleModal(true);
  };

  const handleEditSchedule = (schedule) => {
    setSelectedSchedule(schedule);
    setShowScheduleModal(true);
  };

  const handleCloseScheduleModal = () => {
    setShowScheduleModal(false);
    setSelectedSchedule(null);
  };

  const handleSubmitSchedule = async (data) => {
    try {
      if (selectedSchedule) {
        await updateSchedule(tourId, selectedSchedule.id, data);
      } else {
        await createSchedule(tourId, data);
      }

      handleCloseScheduleModal();
      await loadSchedules(tourId);
    } catch (err) {
      console.error("SAVE SCHEDULE ERROR:", err);
    }
  };

  const handleDeleteSchedule = async (schedule) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa lịch trình "${schedule.name}" không?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      await deleteSchedule(tourId, schedule.id);

      await loadSchedules(tourId);
    } catch (err) {
      console.error("DELETE SCHEDULE ERROR:", err);
    }
  };

  if (tourLoading && !selectedTour) {
    return (
      <div className="scheduler-tour-detail-loading">
        <RefreshCw className="scheduler-spin" size={28} />

        <span>Đang tải thông tin tour...</span>
      </div>
    );
  }

  if (tourError && !selectedTour) {
    return (
      <div className="scheduler-tour-detail-error">
        <h2>Không thể tải Tour</h2>

        <p>{tourError}</p>

        <button type="button" onClick={() => navigate("/scheduler/tours")}>
          Quay lại danh sách
        </button>
      </div>
    );
  }

  if (!selectedTour) {
    return null;
  }

  const isPending = selectedTour.statusTrip === "APPROVAL_PENDING";

  const isApproved = selectedTour.statusTrip === "APPROVED";

  return (
    <div className="scheduler-tour-detail-page">
      <div className="scheduler-tour-detail-header">
        <button
          type="button"
          className="scheduler-back-button"
          onClick={() => navigate("/scheduler/tours")}
        >
          <ArrowLeft size={18} />

          <span>Quay lại</span>
        </button>

        <button
          type="button"
          className="scheduler-refresh-button"
          onClick={handleRefresh}
          disabled={tourLoading || scheduleLoading}
        >
          <RefreshCw
            size={18}
            className={tourLoading || scheduleLoading ? "scheduler-spin" : ""}
          />

          <span>Làm mới</span>
        </button>
      </div>

      <section className="scheduler-tour-info-card">
        <div className="scheduler-tour-info-main">
          <div className="scheduler-tour-code">{selectedTour.code}</div>

          <h1>{selectedTour.name}</h1>

          {selectedTour.description && <p>{selectedTour.description}</p>}
        </div>

        <div className="scheduler-tour-status-area">
          <span
            className={`scheduler-tour-status status-${selectedTour.statusTrip?.toLowerCase()}`}
          >
            {getTripStatusLabel(selectedTour.statusTrip)}
          </span>

          <span
            className={`scheduler-booking-status booking-${selectedTour.statusBooking?.toLowerCase()}`}
          >
            {getBookingStatusLabel(selectedTour.statusBooking)}
          </span>
        </div>
      </section>

      <section className="scheduler-tour-meta-grid">
        <div className="scheduler-tour-meta-card">
          <div className="scheduler-tour-meta-icon">
            <CalendarDays size={20} />
          </div>

          <div>
            <span>Thời gian Tour</span>

            <strong>
              Ngày {selectedTour.dayStart} - Ngày {selectedTour.dayEnd}
            </strong>
          </div>
        </div>

        <div className="scheduler-tour-meta-card">
          <div className="scheduler-tour-meta-icon">
            <Ship size={20} />
          </div>

          <div>
            <span>Du thuyền</span>

            <strong>
              {selectedTour.cruiseName || "Chưa được Operation phân công"}
            </strong>
          </div>
        </div>

        <div className="scheduler-tour-meta-card">
          <div className="scheduler-tour-meta-icon">
            <Clock3 size={20} />
          </div>

          <div>
            <span>Trạng thái booking</span>

            <strong>{getBookingStatusLabel(selectedTour.statusBooking)}</strong>
          </div>
        </div>
      </section>

      {isPending && (
        <div className="scheduler-tour-notice pending">
          <strong>Tour đang chờ Operation duyệt.</strong>

          <span>
            Scheduler có thể tiếp tục chuẩn bị lịch trình, trong khi việc phân
            công du thuyền và phê duyệt sẽ do Operation thực hiện.
          </span>
        </div>
      )}

      {isApproved && (
        <div className="scheduler-tour-notice approved">
          <strong>Tour đã được Operation duyệt.</strong>

          <span>Bạn có thể xem và cập nhật lịch trình của Tour.</span>
        </div>
      )}

      {scheduleError && (
        <div className="scheduler-tour-detail-error-inline">
          {scheduleError}
        </div>
      )}

      <section className="scheduler-schedule-section">
        <div className="scheduler-schedule-section-header">
          <div>
            <h2>Lịch trình Tour</h2>

            <p>Quản lý từng ngày và các cảng mà Tour sẽ ghé qua.</p>
          </div>

          <button
            type="button"
            className="scheduler-create-schedule-button"
            onClick={handleCreateSchedule}
          >
            <CalendarDays size={18} />

            <span>Thêm lịch trình</span>
          </button>
        </div>

        <ScheduleList
          schedules={schedules}
          loading={scheduleLoading}
          onEdit={handleEditSchedule}
          onDelete={handleDeleteSchedule}
        />
      </section>

      {showScheduleModal && (
        <ScheduleFormModal
          tourId={tourId}
          schedule={selectedSchedule}
          onClose={handleCloseScheduleModal}
          onSubmit={handleSubmitSchedule}
        />
      )}
    </div>
  );
}

function getTripStatusLabel(status) {
  switch (status) {
    case "APPROVAL_PENDING":
      return "Chờ duyệt";

    case "APPROVED":
      return "Đã duyệt";

    case "IN_PROGRESS":
      return "Đang diễn ra";

    case "COMPLETED":
      return "Đã hoàn thành";

    case "CANCELLED":
      return "Đã hủy";

    default:
      return status || "Chưa xác định";
  }
}

function getBookingStatusLabel(status) {
  switch (status) {
    case "NOT_OPEN":
      return "Chưa mở";

    case "WAITING":
      return "Đang chờ mở";

    case "OPEN":
      return "Đang mở";

    case "CLOSED":
      return "Đã đóng";

    default:
      return status || "Chưa xác định";
  }
}

export default TourDetail;
