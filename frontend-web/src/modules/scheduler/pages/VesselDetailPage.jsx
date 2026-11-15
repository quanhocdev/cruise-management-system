// src/modules/scheduler/pages/VesselDetailPage.jsx
import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import useVesselDetail from "../hooks/useVesselDetail";
import DetailHero from "../components/vessel/DetailHero";
import LivePositionPanel from "../components/vessel/LivePositionPanel";
import RouteProgress from "../components/vessel/RouteProgress";
import WeatherPanel from "../components/vessel/WeatherPanel";
import "../styles/vessel-shared.css";
import "../styles/VesselDetailPage.css";

export default function VesselDetailPage() {
  const { tourId } = useParams();
  const navigate = useNavigate();
  const { tourDetails, loading, error, reload } = useVesselDetail(tourId);

  // Ưu tiên vị trí tàu (Redis), không có thì lấy cảng đi
  const lat =
    tourDetails?.currentPosition?.latitude ??
    tourDetails?.departurePort?.latitude;
  const lon =
    tourDetails?.currentPosition?.longitude ??
    tourDetails?.departurePort?.longitude;

  return (
    <div className="vm-page">
      <DetailHero
        tour={tourDetails}
        loading={loading}
        onBack={() => navigate("/scheduler/vessels")}
        onReload={reload}
      />

      <main className="vdp-body">
        {loading && !tourDetails ? (
          <div className="vdp-state">Đang tải dữ liệu chi tiết...</div>
        ) : error ? (
          <div className="vdp-error">
            <span>{error}</span>
            <button onClick={reload}>Thử lại</button>
          </div>
        ) : tourDetails ? (
          <div className="vdp-grid">
            <div className="vdp-full">
              <RouteProgress tour={tourDetails} />
            </div>
            <LivePositionPanel position={tourDetails.currentPosition} />
            <WeatherPanel
              lat={lat}
              lon={lon}
              startDate={tourDetails.startDate}
              endDate={tourDetails.endDate}
            />
          </div>
        ) : null}
      </main>
    </div>
  );
}
