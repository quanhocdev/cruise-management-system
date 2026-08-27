// frontend-web/src/modules/shore/pages/ActivityVisitTourHistory.jsx

import { useEffect, useState } from "react";
import { RefreshCw, History, ChevronRight, AlertCircle } from "lucide-react";
import { useNavigate } from "react-router-dom";

import "../styles/ActivityVisitTourHistory.css";

import useVisitTourHistory from "../hooks/useVisitTourHistory";
import ActivityVisitTourHistoryDetail from "../components/ActivityVisitTourHistoryDetail";

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

function ActivityVisitTourHistory() {
  const navigate = useNavigate();

  const {
    history,
    loading,
    detailLoading,
    error,
    detailError,
    loadHistory,
    loadConfigurationDetail,
    clearConfigurationDetail,
    configurationDetail,
  } = useVisitTourHistory();

  const [selectedHistory, setSelectedHistory] = useState(null);

  useEffect(() => {
    loadHistory();
  }, [loadHistory]);

  const handleOpenDetail = async (item) => {
    setSelectedHistory(item);

    await loadConfigurationDetail(item.tourId);
  };

  const handleCloseDetail = () => {
    setSelectedHistory(null);
    clearConfigurationDetail();
  };

  const handleBack = () => {
    navigate("/shore/activity-visit");
  };

  if (loading) {
    return (
      <div className="activity-visit-history">
        <div className="activity-visit-history__loading">
          <RefreshCw size={23} className="activity-visit-history__spinner" />

          <span>Đang tải lịch sử cấu hình...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="activity-visit-history">
      {/* =================================================
          HEADER
          ================================================= */}

      <div className="activity-visit-history__header">
        <div className="activity-visit-history__heading">
          <div className="activity-visit-history__heading-icon">
            <History size={21} />
          </div>

          <div>
            <h1>Lịch sử cấu hình Visit Tour</h1>

            <p>Các Tour đã hoàn thành cấu hình hoạt động tham quan trên bờ.</p>
          </div>
        </div>

        <div className="activity-visit-history__actions">
          <button
            type="button"
            className="activity-visit-history__back"
            onClick={handleBack}
          >
            Quản lý Visit Tour
          </button>

          <button
            type="button"
            className="activity-visit-history__refresh"
            onClick={loadHistory}
            disabled={loading}
          >
            <RefreshCw size={17} />

            <span>Làm mới</span>
          </button>
        </div>
      </div>

      {/* =================================================
          ERROR
          ================================================= */}

      {error && (
        <div className="activity-visit-history__error">
          <AlertCircle size={17} />

          <span>{error}</span>

          <button type="button" onClick={loadHistory}>
            Thử lại
          </button>
        </div>
      )}

      {/* =================================================
          SUMMARY
          ================================================= */}

      {!error && (
        <div className="activity-visit-history__summary">
          <span>Tổng số Tour đã cấu hình</span>

          <strong>{history.length}</strong>
        </div>
      )}

      {/* =================================================
          EMPTY
          ================================================= */}

      {!error && history.length === 0 && (
        <div className="activity-visit-history__empty">
          <div className="activity-visit-history__empty-icon">
            <History size={26} />
          </div>

          <h2>Chưa có lịch sử cấu hình</h2>

          <p>
            Những Tour sau khi hoàn thành cấu hình Visit Tour sẽ xuất hiện tại
            đây.
          </p>
        </div>
      )}

      {/* =================================================
          HISTORY LIST
          ================================================= */}

      {!error && history.length > 0 && (
        <div className="activity-visit-history__list">
          {history.map((item) => (
            <button
              type="button"
              key={item.id || item.tourId}
              className="activity-visit-history__item"
              onClick={() => handleOpenDetail(item)}
            >
              <div className="activity-visit-history__item-icon">
                <History size={19} />
              </div>

              <div className="activity-visit-history__item-main">
                <div className="activity-visit-history__item-title">
                  <span>Tour</span>

                  <strong>{item.tourId}</strong>
                </div>

                <div className="activity-visit-history__item-meta">
                  <span>{item.totalConfigurations ?? 0} cấu hình</span>

                  <span className="activity-visit-history__separator">•</span>

                  <span>Hoàn thành {formatDateTime(item.completedAt)}</span>
                </div>
              </div>

              <div className="activity-visit-history__item-arrow">
                <ChevronRight size={20} />
              </div>
            </button>
          ))}
        </div>
      )}

      {/* =================================================
          DETAIL
          ================================================= */}

      {selectedHistory && (
        <ActivityVisitTourHistoryDetail
          history={selectedHistory}
          activities={configurationDetail}
          loading={detailLoading}
          error={detailError}
          onClose={handleCloseDetail}
        />
      )}
    </div>
  );
}

export default ActivityVisitTourHistory;
