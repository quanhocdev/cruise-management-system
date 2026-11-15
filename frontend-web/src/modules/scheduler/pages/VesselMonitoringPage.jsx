// src/modules/scheduler/pages/VesselMonitoringPage.jsx
import React from "react";
import { useNavigate } from "react-router-dom";
import { RefreshCw, Compass } from "lucide-react";
import useVesselTours from "../hooks/useVesselTours";
import TourCard from "../components/vessel/TourCard";
import "../styles/vessel-shared.css";
import "../styles/VesselMonitoringPage.css";

export default function VesselMonitoringPage() {
  const navigate = useNavigate();
  const { tours, loading, error, loadActiveTours } = useVesselTours();

  const sailing = tours.filter((t) => t.statusTrip === "IN_PROGRESS").length;
  const ready = tours.filter((t) => t.statusTrip === "READY").length;

  return (
    <div className="vm-page">
      <header className="vm-hero vmp-hero">
        <div className="vmp-hero-text">
          <h1>Giám sát đội tàu & Tuyến đường</h1>
          <p>
            Theo dõi vị trí, hành trình và thời tiết của các tour đang hoạt
            động.
          </p>
          <div className="vmp-stats">
            <span>
              <strong>{sailing}</strong> đang hành trình
            </span>
            <span>
              <strong>{ready}</strong> sẵn sàng khởi hành
            </span>
          </div>
        </div>

        <div className="vmp-hero-actions">
          <Compass size={64} className="vmp-compass" aria-hidden="true" />
          <button
            className="vmp-refresh"
            onClick={() => loadActiveTours()}
            disabled={loading}
          >
            <RefreshCw size={16} className={loading ? "vm-spin" : ""} />
            <span>Làm mới</span>
          </button>
        </div>
      </header>

      <main className="vmp-body">
        {error && <div className="vmp-error">{error}</div>}

        {loading && tours.length === 0 ? (
          <div className="vmp-state">Đang tải danh sách tour...</div>
        ) : tours.length === 0 ? (
          <div className="vmp-state">
            Chưa có tour nào ở trạng thái Sẵn sàng hoặc Đang hành trình.
          </div>
        ) : (
          <div className="vmp-grid">
            {tours.map((tour) => (
              <TourCard
                key={tour.id}
                tour={tour}
                onOpen={(id) => navigate(`/scheduler/vessels/${id}`)}
              />
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
