import React from "react";
import { Card, Spinner, Alert, Button, Row, Col } from "react-bootstrap";
import {
  Ticket,
  CalendarDays,
  Users,
  CreditCard,
  Package,
  Clock,
  RefreshCw,
  Hash,
} from "lucide-react";

import "./TripBookingSection.css";

function formatDate(value) {
  if (!value) return "Chưa cập nhật";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function formatPrice(value) {
  if (value === null || value === undefined || value === "") {
    return "Chưa cập nhật";
  }

  const number = Number(value);

  if (!Number.isFinite(number)) return value;

  return `${number.toLocaleString("vi-VN")} ₫`;
}

function getStatusLabel(status) {
  const labels = {
    PENDING: "Chờ xử lý",
    WAITING: "Chờ xác nhận",
    CONFIRMED: "Đã xác nhận",
    PAID: "Đã thanh toán",
    COMPLETED: "Hoàn thành",
    CANCELLED: "Đã hủy",
    REFUNDED: "Đã hoàn tiền",
    EXPIRED: "Hết hạn",
  };

  return labels[status] || status || "Chưa cập nhật";
}

function getStatusClass(status) {
  const classes = {
    PENDING: "pending",
    WAITING: "pending",
    CONFIRMED: "confirmed",
    PAID: "success",
    COMPLETED: "success",
    CANCELLED: "danger",
    REFUNDED: "secondary",
    EXPIRED: "danger",
  };

  return classes[status] || "secondary";
}

function InfoItem({ icon: Icon, label, value }) {
  return (
    <div className="trip-booking-info-item">
      <div className="trip-booking-info-icon">
        <Icon size={17} />
      </div>

      <div className="trip-booking-info-content">
        <span>{label}</span>
        <strong>{value ?? "Chưa cập nhật"}</strong>
      </div>
    </div>
  );
}

export default function TripBookingSection({
  booking,
  loading = false,
  error = "",
  onReload,
}) {
  if (loading && !booking) {
    return (
      <section className="trip-booking-section">
        <div className="trip-booking-state">
          <Spinner animation="border" size="sm" />
          <span>Đang tải thông tin đặt chỗ...</span>
        </div>
      </section>
    );
  }

  if (error && !booking) {
    return (
      <section className="trip-booking-section">
        <Alert variant="danger" className="trip-booking-alert">
          <div>{error}</div>

          {onReload && (
            <Button
              variant="outline-danger"
              size="sm"
              className="mt-2"
              onClick={onReload}
            >
              Thử lại
            </Button>
          )}
        </Alert>
      </section>
    );
  }

  if (!booking) return null;

  const bookingCode =
    booking.bookingCode ||
    booking.code ||
    booking.bookingNumber ||
    booking.id ||
    "Chưa cập nhật";

  const status = booking.status || booking.bookingStatus;

  const createdAt =
    booking.createdAt || booking.createdDate || booking.bookingDate;

  const numberOfPassengers =
    booking.numberPassengers ??
    booking.numberOfPassengers ??
    booking.passengerCount ??
    booking.totalPassengers ??
    booking.numberOfPeople;

  const numberOfRooms = booking.numberOfRooms;

  const packageName =
    booking.packageName || booking.tourPackageName || booking.package?.name;

  const totalAmount =
    booking.totalAmount ?? booking.totalPrice ?? booking.amount;

  const paymentStatus = booking.paymentStatus || booking.payment?.status;

  return (
    <section className="trip-booking-section">
      <div className="trip-booking-heading">
        <div>
          <span className="trip-booking-eyebrow">THÔNG TIN ĐẶT CHỖ</span>

          <h3>Đơn đặt chỗ của bạn</h3>

          <p>Thông tin đặt tour và trạng thái đơn đặt chỗ.</p>
        </div>

        {onReload && (
          <Button
            variant="outline-secondary"
            size="sm"
            onClick={onReload}
            disabled={loading}
          >
            <RefreshCw size={15} className="me-1" />
            Tải lại
          </Button>
        )}
      </div>

      {error && booking && (
        <Alert variant="warning" className="trip-booking-alert">
          Không thể cập nhật dữ liệu mới nhất: {error}
        </Alert>
      )}

      <Card className="trip-booking-card">
        <Card.Body>
          <div className="trip-booking-top">
            <div className="trip-booking-code">
              <div className="trip-booking-ticket-icon">
                <Ticket size={22} />
              </div>

              <div>
                <span>Mã đặt chỗ</span>
                <h4>{bookingCode}</h4>
              </div>
            </div>

            <span
              className={`trip-booking-status trip-booking-status-${getStatusClass(
                status,
              )}`}
            >
              {getStatusLabel(status)}
            </span>
          </div>

          <div className="trip-booking-divider" />

          <Row className="g-3">
            <Col xs={12} md={6}>
              <InfoItem
                icon={CalendarDays}
                label="Ngày đặt"
                value={formatDate(createdAt)}
              />
            </Col>

            <Col xs={12} md={6}>
              <InfoItem
                icon={Users}
                label="Số lượng hành khách"
                value={numberOfPassengers ?? "Chưa cập nhật"}
              />
            </Col>

            {numberOfRooms !== undefined && (
              <Col xs={12} md={6}>
                <InfoItem icon={Hash} label="Số phòng" value={numberOfRooms} />
              </Col>
            )}

            {packageName && (
              <Col xs={12} md={6}>
                <InfoItem icon={Package} label="Gói tour" value={packageName} />
              </Col>
            )}

            {totalAmount !== undefined && (
              <Col xs={12} md={6}>
                <InfoItem
                  icon={CreditCard}
                  label="Tổng giá trị"
                  value={formatPrice(totalAmount)}
                />
              </Col>
            )}

            {paymentStatus && (
              <Col xs={12} md={6}>
                <InfoItem
                  icon={Clock}
                  label="Trạng thái thanh toán"
                  value={paymentStatus}
                />
              </Col>
            )}
          </Row>
        </Card.Body>
      </Card>
    </section>
  );
}
