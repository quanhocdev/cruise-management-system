// src/modules/guest/pages/TourPublic.jsx
import { useState } from "react";
import { Container, Alert } from "react-bootstrap";
import { usePublicTours } from "../hooks/usePublicTours";

import TourHero from "../components/tour-list/TourHero";
import TourFilters from "../components/tour-list/TourFilters";
import TourCard from "../components/tour-list/TourCard";
import TourCardSkeleton from "../components/tour-list/TourCardSkeleton";

import "../styles/TourPublic.css";

export default function TourPublic() {
  const { tours, loading, error } = usePublicTours();
  const [filterStatus, setFilterStatus] = useState("ALL");
  const [filterBooking, setFilterBooking] = useState("ALL");

  const filteredTours = tours.filter((tour) => {
    const matchTrip =
      filterStatus === "ALL" || tour.statusTrip === filterStatus;
    const matchBooking =
      filterBooking === "ALL" || tour.statusBooking === filterBooking;
    return matchTrip && matchBooking;
  });

  const resetFilters = () => {
    setFilterStatus("ALL");
    setFilterBooking("ALL");
  };

  return (
    <div className="tour-public-page">
      <TourHero totalCount={loading ? null : tours.length} />

      <Container className="tour-public-content">
        <TourFilters
          tours={tours}
          loading={loading}
          shownCount={filteredTours.length}
          filterStatus={filterStatus}
          filterBooking={filterBooking}
          onStatusChange={setFilterStatus}
          onBookingChange={setFilterBooking}
          onReset={resetFilters}
        />

        {error && (
          <Alert variant="danger" className="mb-4">
            {error}
          </Alert>
        )}

        {loading && (
          <div className="tour-grid">
            {Array.from({ length: 6 }).map((_, index) => (
              <TourCardSkeleton key={index} />
            ))}
          </div>
        )}

        {!loading && !error && filteredTours.length === 0 && (
          <div className="tour-empty">
            <p>Không có tour nào phù hợp với bộ lọc hiện tại.</p>
            <button type="button" onClick={resetFilters}>
              Xóa bộ lọc
            </button>
          </div>
        )}

        {!loading && !error && filteredTours.length > 0 && (
          <div className="tour-grid">
            {filteredTours.map((tour) => (
              <TourCard key={tour.id} tour={tour} />
            ))}
          </div>
        )}
      </Container>
    </div>
  );
}
