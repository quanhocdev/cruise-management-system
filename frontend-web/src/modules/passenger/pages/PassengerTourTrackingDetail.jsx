// src/modules/passenger/pages/PassengerTourTrackingDetail.jsx
import { Alert, Button, Container, Spinner } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import useTourTracking from "../hooks/useTourTracking";
import ItineraryDays from "../components/tracking/ItineraryDays";
// Dùng lại component/CSS của module scheduler (nên dời sang src/components/vessel/ về sau)
import DetailHero from "../../scheduler/components/vessel/DetailHero";
import RouteProgress from "../../scheduler/components/vessel/RouteProgress";
import LivePositionPanel from "../../scheduler/components/vessel/LivePositionPanel";
import WeatherPanel from "../../scheduler/components/vessel/WeatherPanel";
import "../../scheduler/styles/vessel-shared.css";
import "./PassengerTourTrackingDetail.css";

export default function PassengerTourTrackingDetail() {
  const { tourId } = useParams();
  const navigate = useNavigate();
  const { tracking, loading, error, polling, lastFetchedAt, reload } =
    useTourTracking(tourId);

  // Ưu tiên vị trí tàu hiện tại, không có thì lấy cảng đi
  const lat =
    tracking?.currentPosition?.latitude ?? tracking?.departurePort?.latitude;
  const lon =
    tracking?.currentPosition?.longitude ?? tracking?.departurePort?.longitude;

  return (
    <div className="vm-page">
      <DetailHero
        tour={tracking}
        loading={loading}
        onBack={() => navigate("/passenger/tracking")}
        onReload={reload}
      />

      <Container fluid="lg" className="ptd-body">
        {loading && !tracking ? (
          <div className="ptd-state">
            <Spinner animation="border" variant="primary" size="sm" /> Đang tải
            hành trình...
          </div>
        ) : error ? (
          <Alert
            variant="danger"
            className="d-flex justify-content-between align-items-center"
          >
            <span>{error}</span>
            <Button size="sm" variant="danger" onClick={reload}>
              Thử lại
            </Button>
          </Alert>
        ) : tracking ? (
          <div className="ptd-stack">
            {polling && (
              <small className="ptd-live">
                ● Tự động cập nhật mỗi 60 giây
                {lastFetchedAt &&
                  ` · lần cuối ${lastFetchedAt.toLocaleTimeString("vi-VN")}`}
              </small>
            )}

            {tracking.description && (
              <section className="ptd-desc">{tracking.description}</section>
            )}

            <RouteProgress tour={tracking} />

            <div className="ptd-duo">
              <LivePositionPanel position={tracking.currentPosition} />
              <WeatherPanel
                lat={lat}
                lon={lon}
                startDate={tracking.startDate}
                endDate={tracking.endDate}
              />
            </div>

            <ItineraryDays schedules={tracking.schedules} />
          </div>
        ) : null}
      </Container>
    </div>
  );
}
