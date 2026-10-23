import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Container, Row, Col, Spinner, Alert } from "react-bootstrap";
import useBookingDetail from "../hooks/useBookingDetail";

import BookingDetailHero from "../components/booking-detail/BookingDetailHero";
import OrderOverview from "../components/booking-detail/OrderOverview";
import PassengerRow from "../components/booking-detail/PassengerRow";
import PaymentSummary from "../components/booking-detail/PaymentSummary";
import EditPassengerModal from "../components/booking-detail/EditPassengerModal";
import CancelBookingModal from "../components/booking-detail/CancelBookingModal";

import "../styles/BookingDetail.css";

export default function BookingDetail() {
  const { bookingId } = useParams();
  const {
    booking,
    loading,
    cancelling,
    error,
    success,
    cancelBooking,
    savePassenger,
  } = useBookingDetail(bookingId);

  const [editingPassenger, setEditingPassenger] = useState(null);
  const [showCancelModal, setShowCancelModal] = useState(false);

  const handleConfirmCancel = async () => {
    await cancelBooking();
    setShowCancelModal(false);
  };

  if (loading) {
    return (
      <div className="bd-state">
        <Spinner animation="border" variant="primary" />
        <p>Đang tải chi tiết đơn...</p>
      </div>
    );
  }

  if (!booking) {
    return (
      <Container className="py-5 text-center">
        <Alert variant="danger">
          {error || "Không tìm thấy thông tin đơn hàng."}
        </Alert>
        <Link to="/passenger/bookings" className="bd-back-link">
          Quay lại danh sách đặt chỗ
        </Link>
      </Container>
    );
  }

  const canEdit = booking.status !== "CANCELLED";

  return (
    <div className="booking-detail-page">
      <BookingDetailHero booking={booking} />

      <Container className="bd-content">
        {error && <Alert variant="danger">{error}</Alert>}
        {success && <Alert variant="success">{success}</Alert>}

        <Row className="g-4">
          <Col lg={8}>
            <OrderOverview booking={booking} />

            <section className="bd-passengers">
              <h2 className="bd-passengers__title">
                Danh sách hành khách
                <span>{booking.passengers?.length || 0} người</span>
              </h2>

              {booking.passengers?.length > 0 ? (
                <div className="bd-passengers__list">
                  {booking.passengers.map((p, index) => (
                    <PassengerRow
                      key={p.id || index}
                      index={index}
                      passenger={p}
                      canEdit={canEdit}
                      onEdit={setEditingPassenger}
                    />
                  ))}
                </div>
              ) : (
                <p className="bd-passengers__empty">
                  Đơn này chưa có hành khách nào.
                </p>
              )}
            </section>
          </Col>

          <Col lg={4}>
            <PaymentSummary
              booking={booking}
              cancelling={cancelling}
              onCancel={() => setShowCancelModal(true)}
            />
          </Col>
        </Row>
      </Container>

      {editingPassenger && (
        <EditPassengerModal
          key={editingPassenger.id}
          passenger={editingPassenger}
          onSave={savePassenger}
          onHide={() => setEditingPassenger(null)}
        />
      )}

      <CancelBookingModal
        show={showCancelModal}
        loading={cancelling}
        onHide={() => setShowCancelModal(false)}
        onConfirm={handleConfirmCancel}
      />
    </div>
  );
}
