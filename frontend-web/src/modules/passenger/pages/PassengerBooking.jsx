import { useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Form,
  Button,
  Alert,
  Spinner,
} from "react-bootstrap";
import { useSearchParams, useNavigate } from "react-router-dom";
import { useAuth } from "../../../context/AuthContext";
import useBookings from "../hooks/useBookings";
import usePassengers from "../hooks/usePassengers";
import useBookingForm from "../hooks/useBookingForm";
import { usePublicTourDetail } from "../../guest/hooks/usePublicTours";

import BookingSection from "../components/booking/BookingSection";
import PackageSelector from "../components/booking/PackageSelector";
import RoomStepper from "../components/booking/RoomStepper";
import ContactForm from "../components/booking/ContactForm";
import PassengerList from "../components/booking/PassengerList";
import BookingSummary from "../components/booking/BookingSummary";

import "../styles/PassengerBooking.css";

export default function PassengerBooking() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const tourId = searchParams.get("tourId");
  const packageIdFromUrl = searchParams.get("packageId");

  const { tour, loading: tourLoading } = usePublicTourDetail(tourId);
  const {
    createBooking,
    submitting,
    error: bookingError,
    success,
  } = useBookings();
  const { passengers: savedPassengers, loadPassengers } = usePassengers();

  useEffect(() => {
    loadPassengers();
  }, [loadPassengers]);

  const form = useBookingForm({
    user,
    tour,
    packageIdFromUrl,
    savedPassengers,
  });

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!tourId || !form.tourPackageId) {
      alert("Vui lòng chọn đầy đủ thông tin Tour và Gói Tour.");
      return;
    }

    if (form.isPassengerExceeded) {
      alert(
        `Số lượng hành khách (${form.passengers.length} người) vượt quá sức chứa của ${form.numberOfRooms} phòng (tối đa ${form.totalAllowedPassengers} khách). Vui lòng đăng ký thêm phòng!`,
      );
      return;
    }

    try {
      await createBooking(form.buildFormData(tourId));
      alert("Đặt vé thành công!");
      navigate("/passenger/bookings");
    } catch (err) {
      console.error("Lỗi chi tiết từ Server trả về:", err.response || err);
      alert(
        err.response?.data?.message ||
          err.message ||
          "Đã xảy ra lỗi trong quá trình đặt vé.",
      );
    }
  };

  if (tourLoading) {
    return (
      <div className="booking-page-state">
        <Spinner animation="border" variant="primary" />
        <p>Đang tải thông tin đặt vé...</p>
      </div>
    );
  }

  return (
    <div className="passenger-booking-page">
      <Container>
        <header className="booking-page-header">
          <Button
            variant="link"
            className="booking-page-back"
            onClick={() => navigate(-1)}
          >
            ← Quay lại
          </Button>
          <h1 className="booking-page-title">Đặt vé trực tuyến</h1>
          {tour?.name && <p className="booking-page-sub">{tour.name}</p>}
        </header>

        {bookingError && <Alert variant="danger">{bookingError}</Alert>}
        {success && <Alert variant="success">{success}</Alert>}

        <Form onSubmit={handleSubmit}>
          <Row className="g-4">
            <Col lg={8}>
              <BookingSection
                step={1}
                title="Chọn gói tour và số phòng"
                hint="Giá được tính theo phòng. Chọn gói phù hợp với nhu cầu của bạn."
              >
                <PackageSelector
                  packages={tour?.packages}
                  selectedId={form.tourPackageId}
                  onSelect={form.setTourPackageId}
                />
                <RoomStepper
                  value={form.numberOfRooms}
                  onChange={form.changeRooms}
                  capacityPerRoom={form.maxPassengersPerRoom}
                  totalCapacity={form.totalAllowedPassengers}
                />
              </BookingSection>

              <BookingSection step={2} title="Thông tin liên hệ chính">
                <ContactForm
                  name={form.primaryContactName}
                  phone={form.primaryContactPhone}
                  onNameChange={form.setPrimaryContactName}
                  onPhoneChange={form.setPrimaryContactPhone}
                />
              </BookingSection>

              <BookingSection
                step={3}
                title="Danh sách hành khách"
                action={
                  <Button
                    variant="outline-primary"
                    size="sm"
                    className="booking-add-btn"
                    onClick={form.addPassenger}
                  >
                    + Thêm hành khách
                  </Button>
                }
              >
                <PassengerList
                  passengers={form.passengers}
                  savedPassengers={savedPassengers}
                  exceeded={form.isPassengerExceeded}
                  rooms={form.numberOfRooms}
                  totalCapacity={form.totalAllowedPassengers}
                  onChange={form.updatePassenger}
                  onRemove={form.removePassenger}
                  onPickSaved={form.fillFromSaved}
                />
              </BookingSection>
            </Col>

            <Col lg={4}>
              <BookingSummary
                tour={tour}
                tourId={tourId}
                pkg={form.selectedPackage}
                rooms={form.numberOfRooms}
                passengerCount={form.passengers.length}
                totalPrice={form.totalPrice}
                submitting={submitting}
                exceeded={form.isPassengerExceeded}
                disabled={
                  submitting || !form.tourPackageId || form.isPassengerExceeded
                }
              />
            </Col>
          </Row>
        </Form>
      </Container>
    </div>
  );
}
