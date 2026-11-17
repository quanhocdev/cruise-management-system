// src/modules/passenger/pages/PassengerTourTracking.jsx
import React from "react";
import { useNavigate } from "react-router-dom";
import { Compass, RefreshCw } from "lucide-react";
import usePassengerTours from "../hooks/usePassengerTours"; // Hook của passenger
import TourCard from "../../scheduler/components/vessel/TourCard"; // Dùng lại TourCard chuẩn
import "../../scheduler/styles/vessel-shared.css";
import "../../scheduler/styles/VesselMonitoringPage.css";

export default function PassengerTourTracking() {
  const navigate = useNavigate();
  const { tours, loading, error, loadActiveTours } = usePassengerTours();

  return (
    <div className="vm-page">
      <header className="vm-hero vmp-hero">
        <div className="vmp-hero-text">
          <h1>Theo dõi hành trình tour của tôi</h1>
          <p>
            Xem vị trí trực tiếp, lộ trình chi tiết và thời tiết tuyến du
            thuyền.
          </p>
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
            Bạn chưa có tour nào đang diễn ra hoặc sẵn sàng khởi hành.
          </div>
        ) : (
          <div className="vmp-grid">
            {tours.map((tour) => (
              <TourCard
                key={tour.id}
                tour={tour}
                onOpen={(id) => navigate(`/passenger/tracking/${id}`)}
                actionLabel="Xem hành trình tàu"
              />
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
