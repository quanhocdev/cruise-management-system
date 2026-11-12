// src/modules/scheduler/pages/VesselMonitoringPage.jsx
import React, { useState } from "react";
import {
  Ship,
  Navigation,
  RefreshCw,
  Calendar,
  ArrowRight,
} from "lucide-react";
import useVesselTours from "../hooks/useVesselTours";
import VesselDetailModal from "./VesselDetailModal";
import "../styles/VesselMonitoringPage.css";

export default function VesselMonitoringPage() {
  console.log("VESSEL MONITORING PAGE RENDERED!");
  const { tours, loading, error, loadActiveTours } = useVesselTours();
  const [selectedTourId, setSelectedTourId] = useState(null);

  return (
    <div className="vessel-monitoring-container">
      <div className="vessel-monitoring-header">
        <div>
          <h1>Giám sát đội tàu & Tuyến đường</h1>
          <p>
            Danh sách các tour đang sẵn sàng (READY) hoặc đang thực hiện
            (IN_PROGRESS).
          </p>
        </div>
        <button
          className="vessel-refresh-btn"
          onClick={() => loadActiveTours()}
          disabled={loading}
        >
          <RefreshCw size={18} className={loading ? "spin" : ""} />
          <span>Làm mới</span>
        </button>
      </div>

      {error && <div className="vessel-error-box">{error}</div>}

      {loading && tours.length === 0 ? (
        <div className="vessel-loading">Đang tải danh sách tour...</div>
      ) : tours.length === 0 ? (
        <div className="vessel-empty">
          Không có tour nào ở trạng thái READY hoặc IN_PROGRESS.
        </div>
      ) : (
        <div className="vessel-tour-grid">
          {tours.map((tour) => (
            <div key={tour.id} className="vessel-tour-card">
              <div className="card-top">
                <span className="tour-code">{tour.code}</span>
                <span
                  className={`status-badge ${tour.statusTrip.toLowerCase()}`}
                >
                  {tour.statusTrip}
                </span>
              </div>
              <h3 className="tour-name">{tour.name}</h3>
              <div className="tour-dates">
                <Calendar size={14} />
                <span>
                  {tour.startDate} ➔ {tour.endDate}
                </span>
              </div>
              <button
                className="select-tour-btn"
                onClick={() => setSelectedTourId(tour.id)}
              >
                <span>Xem giám sát chi tiết</span>
                <ArrowRight size={16} />
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Hiển thị phần chi tiết tour được chọn */}
      {selectedTourId && (
        <VesselDetailModal
          tourId={selectedTourId}
          onClose={() => setSelectedTourId(null)}
        />
      )}
    </div>
  );
}
