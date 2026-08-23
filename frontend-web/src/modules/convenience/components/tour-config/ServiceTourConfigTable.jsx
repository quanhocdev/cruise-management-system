// src/modules/convenience/tour-config/ServiceTourConfigTable.jsx

import React, { useMemo, useState } from "react";
import { AlertCircle, Edit3, Plus, RefreshCw, Wrench } from "lucide-react";

import useServiceTour from "../../hooks/useServiceTour";
import "../../styles/tour-config/ServiceTourConfigTable.css";
import ServiceTourConfigModal from "./ServiceTourConfigModal";

const ServiceTourConfigTable = () => {
  const {
    serviceTours,
    loading,
    error,
    loadServiceTours,
    configureService,
    updateService,
  } = useServiceTour();

  const [selectedAssignment, setSelectedAssignment] = useState(null);

  // =====================================================
  // CONFIGURABLE DATA
  // =====================================================

  const configurableTours = useMemo(() => {
    return (serviceTours || []).filter(
      (item) =>
        item.status === "WAITING_CONFIG" || item.status === "NOT_STARTED",
    );
  }, [serviceTours]);

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

  const handleSubmit = async (assignmentId, payload) => {
    const assignment = serviceTours.find((item) => item.id === assignmentId);

    if (!assignment) {
      return;
    }

    if (assignment.status === "WAITING_CONFIG") {
      await configureService(assignmentId, payload);
    } else if (assignment.status === "NOT_STARTED") {
      await updateService(assignmentId, payload);
    }

    setSelectedAssignment(null);
  };

  // =====================================================
  // STATUS LABEL
  // =====================================================

  const getStatusLabel = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "Chờ cấu hình";

      case "NOT_STARTED":
        return "Đã cấu hình";

      case "IN_PROGRESS":
        return "Đang phục vụ";

      case "COMPLETED":
        return "Đã kết thúc";

      default:
        return status || "Không xác định";
    }
  };

  // =====================================================
  // LOADING
  // =====================================================

  if (loading && configurableTours.length === 0) {
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

      {/* =====================================================
          EMPTY
          ===================================================== */}

      {configurableTours.length === 0 && !error ? (
        <div className="service-tour-config-empty">
          <Wrench size={32} />

          <h3>Chưa có dịch vụ cần cấu hình</h3>

          <p>Hiện tại không có dịch vụ nào đang chờ hoặc đã cấu hình.</p>
        </div>
      ) : (
        /* =====================================================
           TABLE
           ===================================================== */

        <div className="service-tour-config-table-wrapper">
          <table className="service-tour-config-table">
            <thead>
              <tr>
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
              {configurableTours.map((assignment) => (
                <tr key={assignment.id}>
                  {/* =================================================
                      TOUR
                      ================================================= */}

                  <td>
                    <div className="service-tour-config-tour">
                      <strong>{assignment.tourCode || "—"}</strong>

                      <span>{assignment.tourName || "—"}</span>
                    </div>
                  </td>

                  {/* =================================================
                      AREA
                      ================================================= */}

                  <td>
                    <div className="service-tour-config-area">
                      <strong>{assignment.cruiseAreaName || "—"}</strong>

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

                    {assignment.status === "NOT_STARTED" && (
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
              ))}
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
    </>
  );
};

export default ServiceTourConfigTable;
