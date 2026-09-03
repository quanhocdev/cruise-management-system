// frontend-web/src/modules/shore/pages/ActivityVisitTour.jsx

import { useCallback, useEffect, useMemo, useState } from "react";
import {
  RefreshCw,
  CheckCircle2,
  Clock3,
  AlertCircle,
  MapPin,
  History,
} from "lucide-react";
import { useNavigate } from "react-router-dom";

import "../styles/ActivityVisitTour.css";

import visitTourService from "../services/visitTourService";
import ShoreTourTable from "../components/ShoreTourTable";

const STATUS_OPTIONS = [
  { value: "ALL", label: "Tất cả" },
  { value: "WAITING_CONFIG", label: "Chờ cấu hình" },
  { value: "CONFIGURED", label: "Đã cấu hình" },
  { value: "NOT_STARTED", label: "Chưa bắt đầu" },
  { value: "IN_PROGRESS", label: "Đang diễn ra" },
  { value: "COMPLETED", label: "Đã hoàn thành" },
  { value: "DELAYED", label: "Trì hoãn" },
  { value: "CANCELLED", label: "Đã hủy" },
];

function ShoreManagerTour() {
  const navigate = useNavigate();

  const [visitTours, setVisitTours] = useState([]);
  const [loading, setLoading] = useState(false);

  const [completeLoading, setCompleteLoading] = useState(null);

  const [error, setError] = useState(null);
  const [completeError, setCompleteError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  const [statusFilter, setStatusFilter] = useState("ALL");

  // =====================================================
  // LOAD
  // =====================================================

  const loadVisitTours = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await visitTourService.getAll();

      setVisitTours(data || []);
    } catch (err) {
      console.error("🔥 LOAD VISIT TOURS ERROR:", err);

      setError(
        err.response?.data?.message || "Không thể tải danh sách Visit Tour.",
      );

      setVisitTours([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadVisitTours();
  }, [loadVisitTours]);

  // =====================================================
  // FILTER
  // =====================================================

  const filteredVisitTours = useMemo(() => {
    if (statusFilter === "ALL") {
      return visitTours;
    }

    return visitTours.filter((item) => item.status === statusFilter);
  }, [visitTours, statusFilter]);

  // =====================================================
  // GROUP BY TOUR
  // =====================================================

  const tourConfigurationStatus = useMemo(() => {
    const map = new Map();

    visitTours.forEach((visitTour) => {
      if (!visitTour.tourId) return;

      if (!map.has(visitTour.tourId)) {
        map.set(visitTour.tourId, []);
      }

      map.get(visitTour.tourId).push(visitTour);
    });

    return map;
  }, [visitTours]);

  // =====================================================
  // CHECK TOUR CAN COMPLETE
  // =====================================================

  const canCompleteTour = useCallback(
    (tourId) => {
      const tours = tourConfigurationStatus.get(tourId);

      if (!tours || tours.length === 0) {
        return false;
      }

      return tours.every((visitTour) => visitTour.status === "CONFIGURED");
    },
    [tourConfigurationStatus],
  );

  // =====================================================
  // COMPLETE CONFIGURATION
  // =====================================================

  const handleCompleteConfiguration = async (tourId) => {
    if (!tourId) return;

    if (!canCompleteTour(tourId)) {
      setCompleteError(
        "Tất cả Visit Tour của Tour phải ở trạng thái Đã cấu hình trước khi hoàn thành.",
      );

      return;
    }

    const confirmed = window.confirm(
      "Bạn có chắc muốn hoàn thành cấu hình Visit Tour cho Tour này?\n\nSau khi hoàn thành, hệ thống sẽ lưu lịch sử cấu hình và gửi thông báo sang các service liên quan.",
    );

    if (!confirmed) return;

    setCompleteLoading(tourId);
    setCompleteError(null);
    setSuccessMessage(null);

    try {
      await visitTourService.completeTourConfiguration(tourId);

      setSuccessMessage("Đã hoàn thành cấu hình Visit Tour thành công.");

      await loadVisitTours();
    } catch (err) {
      console.error("🔥 COMPLETE VISIT TOUR CONFIGURATION ERROR:", err);

      setCompleteError(
        err.response?.data?.message ||
          "Không thể hoàn thành cấu hình Visit Tour.",
      );
    } finally {
      setCompleteLoading(null);
    }
  };

  // =====================================================
  // CONFIGURATION
  // =====================================================

  const handleConfiguration = (tourId, scheduleStopId) => {
    navigate(
      `/shore/visit-tour-configuration?tourId=${tourId}&scheduleStopId=${scheduleStopId}`,
    );
  };

  // =====================================================
  // HISTORY
  // =====================================================

  const handleOpenHistory = () => {
    navigate("/shore/activity-visit-history");
  };

  // =====================================================
  // SUMMARY
  // =====================================================

  const summary = useMemo(() => {
    return {
      total: visitTours.length,

      waiting: visitTours.filter((item) => item.status === "WAITING_CONFIG")
        .length,

      configured: visitTours.filter((item) => item.status === "CONFIGURED")
        .length,

      inProgress: visitTours.filter((item) => item.status === "IN_PROGRESS")
        .length,

      completed: visitTours.filter((item) => item.status === "COMPLETED")
        .length,
    };
  }, [visitTours]);

  // =====================================================
  // LOADING
  // =====================================================

  if (loading) {
    return (
      <div className="shore-manager-tour">
        <div className="shore-manager-tour-loading">
          <RefreshCw size={22} className="shore-manager-tour-spinner" />

          <span>Đang tải danh sách Visit Tour...</span>
        </div>
      </div>
    );
  }

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="shore-manager-tour">
      {/* =================================================
          HEADER
          ================================================= */}

      <div className="shore-manager-tour-header">
        <div>
          <h1>Quản lý Tour bờ</h1>

          <p>Danh sách Visit Tour và cấu hình hoạt động tham quan trên bờ.</p>
        </div>

        <div className="shore-manager-tour-header-actions">
          <button
            type="button"
            className="shore-manager-tour-history"
            onClick={handleOpenHistory}
          >
            <History size={17} />
            <span>Lịch sử cấu hình</span>
          </button>

          <button
            type="button"
            className="shore-manager-tour-refresh"
            onClick={loadVisitTours}
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
        <div className="shore-manager-tour-error">
          <span>{error}</span>

          <button type="button" onClick={loadVisitTours}>
            Thử lại
          </button>
        </div>
      )}

      {/* =================================================
          COMPLETE ERROR
          ================================================= */}

      {completeError && (
        <div className="shore-manager-tour-complete-error">
          <AlertCircle size={17} />

          <span>{completeError}</span>

          <button type="button" onClick={() => setCompleteError(null)}>
            Đóng
          </button>
        </div>
      )}

      {/* =================================================
          SUCCESS
          ================================================= */}

      {successMessage && (
        <div className="shore-manager-tour-success">
          <CheckCircle2 size={17} />

          <span>{successMessage}</span>

          <button type="button" onClick={() => setSuccessMessage(null)}>
            Đóng
          </button>
        </div>
      )}

      {!error && (
        <>
          {/* =================================================
              SUMMARY
              ================================================= */}

          <div className="shore-manager-tour-summary">
            <div className="shore-manager-tour-summary-item">
              <span>Tổng Visit Tour</span>
              <strong>{summary.total}</strong>
            </div>

            <div className="shore-manager-tour-summary-item waiting">
              <span>Chờ cấu hình</span>
              <strong>{summary.waiting}</strong>
            </div>

            <div className="shore-manager-tour-summary-item configured">
              <span>Đã cấu hình</span>
              <strong>{summary.configured}</strong>
            </div>

            <div className="shore-manager-tour-summary-item progress">
              <span>Đang diễn ra</span>
              <strong>{summary.inProgress}</strong>
            </div>

            <div className="shore-manager-tour-summary-item completed">
              <span>Đã hoàn thành</span>
              <strong>{summary.completed}</strong>
            </div>
          </div>

          {/* =================================================
              FILTER
              ================================================= */}

          <div className="shore-manager-tour-toolbar">
            <div className="shore-manager-tour-filter">
              <div className="shore-manager-tour-filter-label">
                <Clock3 size={16} />

                <span>Trạng thái</span>
              </div>

              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
              >
                {STATUS_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="shore-manager-tour-result-count">
              Hiển thị <strong>{filteredVisitTours.length}</strong> /{" "}
              {visitTours.length} Visit Tour
            </div>
          </div>

          {/* =================================================
              TOUR STATUS / COMPLETE
              ================================================= */}

          <div className="shore-manager-tour-completion-list">
            {[...tourConfigurationStatus.entries()].map(([tourId, tours]) => {
              const canComplete = canCompleteTour(tourId);

              return (
                <div
                  key={tourId}
                  className={`shore-manager-tour-completion-card ${
                    canComplete ? "ready" : ""
                  }`}
                >
                  <div className="shore-manager-tour-completion-info">
                    <div className="shore-manager-tour-completion-icon">
                      <MapPin size={18} />
                    </div>

                    <div>
                      <span>Tour</span>

                      <strong>{tourId}</strong>

                      <small>
                        {tours.length} Visit Tour
                        {tours.length > 1 ? "s" : ""}
                      </small>
                    </div>
                  </div>

                  <div className="shore-manager-tour-completion-status">
                    {canComplete ? (
                      <>
                        <CheckCircle2 size={17} />

                        <span>Đã cấu hình đầy đủ</span>
                      </>
                    ) : (
                      <>
                        <Clock3 size={17} />

                        <span>Chưa đủ điều kiện hoàn thành</span>
                      </>
                    )}
                  </div>

                  <button
                    type="button"
                    className="shore-manager-tour-complete-button"
                    disabled={!canComplete || completeLoading === tourId}
                    onClick={() => handleCompleteConfiguration(tourId)}
                  >
                    {completeLoading === tourId ? (
                      <>
                        <RefreshCw
                          size={16}
                          className="shore-manager-tour-spinner"
                        />

                        <span>Đang xử lý...</span>
                      </>
                    ) : (
                      <>
                        <CheckCircle2 size={16} />

                        <span>Hoàn thành</span>
                      </>
                    )}
                  </button>
                </div>
              );
            })}
          </div>

          {/* =================================================
              TABLE
              ================================================= */}

          <ShoreTourTable
            visitTours={filteredVisitTours}
            onConfigure={handleConfiguration}
          />
        </>
      )}
    </div>
  );
}

export default ShoreManagerTour;
