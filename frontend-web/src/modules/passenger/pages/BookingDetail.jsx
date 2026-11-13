import { useState, useEffect } from "react";
import { Link, useParams } from "react-router-dom";
import { Container, Row, Col, Spinner, Alert } from "react-bootstrap";

import useBookingDetail from "../hooks/useBookingDetail";
import useBookingRefunds from "../hooks/useBookingRefunds";

import BookingDetailHero from "../components/booking-detail/BookingDetailHero";
import OrderOverview from "../components/booking-detail/OrderOverview";
import PassengerRow from "../components/booking-detail/PassengerRow";
import PaymentSummary from "../components/booking-detail/PaymentSummary";
import EditPassengerModal from "../components/booking-detail/EditPassengerModal";
import CancelBookingModal from "../components/booking-detail/CancelBookingModal";
import BookingRefundModal from "../components/booking-detail/BookingRefundModal";

import "../styles/BookingDetail.css";

export default function BookingDetail() {
  const { bookingId } = useParams();

  const {
    booking,
    loading,
    cancelling,
    error,
    success,
    fetchBooking,
    cancelBooking,
    savePassenger,
  } = useBookingDetail(bookingId);

  const {
    preview,
    latestRefund,
    loading: refundLoading,
    submitting: refundSubmitting,
    error: refundError,
    success: refundSuccess,
    loadRefundPreview,
    createRefund,
    loadLatestRefund,
    clearPreview,
    clearMessages: clearRefundMessages,
  } = useBookingRefunds();

  const [editingPassenger, setEditingPassenger] = useState(null);
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [showRefundModal, setShowRefundModal] = useState(false);

  useEffect(() => {
    if (bookingId) {
      loadLatestRefund(bookingId);
    }
  }, [bookingId]);

  const handleConfirmCancel = async () => {
    const result = await cancelBooking();

    if (result) {
      setShowCancelModal(false);
    }
  };

  const handleOpenRefund = async () => {
    clearRefundMessages();
    setShowRefundModal(true);
    await loadRefundPreview(bookingId);
  };

  const handleRetryRefundPreview = async () => {
    clearRefundMessages();
    await loadRefundPreview(bookingId);
  };

  const handleConfirmRefund = async (reason) => {
    const result = await createRefund(bookingId, { reason });

    if (result) {
      setShowRefundModal(false);
      clearPreview();

      await fetchBooking({ silent: true });
      await loadLatestRefund(bookingId);
    }
  };

  const handleHideRefundModal = () => {
    if (refundSubmitting) return;

    setShowRefundModal(false);
    clearPreview();
    clearRefundMessages();
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
        {refundSuccess && <Alert variant="success">{refundSuccess}</Alert>}

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
              latestRefund={latestRefund}
              onRefund={handleOpenRefund}
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

      <BookingRefundModal
        show={showRefundModal}
        preview={preview}
        loading={refundLoading}
        submitting={refundSubmitting}
        error={refundError}
        onHide={handleHideRefundModal}
        onRetry={handleRetryRefundPreview}
        onConfirm={handleConfirmRefund}
      />
    </div>
  );
}
