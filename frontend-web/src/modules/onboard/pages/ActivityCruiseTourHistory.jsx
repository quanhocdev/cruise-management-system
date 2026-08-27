// src/modules/onboard/pages/ActivityCruiseTourHistory.jsx

// src/modules/onboard/pages/ActivityCruiseTourHistory.jsx

import { useEffect, useState } from "react";
import {
  RefreshCw,
  History,
  Eye,
  CalendarDays,
  Layers3,
  AlertCircle,
} from "lucide-react";

import useActivityCruiseTourHistory from "../hooks/useActivityCruiseTourHistory";
import ActivityCruiseTourHistoryDetail from "../components/activity-cruise-tour/ActivityCruiseTourHistoryDetail";

import "../styles/ActivityCruiseTourHistory.css";

const formatDateTime = (value) => {
  if (!value) return "—";

  return new Date(value).toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatShortId = (id) => {
  if (!id) return "—";

  return id.length > 12 ? `${id.substring(0, 12)}...` : id;
};

const ActivityCruiseTourHistory = () => {
  const {
    history,
    configurationDetail,
    loading,
    detailLoading,
    error,
    detailError,
    loadHistory,
    loadConfigurationDetail,
    clearConfigurationDetail,
  } = useActivityCruiseTourHistory();

  const [selectedHistory, setSelectedHistory] = useState(null);

  useEffect(() => {
    loadHistory();
  }, [loadHistory]);

  const handleRefresh = async () => {
    await loadHistory();
  };

  const handleViewDetail = async (historyItem) => {
    setSelectedHistory(historyItem);
    await loadConfigurationDetail(historyItem.tourId);
  };

  const handleCloseDetail = () => {
    setSelectedHistory(null);
    clearConfigurationDetail();
  };

  return (
    <div className="activity-cruise-tour-history-page">
      {/* =====================================================
          HEADER
          ===================================================== */}

      <div className="activity-cruise-tour-history-page__header">
        <div className="activity-cruise-tour-history-page__heading">
          <div className="activity-cruise-tour-history-page__icon">
            <History size={22} />
          </div>

          <div>
            <h1 className="activity-cruise-tour-history-page__title">
              Lịch sử cấu hình Tour
            </h1>

            <p className="activity-cruise-tour-history-page__subtitle">
              Theo dõi các Tour đã hoàn thành cấu hình hoạt động trên tàu
            </p>
          </div>
        </div>

        <button
          type="button"
          className="activity-cruise-tour-history-page__refresh"
          onClick={handleRefresh}
          disabled={loading}
        >
          <RefreshCw
            size={17}
            className={
              loading
                ? "activity-cruise-tour-history-page__refresh-icon--spinning"
                : ""
            }
          />

          <span>Làm mới</span>
        </button>
      </div>

      {/* =====================================================
          ERROR
          ===================================================== */}

      {error && (
        <div className="activity-cruise-tour-history-page__error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* =====================================================
          SUMMARY
          ===================================================== */}

      <div className="activity-cruise-tour-history-page__summary">
        <div className="activity-cruise-tour-history-page__summary-card">
          <div className="activity-cruise-tour-history-page__summary-icon">
            <History size={19} />
          </div>

          <div>
            <span>Tổng Tour đã hoàn thành</span>
            <strong>{history.length}</strong>
          </div>
        </div>
      </div>

      {/* =====================================================
          TABLE SECTION
          ===================================================== */}

      <section className="activity-cruise-tour-history-page__section">
        <div className="activity-cruise-tour-history-page__section-header">
          <div>
            <h2 className="activity-cruise-tour-history-page__section-title">
              Danh sách lịch sử
            </h2>

            <p className="activity-cruise-tour-history-page__section-description">
              Các Tour đã hoàn thành cấu hình Activity Cruise
            </p>
          </div>

          <span className="activity-cruise-tour-history-page__count">
            {histories.length}
          </span>
        </div>

        {loading ? (
          <div className="activity-cruise-tour-history-page__loading">
            <RefreshCw
              size={20}
              className="activity-cruise-tour-history-page__loading-icon"
            />

            <span>Đang tải lịch sử cấu hình...</span>
          </div>
        ) : history.length === 0 ? (
          <div className="activity-cruise-tour-history-page__empty">
            <History size={32} />

            <strong>Chưa có lịch sử cấu hình</strong>

            <span>
              Những Tour sau khi hoàn thành cấu hình sẽ xuất hiện tại đây.
            </span>
          </div>
        ) : (
          <div className="activity-cruise-tour-history-page__table-wrapper">
            <table className="activity-cruise-tour-history-page__table">
              <thead>
                <tr>
                  <th>Tour</th>
                  <th>Số cấu hình</th>
                  <th>Thời gian hoàn thành</th>
                  <th>Trạng thái</th>
                  <th></th>
                </tr>
              </thead>

              <tbody>
                {history.map((history) => (
                  <tr key={history.id}>
                    {/* TOUR */}
                    <td>
                      <div className="activity-cruise-tour-history-page__tour">
                        <div className="activity-cruise-tour-history-page__tour-icon">
                          <Layers3 size={17} />
                        </div>

                        <div>
                          <strong>{formatShortId(history.tourId)}</strong>

                          <span>ID: {formatShortId(history.tourId)}</span>
                        </div>
                      </div>
                    </td>

                    {/* TOTAL CONFIG */}
                    <td>
                      <span className="activity-cruise-tour-history-page__configuration-count">
                        {history.totalConfigurations}
                      </span>
                    </td>

                    {/* COMPLETED AT */}
                    <td>
                      <div className="activity-cruise-tour-history-page__date">
                        <CalendarDays size={15} />

                        <span>{formatDateTime(history.completedAt)}</span>
                      </div>
                    </td>

                    {/* STATUS */}
                    <td>
                      <span className="activity-cruise-tour-history-page__status">
                        Đã hoàn thành
                      </span>
                    </td>

                    {/* DETAIL */}
                    <td>
                      <button
                        type="button"
                        className="activity-cruise-tour-history-page__detail-button"
                        onClick={() => handleViewDetail(history)}
                      >
                        <Eye size={16} />
                        <span>Xem chi tiết</span>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {/* =====================================================
          DETAIL MODAL
          ===================================================== */}

      {selectedHistory && (
        <ActivityCruiseTourHistoryDetail
          history={selectedHistory}
          activities={configurationDetail}
          loading={detailLoading}
          error={detailError}
          onClose={handleCloseDetail}
        />
      )}
    </div>
  );
};

export default ActivityCruiseTourHistory;
