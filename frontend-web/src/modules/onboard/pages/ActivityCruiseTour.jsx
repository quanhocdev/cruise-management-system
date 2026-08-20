import { useState } from "react";
import { RefreshCw, Activity, AlertCircle } from "lucide-react";

import useActivityCruiseTour from "../hooks/useActivityCruiseTour";

import ActivityCruiseTourTable from "../components/activity-cruise-tour/ActivityCruiseTourTable";
import ActivityCruiseTourDetail from "../components/activity-cruise-tour/ActivityCruiseTourDetail";
import ActivityCruiseTourConfigModal from "../components/activity-cruise-tour/ActivityCruiseTourConfigModal";

import "../styles/ActivityCruiseTour.css";

const ActivityCruiseTour = () => {
  const {
    pendingActivities,
    loading,
    error,
    loadPendingActivities,
    configureActivity,
    updateActivityConfig,
  } = useActivityCruiseTour();

  const [selectedTour, setSelectedTour] = useState(null);
  const [configTour, setConfigTour] = useState(null);

  // =====================================================
  // DETAIL
  // =====================================================

  const handleViewDetail = (tour) => {
    setSelectedTour(tour);
  };

  // =====================================================
  // CONFIG
  // =====================================================

  const handleOpenConfig = (tour) => {
    setConfigTour(tour);
  };

  const handleCloseDetail = () => {
    setSelectedTour(null);
  };

  const handleCloseConfig = () => {
    setConfigTour(null);
  };

  // =====================================================
  // SUBMIT CONFIG
  // =====================================================

  const handleSubmitConfig = async (assignmentId, data) => {
    try {
      await configureActivity(assignmentId, data);

      setConfigTour(null);
    } catch (err) {
      // Hook đã xử lý error.
      // Giữ modal mở để người dùng sửa dữ liệu.
      console.error("CONFIG ACTIVITY TOUR ERROR:", err);
    }
  };

  // =====================================================
  // UPDATE CONFIG
  // =====================================================

  const handleUpdateConfig = async (assignmentId, data) => {
    try {
      const updatedActivity = await updateActivityConfig(assignmentId, data);

      setConfigTour(null);

      return updatedActivity;
    } catch (err) {
      console.error("UPDATE ACTIVITY TOUR CONFIG ERROR:", err);
    }
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="activity-cruise-tour-page">
      {/* =================================================
          HEADER
          ================================================= */}

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
          onClick={loadPendingActivities}
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

      {/* =================================================
          ERROR
          ================================================= */}

      {error && (
        <div className="activity-cruise-tour-page__error">
          <AlertCircle size={18} />

          <span>{error}</span>
        </div>
      )}

      {/* =================================================
          CONTENT
          ================================================= */}

      <section className="activity-cruise-tour-page__section">
        <div className="activity-cruise-tour-page__section-header">
          <div>
            <h2 className="activity-cruise-tour-page__section-title">
              Hoạt động chờ cấu hình
            </h2>

            <p className="activity-cruise-tour-page__section-description">
              Các hoạt động thuộc Tour đã được duyệt và đang chờ Onboard cấu
              hình.
            </p>
          </div>

          <span className="activity-cruise-tour-page__count">
            {pendingActivities.length}
          </span>
        </div>

        <ActivityCruiseTourTable
          activities={pendingActivities}
          loading={loading}
          onViewDetail={handleViewDetail}
          onConfigure={handleOpenConfig}
        />
      </section>

      {/* =================================================
          DETAIL
          ================================================= */}

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

      {/* =================================================
          CONFIG
          ================================================= */}

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
