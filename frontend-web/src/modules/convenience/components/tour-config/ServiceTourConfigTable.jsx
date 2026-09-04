// src/modules/convenience/tour-config/ServiceTourConfigTable.jsx

import React, { useMemo, useState } from "react";
import {
  AlertCircle,
  CheckCircle2,
  Edit3,
  Eye,
  Plus,
  RefreshCw,
  Wrench,
} from "lucide-react";

import useServiceTour from "../../hooks/useServiceTour";
import "../../styles/tour-config/ServiceTourConfigTable.css";
import ServiceTourConfigModal from "./ServiceTourConfigModal";
import ServiceTourDetailModal from "../history/ServiceTourDetailModal"; // Modal chi tiết khi click con mắt

// =========================================================
// STATUS TABS — khớp đúng ServiceTourStatus (Java enum)
// (không có OUT_OF_STOCK như Product)
// =========================================================

const STATUS_TABS = [
  { value: "ALL", label: "Tất cả" },
  { value: "WAITING_CONFIG", label: "Chờ cấu hình" },
  { value: "CONFIGURED", label: "Đã cấu hình" },
  { value: "NOT_STARTED", label: "Chưa bắt đầu" },
  { value: "IN_PROGRESS", label: "Đang phục vụ" },
  { value: "COMPLETED", label: "Đã kết thúc" },
];

const formatShortId = (str, maxLength = 8) => {
  if (!str) return "—";
  if (str.length <= maxLength) return str;
  return `${str.substring(0, maxLength)}...`;
};

const ServiceTourConfigTable = () => {
  const {
    serviceTours,
    tourSummaries,
    loading,
    error,
    completing,
    completeError,
    loadServiceTours,
    configureService,
    updateService,
    completeTourConfiguration,
  } = useServiceTour();

  const [statusFilter, setStatusFilter] = useState("ALL");
  const [selectedAssignment, setSelectedAssignment] = useState(null);
  const [viewDetailAssignment, setViewDetailAssignment] = useState(null); // State xem chi tiết

  // Tour đang được chọn để "Hoàn thành cấu hình"
  const [selectedTourIdToComplete, setSelectedTourIdToComplete] = useState("");
  const [completeSuccess, setCompleteSuccess] = useState(false);

  // =====================================================
  // FILTER THEO TAB TRẠNG THÁI (không chỉ WAITING_CONFIG nữa)
  // =====================================================

  const filteredTours = useMemo(() => {
    if (statusFilter === "ALL") return serviceTours || [];
    return (serviceTours || []).filter((item) => item.status === statusFilter);
  }, [serviceTours, statusFilter]);

  // =====================================================
  // OPEN MODAL
  // =====================================================

  const handleOpenConfig = (assignment) => {
    setSelectedAssignment(assignment);
  };

  // =====================================================
  // CLOSE MODAL
  // =====================================================

  const handleCloseModal = () => {
    if (loading) {
      return;
    }

    setSelectedAssignment(null);
  };

  // =====================================================
  // SUBMIT CONFIG
  // =====================================================
  //
  // ✅ Backend chỉ cho:
  //   - configure()   khi status === WAITING_CONFIG
  //   - updateConfig() khi status === CONFIGURED (không phải NOT_STARTED)
  //
  // =====================================================

  const handleSubmit = async (assignmentId, payload) => {
    const assignment = serviceTours.find((item) => item.id === assignmentId);

    if (!assignment) {
      return;
    }

    if (assignment.status === "WAITING_CONFIG") {
      await configureService(assignmentId, payload);
    } else if (assignment.status === "CONFIGURED") {
      await updateService(assignmentId, payload);
    }

    setSelectedAssignment(null);
  };

  // =====================================================
  // STATUS LABEL — khớp đúng ý nghĩa thật của từng status
  // =====================================================

  const getStatusLabel = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "Chờ cấu hình";

      case "CONFIGURED":
        return "Đã cấu hình";

      case "NOT_STARTED":
        return "Chưa bắt đầu";

      case "IN_PROGRESS":
        return "Đang phục vụ";

      case "COMPLETED":
        return "Đã kết thúc";

      default:
        return status || "Không xác định";
    }
  };

  // =====================================================
  // HOÀN THÀNH CẤU HÌNH TOUR
  // =====================================================

  const selectedTourSummary = useMemo(
    () =>
      tourSummaries.find((tour) => tour.tourId === selectedTourIdToComplete),
    [tourSummaries, selectedTourIdToComplete],
  );

  const canComplete =
    !!selectedTourSummary &&
    !selectedTourSummary.completed &&
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
      console.error("COMPLETE SERVICE TOUR ERROR:", err);
      // completeError đã được hook set, hiển thị bên dưới
    }
  };

  // =====================================================
  // LOADING
  // =====================================================

  if (loading && filteredTours.length === 0) {
    return (
      <div className="service-tour-config-loading">
        <RefreshCw size={20} className="spin" />

        <span>Đang tải danh sách dịch vụ...</span>
      </div>
    );
  }

  return (
    <>
      {/* =====================================================
          HEADER
          ===================================================== */}

      <div className="service-tour-config-toolbar">
        <div>
          <h2>
            <Wrench size={20} />
            Dịch vụ của Tour
          </h2>

          <p>Cấu hình các dịch vụ được Operation phân công cho từng Tour.</p>
        </div>

        <button
          type="button"
          className="service-tour-config-refresh"
          onClick={loadServiceTours}
          disabled={loading}
        >
          <RefreshCw size={16} className={loading ? "spin" : ""} />

          <span>Làm mới</span>
        </button>
      </div>

      {/* =====================================================
          ERROR
          ===================================================== */}

      {error && (
        <div className="service-tour-config-error">
          <AlertCircle size={18} />

          <span>{error}</span>
        </div>
      )}

      {/* ============ HOÀN THÀNH CẤU HÌNH TOUR ============ */}
      <div className="service-tour-config-complete-box">
        <div className="service-tour-config-complete-info">
          <strong>Hoàn thành cấu hình Tour</strong>
          <p>
            Chọn Tour và bấm Hoàn thành khi tất cả dịch vụ của Tour đó đã được
            cấu hình xong. Hệ thống sẽ gửi thông tin sang Tour service.
          </p>
        </div>

        <div className="service-tour-config-complete-controls">
          <select
            className="service-tour-config-complete-select"
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
                {formatShortId(tour.tourId, 8)} ({tour.configuredCount}/
                {tour.total} đã cấu hình)
                {tour.completed ? " — Đã hoàn thành" : ""}
              </option>
            ))}
          </select>

          <button
            type="button"
            className="service-tour-config-complete-button"
            onClick={handleCompleteTour}
            disabled={!canComplete || completing}
          >
            <CheckCircle2 size={16} />
            {completing ? "Đang xử lý..." : "Hoàn thành cấu hình"}
          </button>
        </div>

        {selectedTourIdToComplete && selectedTourSummary?.completed && (
          <span className="service-tour-config-complete-hint">
            Tour này đã được hoàn thành cấu hình trước đó.
          </span>
        )}

        {selectedTourIdToComplete &&
          !selectedTourSummary?.completed &&
          !canComplete && (
            <span className="service-tour-config-complete-hint">
              Tour này còn dịch vụ chưa được cấu hình xong.
            </span>
          )}

        {completeError && (
          <span className="service-tour-config-complete-error">
            {completeError}
          </span>
        )}

        {completeSuccess && (
          <span className="service-tour-config-complete-success">
            Đã hoàn thành cấu hình Tour thành công.
          </span>
        )}
      </div>

      {/* =====================================================
          STATUS TABS
          ===================================================== */}

      <div className="service-tour-config-filters">
        {STATUS_TABS.map((tab) => (
          <button
            key={tab.value}
            type="button"
            className={`service-tour-config-filter-btn ${
              statusFilter === tab.value
                ? "service-tour-config-filter-btn--active"
                : ""
            }`}
            onClick={() => setStatusFilter(tab.value)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* =====================================================
          EMPTY
          ===================================================== */}

      {filteredTours.length === 0 && !error ? (
        <div className="service-tour-config-empty">
          <Wrench size={32} />

          <h3>Không có dịch vụ nào</h3>

          <p>Không có dịch vụ nào khớp với bộ lọc hiện tại.</p>
        </div>
      ) : (
        /* =====================================================
           TABLE
           ===================================================== */

        <div className="service-tour-config-table-wrapper">
          <table className="service-tour-config-table">
            <thead>
              <tr>
                <th>STT</th>
                <th>Tour</th>
                <th>Khu vực</th>
                <th>Dịch vụ</th>
                <th>Khách tối đa</th>
                <th>Thời lượng</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>

            <tbody>
              {filteredTours.map((assignment, index) => {
                const tourIdValue = assignment.tourId || assignment.id;
                const fullTourCode =
                  assignment.tourCode ||
                  (tourIdValue ? `TOUR-${tourIdValue}` : "");
                const fullAreaId =
                  assignment.cruiseAreaName || assignment.cruiseAreaId || "";

                return (
                  <tr key={assignment.id}>
                    {/* =================================================
                        STT
                        ================================================= */}

                    <td>{index + 1}</td>

                    {/* =================================================
                        TOUR
                        ================================================= */}

                    <td>
                      <div
                        className="service-tour-config-tour"
                        style={{
                          display: "flex",
                          alignItems: "center",
                          gap: "6px",
                        }}
                      >
                        <strong className="font-mono" title={fullTourCode}>
                          {assignment.tourCode ||
                            (tourIdValue
                              ? `TOUR-${formatShortId(tourIdValue, 6, 4)}`
                              : "—")}
                        </strong>
                        <button
                          type="button"
                          className="service-tour-config-action service-tour-config-action--view"
                          onClick={() => setViewDetailAssignment(assignment)}
                          title="Xem chi tiết Tour"
                          style={{ padding: "2px 4px" }}
                        >
                          <Eye size={14} />
                        </button>
                        {assignment.tourName && (
                          <span>{assignment.tourName}</span>
                        )}
                      </div>
                    </td>

                    {/* =================================================
                        AREA
                        ================================================= */}

                    <td>
                      <div
                        className="service-tour-config-area"
                        style={{
                          display: "flex",
                          alignItems: "center",
                          gap: "6px",
                        }}
                      >
                        <strong className="font-mono" title={fullAreaId}>
                          {assignment.cruiseAreaName ||
                            formatShortId(assignment.cruiseAreaId, 6, 4)}
                        </strong>
                        <button
                          type="button"
                          className="service-tour-config-action service-tour-config-action--view"
                          onClick={() => setViewDetailAssignment(assignment)}
                          title="Xem chi tiết Khu vực"
                          style={{ padding: "2px 4px" }}
                        >
                          <Eye size={14} />
                        </button>
                        {assignment.deckNumber != null && (
                          <span>Tầng {assignment.deckNumber}</span>
                        )}
                      </div>
                    </td>

                    {/* =================================================
                        SERVICE
                        ================================================= */}

                    <td>
                      {assignment.serviceName ? (
                        <div className="service-tour-config-service">
                          <strong>{assignment.serviceName}</strong>

                          {assignment.serviceDescription && (
                            <span>{assignment.serviceDescription}</span>
                          )}
                        </div>
                      ) : (
                        <span className="service-tour-config-muted">
                          Chưa chọn dịch vụ
                        </span>
                      )}
                    </td>

                    {/* =================================================
                        MAX PASSENGERS
                        ================================================= */}

                    <td>
                      {assignment.maxPassengers != null ? (
                        <span>{assignment.maxPassengers}</span>
                      ) : (
                        <span className="service-tour-config-muted">—</span>
                      )}
                    </td>

                    {/* =================================================
                        DURATION
                        ================================================= */}

                    <td>
                      {assignment.durationMinutes != null ? (
                        <span>{assignment.durationMinutes} phút</span>
                      ) : (
                        <span className="service-tour-config-unlimited">
                          Không giới hạn
                        </span>
                      )}
                    </td>

                    {/* =================================================
                        STATUS
                        ================================================= */}

                    <td>
                      <span
                        className={`service-tour-config-status ${String(
                          assignment.status || "",
                        ).toLowerCase()}`}
                      >
                        {getStatusLabel(assignment.status)}
                      </span>
                    </td>

                    {/* =================================================
                        ACTION
                        ================================================= */}

                    <td>
                      {/* ✅ Cấu hình lần đầu: chỉ khi WAITING_CONFIG */}
                      {assignment.status === "WAITING_CONFIG" && (
                        <button
                          type="button"
                          className="service-tour-config-action"
                          onClick={() => handleOpenConfig(assignment)}
                        >
                          <Plus size={16} />

                          <span>Cấu hình</span>
                        </button>
                      )}

                      {/* ✅ Chỉnh sửa: chỉ khi CONFIGURED (khớp backend updateConfig) */}
                      {assignment.status === "CONFIGURED" && (
                        <button
                          type="button"
                          className="service-tour-config-action"
                          onClick={() => handleOpenConfig(assignment)}
                        >
                          <Edit3 size={16} />

                          <span>Chỉnh sửa</span>
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {/* =====================================================
          CONFIG MODAL
          ===================================================== */}

      {selectedAssignment && (
        <ServiceTourConfigModal
          assignment={selectedAssignment}
          onClose={handleCloseModal}
          onSubmit={handleSubmit}
          submitting={loading}
        />
      )}

      {/* =====================================================
          MODAL XEM CHI TIẾT (EYE ICON)
          ===================================================== */}

      {viewDetailAssignment && (
        <ServiceTourDetailModal
          assignmentId={viewDetailAssignment.id}
          onClose={() => setViewDetailAssignment(null)}
        />
      )}
    </>
  );
};

export default ServiceTourConfigTable;
