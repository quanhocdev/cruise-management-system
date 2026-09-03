// src/modules/shore/pages/ActivityVisitTour.jsx

import { useCallback, useEffect, useMemo, useState } from "react";
import {
  RefreshCw,
  CheckCircle2,
  Clock3,
  AlertCircle,
  MapPin,
  History,
  Calendar,
  Compass,
} from "lucide-react";
import { useNavigate } from "react-router-dom";

import "../styles/ActivityVisitTour.css";

import visitTourService from "../services/visitTourService";
import ShoreTourTable from "../components/ShoreTourTable";
import VisitTourFormModal from "../components/visit-tour/VisitTourFormModal";

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
  const [masterToursMap, setMasterToursMap] = useState(new Map()); // Lưu thông tin Master Tour theo tourId
  const [loading, setLoading] = useState(false);
  const [visitTourLoading, setVisitTourLoading] = useState(false);

  const [completeLoading, setCompleteLoading] = useState(null);

  const [error, setError] = useState(null);
  const [completeError, setCompleteError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  const [statusFilter, setStatusFilter] = useState("ALL");

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingVisitTour, setEditingVisitTour] = useState(null);
  const [selectedScheduleStop, setSelectedScheduleStop] = useState(null);

  // =====================================================
  // LOAD DATA & MASTER TOURS
  // =====================================================

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      // 1. Lấy danh sách visit tours cấu hình
      const visitData = await visitTourService.getAll();
      setVisitTours(visitData || []);

      // 2. Lấy danh sách master tours phân cấp từ Kafka sync
      const masterData = await visitTourService.getAllMasterTours();
      const map = new Map();
      if (masterData) {
        masterData.forEach((tour) => {
          map.set(tour.id, tour);
        });
      }
      setMasterToursMap(map);
    } catch (err) {
      console.error("🔥 LOAD DATA ERROR:", err);
      setError(
        err.response?.data?.message || "Không thể tải dữ liệu quản lý Tour bờ.",
      );
      setVisitTours([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

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
      await loadData();
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
    const targetTour = visitTours.find(
      (item) => item.scheduleStopId === scheduleStopId,
    );

    setSelectedScheduleStop({
      scheduleStopId: scheduleStopId,
      arriveAt: targetTour?.arriveAt || "",
      leaveAt: targetTour?.leaveAt || "",
      portName: targetTour?.portName || "",
    });

    setEditingVisitTour(targetTour?.name ? targetTour : null);
    setIsModalOpen(true);
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
          <span>Đang tải danh sách Tour bờ...</span>
        </div>
      </div>
    );
  }

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="shore-manager-tour">
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
            onClick={loadData}
            disabled={loading}
          >
            <RefreshCw size={17} />
            <span>Làm mới</span>
          </button>
        </div>
      </div>

      {error && (
        <div className="shore-manager-tour-error">
          <span>{error}</span>
          <button type="button" onClick={loadData}>
            Thử lại
          </button>
        </div>
      )}

      {completeError && (
        <div className="shore-manager-tour-complete-error">
          <AlertCircle size={17} />
          <span>{completeError}</span>
          <button type="button" onClick={() => setCompleteError(null)}>
            Đóng
          </button>
        </div>
      )}

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

          {/* HIỂN THỊ THÔNG TIN CHI TIẾT MASTER TOUR KÈM SỐ LƯỢNG NGÀY / LỊCH TRÌNH */}
          <div className="shore-manager-tour-completion-list">
            {[...tourConfigurationStatus.entries()].map(([tourId, tours]) => {
              const canComplete = canCompleteTour(tourId);
              const masterTourInfo = masterToursMap.get(tourId);

              return (
                <div
                  key={tourId}
                  className={`shore-manager-tour-completion-card ${
                    canComplete ? "ready" : ""
                  }`}
                >
                  <div className="shore-manager-tour-completion-info">
                    <div className="shore-manager-tour-completion-icon">
                      <Compass size={18} />
                    </div>

                    <div>
                      <div
                        style={{
                          display: "flex",
                          gap: "8px",
                          alignItems: "center",
                        }}
                      >
                        <strong>
                          {masterTourInfo?.name || `Tour ID: ${tourId}`}
                        </strong>
                        {masterTourInfo?.code && (
                          <span
                            style={{
                              fontSize: "0.75rem",
                              background: "#e2e8f0",
                              padding: "2px 6px",
                              borderRadius: "4px",
                            }}
                          >
                            {masterTourInfo.code}
                          </span>
                        )}
                      </div>
                      <small
                        style={{
                          display: "flex",
                          alignItems: "center",
                          gap: "6px",
                          marginTop: "2px",
                        }}
                      >
                        <Calendar size={13} />
                        {masterTourInfo?.startDate && masterTourInfo?.endDate
                          ? `${masterTourInfo.startDate} → ${masterTourInfo.endDate}`
                          : `Đồng bộ: ${tours.length} điểm dừng`}
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

          <ShoreTourTable
            visitTours={filteredVisitTours}
            onConfigure={handleConfiguration}
          />
        </>
      )}

      {isModalOpen && (
        <VisitTourFormModal
          visitTour={editingVisitTour}
          scheduleStop={selectedScheduleStop}
          loading={visitTourLoading}
          onClose={() => {
            setIsModalOpen(false);
            setEditingVisitTour(null);
            setSelectedScheduleStop(null);
          }}
          onSubmit={async (formData) => {
            setVisitTourLoading(true);
            try {
              if (editingVisitTour) {
                await visitTourService.update(editingVisitTour.id, formData);
              } else {
                await visitTourService.create(
                  selectedScheduleStop.scheduleStopId,
                  formData,
                );
              }

              setIsModalOpen(false);
              setEditingVisitTour(null);
              setSelectedScheduleStop(null);
              await loadData();
            } catch (err) {
              console.error("SAVE VISIT TOUR ERROR:", err);
              throw err;
            } finally {
              setVisitTourLoading(false);
            }
          }}
        />
      )}
    </div>
  );
}

export default ShoreManagerTour;
