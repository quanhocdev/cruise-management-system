// src/modules/scheduler/pages/VesselDetailPage.jsx
import React, { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useVesselDetail from "../hooks/useVesselDetail";
import DetailHero from "../components/vessel/DetailHero";
import RouteProgress from "../components/vessel/RouteProgress";
import WeatherPanel from "../components/vessel/WeatherPanel";
import VesselRouteMap from "../components/vessel/VesselRouteMap"; // Component bản đồ riêng biệt
import RouteInfoPanel from "../components/vessel/RouteInfoPanel"; // Component thông số DTO riêng biệt
import "../styles/vessel-shared.css";
import "../styles/VesselDetailPage.css";

export default function VesselDetailPage() {
  const { tourId } = useParams();
  const navigate = useNavigate();
  const {
    tourDetails,
    routeOverview,
    routeLegs,
    vesselPosition,
    loading,
    error,
    reload,
  } = useVesselDetail(tourId);

  // State quản lý chặng đang được chọn xem (null = xem tổng quan)
  const [selectedLeg, setSelectedLeg] = useState(null);

  const lat =
    vesselPosition?.latitude ??
    tourDetails?.currentPosition?.latitude ??
    tourDetails?.departurePort?.latitude;
  const lon =
    vesselPosition?.longitude ??
    tourDetails?.currentPosition?.longitude ??
    tourDetails?.departurePort?.longitude;

  // Xác định GeoJSON hiện tại cần truyền cho bản đồ vẽ (nếu chọn chặng -> lấy GeoJSON chặng, ngược lại lấy tổng quan)
  const activeGeoJson = selectedLeg
    ? selectedLeg.routeGeoJson
    : routeOverview?.routeGeoJson;

  return (
    <div className="vm-page">
      <DetailHero
        tour={tourDetails}
        loading={loading}
        onBack={() => navigate("/scheduler/vessels/monitoring")}
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

            {/* Cột bản đồ (Chuyên vẽ Mapbox) */}
            <div className="vdp-full">
              <div
                style={{
                  background: "#fff",
                  borderRadius: "16px",
                  padding: "16px",
                  border: "1px solid #e2e8f0",
                }}
              >
                <h3
                  style={{
                    margin: "0 0 12px 0",
                    fontSize: "15px",
                    fontWeight: "700",
                    color: "#1e293b",
                  }}
                >
                  Bản đồ Giám sát Hành trình & Vị trí Tàu
                </h3>
                <VesselRouteMap
                  activeGeoJson={activeGeoJson}
                  vesselPosition={vesselPosition}
                />
              </div>
            </div>

            {/* Cột thông số DTO (Chuyên hiển thị text, thông tin, danh sách chặng) */}
            <div className="vdp-full">
              <RouteInfoPanel
                overview={routeOverview}
                legs={routeLegs}
                selectedLegId={selectedLeg?.legId}
                onSelectOverview={() => setSelectedLeg(null)}
                onSelectLeg={(leg) => setSelectedLeg(leg)}
              />
            </div>

            {/* Panel thời tiết */}
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
