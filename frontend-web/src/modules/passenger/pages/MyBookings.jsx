import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Badge,
  Spinner,
  Alert,
} from "react-bootstrap";
import { CalendarDays, CreditCard, Users, Ship, ArrowLeft } from "lucide-react";
import usePassengerBookings from "../hooks/usePassengerBookings";
import BookingStatusFilter from "../components/BookingStatusFilter";
import TourStatusFilter from "../components/TourStatusFilter";
import "../styles/MyBooking.css";

const statusConfig = {
  PENDING_PAYMENT: { label: "Chờ thanh toán", bg: "warning", text: "dark" },
  CONFIRMED: { label: "Đã xác nhận", bg: "success", text: "white" },
  CANCELLED: { label: "Đã hủy", bg: "danger", text: "white" },
};

const formatMoney = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(value || 0);

const formatDateTime = (value) =>
  value
    ? new Intl.DateTimeFormat("vi-VN", {
        dateStyle: "medium",
        timeStyle: "short",
      }).format(new Date(value))
    : "—";

export default function MyBookings() {
  const { bookings, loading, error, loadMyBookings } = usePassengerBookings();

  // Khai báo state cho 2 loại bộ lọc
  const [filterBookingStatus, setFilterBookingStatus] = useState("ALL");
  const [filterTripStatus, setFilterTripStatus] = useState("ALL");

  useEffect(() => {
    loadMyBookings();
  }, [loadMyBookings]);
  console.log("Dữ liệu booking:", bookings);
  // Lọc danh sách booking theo cả 2 tiêu chí cùng lúc
  const filteredBookings = bookings.filter((booking) => {
    // 1. Lọc theo trạng thái đơn
    const matchBookingStatus =
      filterBookingStatus === "ALL" || booking.status === filterBookingStatus;

    // 2. Lọc theo trạng thái chuyến đi
    const matchTripStatus =
      filterTripStatus === "ALL" ||
      booking.tourStatusTrip === filterTripStatus ||
      booking.tour?.statusTrip === filterTripStatus;

    return matchBookingStatus && matchTripStatus;
  });

  return (
    <div className="tour-public-page bg-light min-vh-100 pb-5">
      {/* Banner đầu trang */}
      <div className="tour-public-hero text-white py-5 mb-4 shadow-sm text-center">
        <Container>
          <div className="d-flex justify-content-start mb-2">
            <Link
              to="/passenger/dashboard"
              className="text-white text-decoration-none small d-flex align-items-center gap-1"
            >
              <ArrowLeft size={16} /> Quay lại danh sách tour
            </Link>
          </div>
          <h1 className="fw-bold mb-2">🎫 Hành Trình Đặt Chỗ Của Tôi</h1>
          <p className="lead text-white-50 mb-0">
            Quản lý các đơn đặt vé và theo dõi lịch trình chuyến đi của bạn.
          </p>
        </Container>
      </div>

      <Container>
        {/* Bộ lọc 1: Trạng thái đơn vé */}
        <BookingStatusFilter
          filterStatus={filterBookingStatus}
          setFilterStatus={setFilterBookingStatus}
          totalCount={bookings.length}
        />

        {/* Bộ lọc 2: Trạng thái chuyến đi */}
        <div className="d-flex align-items-center justify-content-between flex-wrap gap-3 mb-4">
          <TourStatusFilter
            filterStatus={filterTripStatus}
            setFilterStatus={setFilterTripStatus}
            totalCount={bookings.length}
          />
          <span className="text-muted small">
            Hiển thị {filteredBookings.length} đơn vé
          </span>
        </div>

        {/* Trạng thái tải dữ liệu */}
        {loading && (
          <div className="text-center py-5">
            <Spinner animation="border" variant="primary" />
            <p className="mt-2 text-muted">Đang tải danh sách booking...</p>
          </div>
        )}

        {/* Thông báo lỗi */}
        {error && (
          <Alert variant="danger" className="my-3">
            {error}
          </Alert>
        )}

        {/* Khi không có dữ liệu */}
        {!loading && !error && filteredBookings.length === 0 && (
          <Alert variant="info" className="text-center py-5">
            <Ship size={40} className="mb-2 text-muted" />
            <h5 className="fw-bold">Bạn chưa có đơn vé nào phù hợp</h5>
            <p className="text-muted small mb-3">
              Hãy khám phá thêm các hành trình du thuyền mới nhé.
            </p>
            <Button
              as={Link}
              to="/passenger/dashboard"
              variant="primary"
              size="sm"
              className="rounded-pill px-4"
            >
              Khám phá tour ngay
            </Button>
          </Alert>
        )}

        {/* Lưới hiển thị danh sách Booking */}
        <Row xs={1} md={2} lg={3} className="g-4">
          {filteredBookings.map((booking) => {
            const currentStatus = statusConfig[booking.status] || {
              label: booking.status,
              bg: "secondary",
              text: "white",
            };

            return (
              <Col key={booking.id}>
                <Card className="tour-card h-100 shadow-sm border-0 rounded-4 p-4 d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-center mb-3">
                    <span className="text-muted small fw-semibold">
                      MÃ:{" "}
                      <strong>{booking.bookingCode || `#${booking.id}`}</strong>
                    </span>
                    <Badge
                      bg={currentStatus.bg}
                      text={currentStatus.text}
                      className="px-3 py-2 rounded-pill shadow-sm"
                    >
                      {currentStatus.label}
                    </Badge>
                  </div>

                  <div className="tour-info-box bg-light p-3 rounded-3 mb-3 small text-secondary flex-grow-1">
                    <div className="mb-2 text-dark fw-bold">
                      {booking.tourName ||
                        booking.tour?.name ||
                        "Hành trình du thuyền"}
                    </div>
                    <div className="d-flex align-items-center gap-2 mb-2">
                      <CalendarDays size={16} className="text-primary" />
                      <span>
                        <strong>Ngày tạo:</strong>{" "}
                        {formatDateTime(booking.createdAt)}
                      </span>
                    </div>
                    <div className="d-flex align-items-center gap-2 mb-2">
                      <Users size={16} className="text-primary" />
                      <span>
                        <strong>Hành khách:</strong>{" "}
                        {booking.passengers?.length || 0} người
                      </span>
                    </div>
                    <div className="d-flex align-items-center gap-2">
                      <CreditCard size={16} className="text-primary" />
                      <span>
                        <strong>Tổng tiền:</strong>{" "}
                        <span className="text-primary fw-bold">
                          {formatMoney(booking.totalAmount)}
                        </span>
                      </span>
                    </div>
                  </div>

                  <div className="pt-3 border-top text-end mt-auto">
                    <Button
                      as={Link}
                      to={`/passenger/bookings/${booking.id}`}
                      variant="outline-primary"
                      size="sm"
                      className="rounded-pill px-4 w-100"
                    >
                      Xem chi tiết & Quản lý hành khách
                    </Button>
                  </div>
                </Card>
              </Col>
            );
          })}
        </Row>
      </Container>
    </div>
  );
}
