import { useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";

import useShoreTourConfiguration from "../hooks/useShoreTourConfiguration";
import useVisitTours from "../hooks/useVisitTours";
import "../styles/VisitTourConfiguration.css";
import VisitTourFilter from "../components/visit-tour/VisitTourFilter";
import VisitTourSchedule from "../components/visit-tour/VisitTourSchedule";
import VisitTourFormModal from "../components/visit-tour/VisitTourFormModal";

const STATUS_OPTIONS = [
  {
    value: "",
    label: "Tất cả trạng thái",
  },
  {
    value: "NOT_STARTED",
    label: "Chưa bắt đầu",
  },
  {
    value: "IN_PROGRESS",
    label: "Đang diễn ra",
  },
  {
    value: "COMPLETED",
    label: "Đã hoàn thành",
  },
  {
    value: "DELAYED",
    label: "Trì hoãn",
  },
  {
    value: "CANCELLED",
    label: "Đã hủy",
  },
];

function VisitTourConfiguration() {
  /*
   * =====================================================
   * URL SEARCH PARAMS & FILTER STATE
   * =====================================================
   */

  const [searchParams, setSearchParams] = useSearchParams();
  const tourId = searchParams.get("tourId");
  const status = searchParams.get("status") || "";

  /*
   * =====================================================
   * MODAL STATE
   * =====================================================
   */

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingVisitTour, setEditingVisitTour] = useState(null);
  const [selectedScheduleStop, setSelectedScheduleStop] = useState(null);

  /*
   * =====================================================
   * CONFIGURATION HOOK
   * =====================================================
   */

  const {
    configuration,
    loading: configurationLoading,
    error: configurationError,
    reload: fetchConfiguration,
  } = useShoreTourConfiguration(tourId, status || null);

  /*
   * =====================================================
   * VISIT TOUR CRUD HOOK
   * =====================================================
   */

  const {
    createVisitTour,
    updateVisitTour,
    deleteVisitTour,
    loading: visitTourLoading,
    error: visitTourError,
  } = useVisitTours();

  /*
   * =====================================================
   * STATUS OPTIONS
   * =====================================================
   */

  const statusOptions = useMemo(() => STATUS_OPTIONS, []);

  /*
   * =====================================================
   * FILTER CHANGE HANDLER
   * =====================================================
   */

  const handleStatusFilterChange = (newStatus) => {
    const nextParams = new URLSearchParams(searchParams);

    if (newStatus) {
      nextParams.set("status", newStatus);
    } else {
      nextParams.delete("status");
    }

    setSearchParams(nextParams);
  };

  /*
   * =====================================================
   * OPEN CREATE
   * =====================================================
   */

  const handleCreate = (scheduleStop) => {
    setSelectedScheduleStop(scheduleStop);
    setEditingVisitTour(null);
    setIsModalOpen(true);
  };

  /*
   * =====================================================
   * OPEN EDIT
   * =====================================================
   */

  const handleEdit = (visitTour) => {
    setEditingVisitTour(visitTour);
    setSelectedScheduleStop(null);
    setIsModalOpen(true);
  };

  /*
   * =====================================================
   * DELETE
   * =====================================================
   */

  const handleDelete = async (visitTour) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa Visit Tour "${visitTour.name}" không?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      await deleteVisitTour(visitTour.id);

      await fetchConfiguration();
    } catch (error) {
      console.error("DELETE VISIT TOUR ERROR:", error);
    }
  };

  /*
   * =====================================================
   * CHANGE STATUS
   * =====================================================
   */

  const handleStatusChange = async (visitTour, newStatus) => {
    if (!newStatus || newStatus === visitTour.status) {
      return;
    }

    try {
      await updateVisitTour(visitTour.id, {
        status: newStatus,
      });

      await fetchConfiguration();
    } catch (error) {
      console.error("UPDATE VISIT TOUR STATUS ERROR:", error);
    }
  };

  /*
   * =====================================================
   * SUBMIT FORM
   * =====================================================
   */

  const handleSubmit = async (formData) => {
    try {
      if (editingVisitTour) {
        await updateVisitTour(editingVisitTour.id, formData);
      } else {
        await createVisitTour(selectedScheduleStop.scheduleStopId, formData);
      }

      setIsModalOpen(false);
      setEditingVisitTour(null);
      setSelectedScheduleStop(null);

      await fetchConfiguration();
    } catch (error) {
      console.error("SAVE VISIT TOUR ERROR:", error);

      throw error;
    }
  };

  /*
   * =====================================================
   * CLOSE MODAL
   * =====================================================
   */

  const handleCloseModal = () => {
    if (visitTourLoading) {
      return;
    }

    setIsModalOpen(false);
    setEditingVisitTour(null);
    setSelectedScheduleStop(null);
  };

  /*
   * =====================================================
   * NO TOUR ID
   * =====================================================
   */

  if (!tourId) {
    return (
      <div className="visit-tour-configuration">
        <div className="visit-tour-configuration-empty">
          <h2>Chưa chọn Tour</h2>

          <p>Không tìm thấy tourId trong URL.</p>

          <p>
            Ví dụ:
            <br />
            <code>/shore/visit-tour-configuration?tourId=TOUR_ID</code>
          </p>
        </div>
      </div>
    );
  }

  /*
   * =====================================================
   * RENDER
   * =====================================================
   */

  return (
    <div className="visit-tour-configuration">
      {/* =================================================
          PAGE HEADER
          ================================================= */}

      <div className="visit-tour-configuration-header">
        <div>
          <h1>Cấu hình Visit Tour</h1>

          <p>Quản lý các tour tham quan trên bờ theo lịch trình của Tour.</p>
        </div>
      </div>

      {/* =================================================
          ERROR
          ================================================= */}

      {configurationError && (
        <div className="visit-tour-configuration-error">
          {configurationError.message || String(configurationError)}
        </div>
      )}

      {visitTourError && (
        <div className="visit-tour-configuration-error">
          {visitTourError.message || String(visitTourError)}
        </div>
      )}

      {/* =================================================
          FILTER
          ================================================= */}

      <VisitTourFilter
        status={status}
        onStatusChange={handleStatusFilterChange}
        statusOptions={statusOptions}
      />

      {/* =================================================
          LOADING
          ================================================= */}

      {configurationLoading ? (
        <div className="visit-tour-configuration-loading">
          Đang tải cấu hình Tour...
        </div>
      ) : !configuration ? (
        <div className="visit-tour-configuration-empty">
          Không có dữ liệu cấu hình.
        </div>
      ) : (
        <>
          {/* =============================================
              TOUR INFORMATION
              ============================================= */}

          <section className="visit-tour-tour-info">
            <div className="visit-tour-tour-info-main">
              <span className="visit-tour-tour-code">
                {configuration.tourCode}
              </span>

              <h2>{configuration.tourName}</h2>

              {configuration.tourDescription && (
                <p>{configuration.tourDescription}</p>
              )}
            </div>

            <div className="visit-tour-tour-info-date">
              <span>{configuration.startDate}</span>

              <span>→</span>

              <span>{configuration.endDate}</span>
            </div>
          </section>

          {/* =============================================
              SCHEDULES
              ============================================= */}

          <div className="visit-tour-schedules">
            {configuration.schedules?.length === 0 ? (
              <div className="visit-tour-configuration-empty">
                Tour chưa có lịch trình.
              </div>
            ) : (
              configuration.schedules.map((schedule) => (
                <VisitTourSchedule
                  key={schedule.scheduleId}
                  schedule={schedule}
                  onCreate={handleCreate}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                  onStatusChange={handleStatusChange}
                />
              ))
            )}
          </div>
        </>
      )}

      {/* =================================================
          CREATE / EDIT MODAL
          ================================================= */}

      {isModalOpen && (
        <VisitTourFormModal
          visitTour={editingVisitTour}
          scheduleStop={selectedScheduleStop}
          loading={visitTourLoading}
          onClose={handleCloseModal}
          onSubmit={handleSubmit}
        />
      )}
    </div>
  );
}

export default VisitTourConfiguration;
