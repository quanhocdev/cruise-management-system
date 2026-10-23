import { useState, useEffect } from "react";
import { Container, Spinner, Alert } from "react-bootstrap";
import usePassengerBookings from "../hooks/usePassengerBookings";

import MyBookingsHero from "../components/my-bookings/MyBookingsHero";
import BookingFilters from "../components/my-bookings/BookingFilters";
import BookingCard from "../components/my-bookings/BookingCard";
import BookingsEmpty from "../components/my-bookings/BookingsEmpty";
import { getTripStatus } from "../components/my-bookings/helpers";

import "../styles/MyBooking.css";

export default function MyBookings() {
  const { bookings, loading, error, loadMyBookings } = usePassengerBookings();

  const [filterBooking, setFilterBooking] = useState("ALL");
  const [filterTrip, setFilterTrip] = useState("ALL");

  useEffect(() => {
    loadMyBookings();
  }, [loadMyBookings]);

  const filteredBookings = bookings.filter((b) => {
    const matchBooking = filterBooking === "ALL" || b.status === filterBooking;
    const matchTrip = filterTrip === "ALL" || getTripStatus(b) === filterTrip;
    return matchBooking && matchTrip;
  });

  const resetFilters = () => {
    setFilterBooking("ALL");
    setFilterTrip("ALL");
  };

  return (
    <div className="my-bookings-page">
      <MyBookingsHero totalCount={loading ? null : bookings.length} />

      <Container className="my-bookings-content">
        <BookingFilters
          bookings={bookings}
          loading={loading}
          shownCount={filteredBookings.length}
          filterBooking={filterBooking}
          filterTrip={filterTrip}
          onBookingChange={setFilterBooking}
          onTripChange={setFilterTrip}
          onReset={resetFilters}
        />

        {error && (
          <Alert variant="danger" className="mb-4">
            {error}
          </Alert>
        )}

        {loading && (
          <div className="my-bookings-state">
            <Spinner animation="border" variant="primary" />
            <p>Đang tải danh sách đặt chỗ...</p>
          </div>
        )}

        {!loading && !error && filteredBookings.length === 0 && (
          <BookingsEmpty
            hasAnyBooking={bookings.length > 0}
            onReset={resetFilters}
          />
        )}

        {!loading && !error && filteredBookings.length > 0 && (
          <div className="my-bookings-grid">
            {filteredBookings.map((booking) => (
              <BookingCard key={booking.id} booking={booking} />
            ))}
          </div>
        )}
      </Container>
    </div>
  );
}
