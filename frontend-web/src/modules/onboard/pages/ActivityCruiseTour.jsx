import { useMemo, useState } from "react";
import { RefreshCw, Activity, AlertCircle, CheckCircle2 } from "lucide-react";

import useActivityCruiseTour from "../hooks/useActivityCruiseTour";

import ActivityCruiseTourTable from "../components/activity-cruise-tour/ActivityCruiseTourTable";
import ActivityCruiseTourDetail from "../components/activity-cruise-tour/ActivityCruiseTourDetail";
import ActivityCruiseTourConfigModal from "../components/activity-cruise-tour/ActivityCruiseTourConfigModal";

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

  // Tour đang được chọn để "Hoàn thành cấu hình"
  const [selectedTourIdToComplete, setSelectedTourIdToComplete] = useState("");
  const [completeSuccess, setCompleteSuccess] = useState(false);

  const handleViewDetail = (tour) => setSelectedTour(tour);
  const handleOpenConfig = (tour) => setConfigTour(tour);
  const handleCloseDetail = () => setSelectedTour(null);
  const handleCloseConfig = () => setConfigTour(null);

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

  // Tour đang chọn có đủ điều kiện hoàn thành không (tất cả activity CONFIGURED)
  const selectedTourSummary = useMemo(
    () =>
      tourSummaries.find((tour) => tour.tourId === selectedTourIdToComplete),
    [tourSummaries, selectedTourIdToComplete],
  );

  const canComplete =
    !!selectedTourSummary &&
    selectedTourSummary.total > 0 &&
    selectedTourSummary.configuredCount === selectedTourSummary.total;

  const handleCompleteTour = async () => {
    if (!selectedTourIdToComplete || !canComplete) return;

    try {
      setCompleteSuccess(false);
      await completeTourConfiguration(selectedTourIdToComplete);
      setCompleteSuccess(true);
      setSelectedTourIdToComplete("");
    } catch (err) {
      console.error("COMPLETE TOUR ERROR:", err);
      // completeError đã được hook set, hiển thị bên dưới
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
              Quản lý và cấu hình các hoạt động được phân công cho Tour
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

      {/* ERROR chung */}
      {error && (
        <div className="activity-cruise-tour-page__error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* ============ COMPLETE TOUR CONFIGURATION ============ */}
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
              <option key={tour.tourId} value={tour.tourId}>
                {tour.tourCode || formatShortId(tour.tourId)} (
                {tour.configuredCount}/{tour.total} đã cấu hình)
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

        {selectedTourIdToComplete && !canComplete && (
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

      {/* MAIN SECTION (giữ nguyên) */}
      <section className="activity-cruise-tour-page__section">
        <div className="activity-cruise-tour-page__section-header">
          <div>
            <h2 className="activity-cruise-tour-page__section-title">
              Danh sách hoạt động
            </h2>
            <p className="activity-cruise-tour-page__section-description">
              Toàn bộ hoạt động thuộc Tour, lọc theo trạng thái cấu hình.
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
        />
      </section>

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

      {configTour && (
        <ActivityCruiseTourConfigModal
          assignment={configTour}
          onClose={handleCloseConfig}
          onSubmit={handleSubmitConfig}
          submitting={loading}
        />
      )}
    </div>
  );
};

export default ActivityCruiseTour;
