// src/modules/convenience/tour-config/ServiceTourDetailModal.jsx
import React, { useMemo } from "react";
import { Layers, MapPin, Ship, X } from "lucide-react";

import useServiceTour from "../../hooks/useServiceTour";
import "../../styles/history/ServiceTourDetailModal.css";

const ServiceTourDetailModal = ({ assignmentId, onClose }) => {
  const { serviceTours } = useServiceTour();

  const assignment = useMemo(
    () => (serviceTours || []).find((item) => item.id === assignmentId),
    [serviceTours, assignmentId],
  );

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

  const handleOverlayClick = () => {
    onClose();
  };

  const stopPropagation = (event) => {
    event.stopPropagation();
  };

  // =====================================================
  // NOT FOUND
  // =====================================================

  if (!assignment) {
    return (
      <div className="service-tour-detail-overlay" onClick={handleOverlayClick}>
        <div className="service-tour-detail-modal" onClick={stopPropagation}>
          <div className="service-tour-detail-header">
            <h3>
              <Layers size={18} />
              Chi tiết dịch vụ
            </h3>
            <button
              type="button"
              className="service-tour-detail-close"
              onClick={onClose}
              title="Đóng"
            >
              <X size={18} />
            </button>
          </div>

          <div className="service-tour-detail-empty">
            <p>Không tìm thấy thông tin phân công này.</p>
          </div>
        </div>
      </div>
    );
  }

  // =====================================================
  // MAIN
  // =====================================================

  return (
    <div className="service-tour-detail-overlay" onClick={handleOverlayClick}>
      <div className="service-tour-detail-modal" onClick={stopPropagation}>
        {/* HEADER */}
        <div className="service-tour-detail-header">
          <h3>
            <Layers size={18} />
            Chi tiết dịch vụ Tour
          </h3>
          <button
            type="button"
            className="service-tour-detail-close"
            onClick={onClose}
            title="Đóng"
          >
            <X size={18} />
          </button>
        </div>

        {/* BODY */}
        <div className="service-tour-detail-body">
          {/* TOUR */}
          <section className="service-tour-detail-section">
            <h4>
              <Ship size={15} />
              Thông tin Tour
            </h4>

            <div className="service-tour-detail-row">
              <span className="service-tour-detail-label">Mã Tour</span>
              <span className="service-tour-detail-value font-mono">
                {assignment.tourCode || assignment.tourId || "—"}
              </span>
            </div>

            <div className="service-tour-detail-row">
              <span className="service-tour-detail-label">Tên Tour</span>
              <span className="service-tour-detail-value">
                {assignment.tourName || "—"}
              </span>
            </div>
          </section>

          {/* AREA */}
          <section className="service-tour-detail-section">
            <h4>
              <MapPin size={15} />
              Khu vực
            </h4>

            <div className="service-tour-detail-row">
              <span className="service-tour-detail-label">Khu vực</span>
              <span className="service-tour-detail-value font-mono">
                {assignment.cruiseAreaName || assignment.cruiseAreaId || "—"}
              </span>
            </div>

            {assignment.deckNumber != null && (
              <div className="service-tour-detail-row">
                <span className="service-tour-detail-label">Tầng</span>
                <span className="service-tour-detail-value">
                  Tầng {assignment.deckNumber}
                </span>
              </div>
            )}
          </section>

          {/* SERVICE */}
          <section className="service-tour-detail-section">
            <h4>
              <Layers size={15} />
              Dịch vụ
            </h4>

            <div className="service-tour-detail-row">
              <span className="service-tour-detail-label">Tên dịch vụ</span>
              <span className="service-tour-detail-value">
                {assignment.serviceName || "Chưa chọn dịch vụ"}
              </span>
            </div>

            {assignment.serviceDescription && (
              <div className="service-tour-detail-row">
                <span className="service-tour-detail-label">Mô tả</span>
                <span className="service-tour-detail-value">
                  {assignment.serviceDescription}
                </span>
              </div>
            )}

            <div className="service-tour-detail-row">
              <span className="service-tour-detail-label">Khách tối đa</span>
              <span className="service-tour-detail-value">
                {assignment.maxPassengers != null
                  ? assignment.maxPassengers
                  : "—"}
              </span>
            </div>

            <div className="service-tour-detail-row">
              <span className="service-tour-detail-label">Thời lượng</span>
              <span className="service-tour-detail-value">
                {assignment.durationMinutes != null
                  ? `${assignment.durationMinutes} phút`
                  : "Không giới hạn"}
              </span>
            </div>

            <div className="service-tour-detail-row">
              <span className="service-tour-detail-label">Trạng thái</span>
              <span
                className={`service-tour-detail-status ${String(
                  assignment.status || "",
                ).toLowerCase()}`}
              >
                {getStatusLabel(assignment.status)}
              </span>
            </div>
          </section>
        </div>

        {/* FOOTER */}
        <div className="service-tour-detail-footer">
          <button
            type="button"
            className="service-tour-detail-close-btn"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};

export default ServiceTourDetailModal;
