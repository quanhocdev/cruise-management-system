import { useMemo, useState } from "react";
import { RefreshCw, Activity, AlertCircle, CheckCircle2 } from "lucide-react";

import useActivityCruiseTour from "../hooks/useActivityCruiseTour";

import ActivityCruiseTourTable from "../components/activity-cruise-tour/ActivityCruiseTourTable";
import ActivityCruiseTourDetail from "../components/activity-cruise-tour/ActivityCruiseTourDetail";
import ActivityCruiseTourConfigModal from "../components/activity-cruise-tour/ActivityCruiseTourConfigModal";
import OnboardStaffAssignmentModal from "../components/tour-config/OnboardStaffAssignmentModal";

import "../styles/ActivityCruiseTour.css";

const STATUS_TABS = [
  { value: "ALL", label: "Tất cả" },
  { value: "WAITING_CONFIG", label: "Chờ cấu hình" },
  { value: "CONFIGURED", label: "Đã cấu hình" },
  { value: "NOT_STARTED", label: "Chưa diễn ra" },
  { value: "IN_PROGRESS", label: "Đang diễn ra" },
  { value: "COMPLETED", label: "Đã kết thúc" },
];

const formatShortId = (id) => {
  if (!id) return "—";
  return id.length > 8 ? `${id.substring(0, 8)}...` : id;
};

const ActivityCruiseTour = () => {
  const {
    filteredActivities,
    statusFilter,
    setStatusFilter,
    tourSummaries,
    loading,
    error,
    completing,
    completeError,
    loadAllActivities,
    configureActivity,
    updateActivityConfig,
    completeTourConfiguration,
  } = useActivityCruiseTour();

  const [selectedTour, setSelectedTour] = useState(null);
  const [configTour, setConfigTour] = useState(null);

  // Hoạt động đang được chọn để phân công nhân viên
  const [assignmentActivity, setAssignmentActivity] = useState(null);

  const [selectedTourIdToComplete, setSelectedTourIdToComplete] = useState("");
  const [completeSuccess, setCompleteSuccess] = useState(false);

  const handleViewDetail = (tour) => setSelectedTour(tour);
  const handleOpenConfig = (tour) => setConfigTour(tour);
  const handleCloseDetail = () => setSelectedTour(null);
  const handleCloseConfig = () => setConfigTour(null);

  const handleOpenAssignment = (activity) => {
    if (!activity?.id || !activity?.tourId) {
      console.error("Activity assignment is missing id or tourId:", activity);
      return;
    }

    setAssignmentActivity(activity);
  };

  const handleCloseAssignment = () => {
    setAssignmentActivity(null);
  };

  const handleSubmitConfig = async (assignmentId, data) => {
    try {
      await configureActivity(assignmentId, data);
      setConfigTour(null);
    } catch (err) {
      console.error("CONFIG ACTIVITY TOUR ERROR:", err);
    }
  };

  const handleUpdateConfig = async (assignmentId, data) => {
    try {
      const updatedActivity = await updateActivityConfig(assignmentId, data);
      setConfigTour(null);
      return updatedActivity;
    } catch (err) {
      console.error("UPDATE ACTIVITY TOUR CONFIG ERROR:", err);
    }
  };

  const selectedTourSummary = useMemo(
    () =>
      tourSummaries.find((tour) => tour.tourId === selectedTourIdToComplete),
    [tourSummaries, selectedTourIdToComplete],
  );

  const canComplete =
    !!selectedTourIdToComplete &&
    (!selectedTourSummary || !selectedTourSummary.completed);

  const handleCompleteTour = async () => {
    if (!selectedTourIdToComplete || !canComplete) return;

    try {
      setCompleteSuccess(false);
      await completeTourConfiguration(selectedTourIdToComplete);
      setCompleteSuccess(true);
      setSelectedTourIdToComplete("");
    } catch (err) {
      console.error("COMPLETE TOUR ERROR:", err);
    }
  };

  return (
    <div className="activity-cruise-tour-page">
      {/* HEADER */}
      <div className="activity-cruise-tour-page__header">
        <div className="activity-cruise-tour-page__heading">
          <div className="activity-cruise-tour-page__icon">
            <Activity size={22} />
          </div>

          <div>
            <h1 className="activity-cruise-tour-page__title">
              Cấu hình hoạt động Tour
            </h1>
            <p className="activity-cruise-tour-page__subtitle">
              Quản lý, cấu hình hoạt động và phân công nhân viên ONBOARD
            </p>
          </div>
        </div>

        <button
          type="button"
          className="activity-cruise-tour-page__refresh"
          onClick={loadAllActivities}
          disabled={loading}
        >
          <RefreshCw
            size={17}
            className={
              loading ? "activity-cruise-tour-page__refresh-icon--spinning" : ""
            }
          />
          <span>Làm mới</span>
        </button>
      </div>

      {error && (
        <div className="activity-cruise-tour-page__error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* HOÀN THÀNH CẤU HÌNH TOUR */}
      <div className="activity-cruise-tour-page__complete-box">
        <div className="activity-cruise-tour-page__complete-info">
          <strong>Hoàn thành cấu hình Tour</strong>
          <p>
            Chọn Tour và bấm Hoàn thành khi tất cả hoạt động của Tour đó đã được
            cấu hình xong. Hệ thống sẽ gửi thông tin sang các dịch vụ liên quan.
          </p>
        </div>

        <div className="activity-cruise-tour-page__complete-controls">
          <select
            className="activity-cruise-tour-page__complete-select"
            value={selectedTourIdToComplete}
            onChange={(e) => {
              setSelectedTourIdToComplete(e.target.value);
              setCompleteSuccess(false);
            }}
            disabled={completing}
          >
            <option value="">— Chọn Tour —</option>

            {tourSummaries.map((tour) => (
              <option
                key={tour.tourId}
                value={tour.tourId}
                disabled={tour.completed}
              >
                {tour.tourCode || formatShortId(tour.tourId)} (
                {tour.configuredCount}/{tour.total} đã cấu hình)
                {tour.completed ? " - Đã hoàn thành" : ""}
              </option>
            ))}
          </select>

          <button
            type="button"
            className="activity-cruise-tour-page__complete-button"
            onClick={handleCompleteTour}
            disabled={!canComplete || completing}
          >
            <CheckCircle2 size={16} />
            {completing ? "Đang xử lý..." : "Hoàn thành cấu hình"}
          </button>
        </div>

        {selectedTourIdToComplete &&
          selectedTourSummary &&
          !selectedTourSummary.completed &&
          !canComplete && (
            <span className="activity-cruise-tour-page__complete-hint">
              Tour này còn hoạt động chưa được cấu hình xong.
            </span>
          )}

        {completeError && (
          <span className="activity-cruise-tour-page__complete-error">
            {completeError}
          </span>
        )}

        {completeSuccess && (
          <span className="activity-cruise-tour-page__complete-success">
            Đã hoàn thành cấu hình Tour thành công.
          </span>
        )}
      </div>

      {/* DANH SÁCH HOẠT ĐỘNG */}
      <section className="activity-cruise-tour-page__section">
        <div className="activity-cruise-tour-page__section-header">
          <div>
            <h2 className="activity-cruise-tour-page__section-title">
              Danh sách hoạt động
            </h2>
            <p className="activity-cruise-tour-page__section-description">
              Quản lý cấu hình và phân công nhân viên phụ trách từng hoạt động.
            </p>
          </div>

          <span className="activity-cruise-tour-page__count">
            {filteredActivities.length}
          </span>
        </div>

        <div className="activity-cruise-tour-page__filters">
          {STATUS_TABS.map((tab) => (
            <button
              key={tab.value}
              type="button"
              className={`activity-cruise-tour-page__filter-btn ${
                statusFilter === tab.value
                  ? "activity-cruise-tour-page__filter-btn--active"
                  : ""
              }`}
              onClick={() => setStatusFilter(tab.value)}
            >
              {tab.label}
            </button>
          ))}
        </div>

        <ActivityCruiseTourTable
          activities={filteredActivities}
          loading={loading}
          onViewDetail={handleViewDetail}
          onConfigure={handleOpenConfig}
          onAssign={handleOpenAssignment}
        />
      </section>

      {/* MODAL XEM CHI TIẾT */}
      {selectedTour && (
        <div className="activity-cruise-tour-page__modal-overlay">
          <div className="activity-cruise-tour-page__detail-modal">
            <ActivityCruiseTourDetail
              activity={selectedTour}
              onClose={handleCloseDetail}
            />
          </div>
        </div>
      )}

      {/* MODAL CẤU HÌNH HOẠT ĐỘNG */}
      {configTour && (
        <ActivityCruiseTourConfigModal
          assignment={configTour}
          onClose={handleCloseConfig}
          onSubmit={handleSubmitConfig}
          onUpdate={handleUpdateConfig}
          submitting={loading}
        />
      )}

      {/* MODAL PHÂN CÔNG NHÂN VIÊN */}
      {assignmentActivity && (
        <OnboardStaffAssignmentModal
          open={true}
          assignment={{
            tourId: assignmentActivity.tourId,
            activityCruiseTourId: assignmentActivity.id,
          }}
          assignmentType="ACTIVITY_CRUISE"
          onClose={handleCloseAssignment}
        />
      )}
    </div>
  );
};

export default ActivityCruiseTour;
