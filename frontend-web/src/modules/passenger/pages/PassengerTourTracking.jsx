// src/modules/passenger/pages/PassengerTourTracking.jsx
import { Alert, Container, Spinner } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import useBookedTours from "../hooks/useBookedTours";
import TourCard from "../../scheduler/components/vessel/TourCard";
import "../../scheduler/styles/vessel-shared.css";
import "./PassengerTourTracking.css";

export default function PassengerTourTracking() {
  const navigate = useNavigate();
  const { tours, loading, error } = useBookedTours();

  const handleOpenTour = (tour) => {
    if (!tour.id || !tour.bookingId) {
      console.error("Thiếu tourId hoặc bookingId:", tour);
      return;
    }

    navigate(`/passenger/tracking/${tour.id}/${tour.bookingId}`);
  };

  return (
    <div className="vm-page ptt-page">
      <header className="vm-hero ptt-hero">
        <Container fluid="lg">
          <h1>Hành trình của tôi</h1>
          <p>
            Theo dõi vị trí tàu, lịch trình và thời tiết của các chuyến đi bạn
            đã đặt.
          </p>
        </Container>
      </header>

      <Container fluid="lg" className="ptt-body">
        {error && <Alert variant="danger">{error}</Alert>}

        {loading ? (
          <div className="ptt-state">
            <Spinner animation="border" variant="primary" size="sm" /> Đang tải
            hành trình...
          </div>
        ) : tours.length === 0 && !error ? (
          <div className="ptt-state">
            Bạn chưa có tour nào đã xác nhận. Hành trình sẽ hiện ở đây sau khi
            đặt tour thành công.
          </div>
        ) : (
          <div className="ptt-grid">
            {tours.map((tour) => (
              <TourCard
                key={tour.id}
                tour={tour}
                actionLabel="Theo dõi hành trình"
                onOpen={() => handleOpenTour(tour)}
              />
            ))}
          </div>
        )}
      </Container>
    </div>
  );
}
