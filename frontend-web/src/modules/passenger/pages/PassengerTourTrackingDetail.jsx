// src/modules/passenger/pages/PassengerTourTrackingDetail.jsx
import React, { useState } from "react";
import { Alert, Button, Container, Spinner } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

// Hook riêng của module passenger (cần đảm bảo hook này trả về thêm routeOverview, routeLegs, vesselPosition)
import usePassengerDetail from "../hooks/usePassengerDetail";

// Dùng chung hoàn toàn component và CSS chuẩn từ module scheduler
import DetailHero from "../../scheduler/components/vessel/DetailHero";
import RouteProgress from "../../scheduler/components/vessel/RouteProgress";
import VesselRouteMap from "../../scheduler/components/vessel/VesselRouteMap";
import RouteInfoPanel from "../../scheduler/components/vessel/RouteInfoPanel";
import LivePositionPanel from "../../scheduler/components/vessel/LivePositionPanel";
import WeatherPanel from "../../scheduler/components/vessel/WeatherPanel";

import "../../scheduler/styles/vessel-shared.css";
import "../../scheduler/styles/VesselDetailPage.css";
import "./PassengerTourTrackingDetail.css";

export default function PassengerTourTrackingDetail() {
  const { tourId } = useParams();
  const navigate = useNavigate();

  // Sử dụng hook chi tiết (hoặc hook passenger tương đương trả về đầy đủ overview, legs, vesselPosition)
  const {
    tourDetails,
    routeOverview,
    routeLegs,
    vesselPosition,
    loading,
    error,
    reload,
  } = usePassengerDetail(tourId);

  // State quản lý chặng đang được chọn xem trên bản đồ (null = xem tổng quan toàn tuyến)
  const [selectedLeg, setSelectedLeg] = useState(null);

  // Ưu tiên vị trí tàu real-time từ Redis, sau đó đến tọa độ cảng đi
  const lat =
    vesselPosition?.latitude ??
    tourDetails?.currentPosition?.latitude ??
    tourDetails?.departurePort?.latitude;
  const lon =
    vesselPosition?.longitude ??
    tourDetails?.currentPosition?.longitude ??
    tourDetails?.departurePort?.longitude;

  // Xác định GeoJSON để vẽ bản đồ (chọn chặng nào -> vẽ chặng đó, ngược lại vẽ tổng quan)
  const activeGeoJson = selectedLeg
    ? selectedLeg.routeGeoJson
    : routeOverview?.routeGeoJson;

  return (
    <div className="vm-page">
      <DetailHero
        tour={tourDetails}
        loading={loading}
        onBack={() => navigate("/passenger/tracking")}
        onReload={reload}
      />

      <main className="vdp-body pt-3">
        {loading && !tourDetails ? (
          <div className="vdp-state">
            <Spinner animation="border" variant="primary" size="sm" /> Đang tải
            dữ liệu hành trình...
          </div>
        ) : error ? (
          <Container fluid="lg">
            <Alert
              variant="danger"
              className="d-flex justify-content-between align-items-center"
            >
              <span>{error}</span>
              <Button size="sm" variant="danger" onClick={reload}>
                Thử lại
              </Button>
            </Alert>
          </Container>
        ) : tourDetails ? (
          <div className="vdp-grid">
            {/* 1. Thanh tiến trình ngang (Route Progress Bar) */}
            <div className="vdp-full">
              <RouteProgress tour={tourDetails} />
            </div>

            {/* 2. Bản đồ Mapbox trực quan & vị trí tàu real-time */}
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

            {/* 3. Bảng thông số DTO (Cảng đi/đến kèm badge ngày tháng, danh sách các chặng) */}
            <div className="vdp-full">
              <RouteInfoPanel
                overview={routeOverview}
                legs={routeLegs}
                selectedLegId={selectedLeg?.legId}
                onSelectOverview={() => setSelectedLeg(null)}
                onSelectLeg={(leg) => setSelectedLeg(leg)}
                tourStartDate={tourDetails?.startDate}
                tourEndDate={tourDetails?.endDate}
              />
            </div>

            {/* 4. Panel vị trí GPS trực tiếp & Dự báo thời tiết Open-Meteo */}
            <div
              className="vdp-full d-grid gap-3"
              style={{
                gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))",
              }}
            >
              <LivePositionPanel
                position={vesselPosition || tourDetails?.currentPosition}
              />
              <WeatherPanel
                lat={lat}
                lon={lon}
                startDate={tourDetails.startDate}
                endDate={tourDetails.endDate}
              />
            </div>
          </div>
        ) : null}
      </main>
    </div>
  );
}
