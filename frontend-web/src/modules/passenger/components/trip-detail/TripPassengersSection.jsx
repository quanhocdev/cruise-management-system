import React from "react";
import { Card, Spinner, Alert, Button, Row, Col } from "react-bootstrap";
import {
  Users,
  UserRound,
  Mail,
  Phone,
  CalendarDays,
  CreditCard,
  RefreshCw,
} from "lucide-react";

import "./TripPassengersSection.css";

function unwrapList(value) {
  if (Array.isArray(value)) return value;
  if (!value || typeof value !== "object") return [];

  if (Array.isArray(value.passengers)) return value.passengers;
  if (Array.isArray(value.items)) return value.items;
  if (Array.isArray(value.content)) return value.content;
  if (Array.isArray(value.data)) return value.data;

  return [];
}

function formatDate(value) {
  if (!value) return "Chưa cập nhật";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

function getPassengerName(passenger) {
  return (
    passenger?.fullName ||
    passenger?.passengerName ||
    passenger?.name ||
    [passenger?.lastName, passenger?.firstName].filter(Boolean).join(" ") ||
    "Hành khách"
  );
}

function getPassengerId(passenger, index) {
  return (
    passenger?.id ??
    passenger?.passengerId ??
    passenger?.bookingPassengerId ??
    index
  );
}

function getPassengerType(passenger) {
  return (
    passenger?.passengerType || passenger?.type || passenger?.category || ""
  );
}

function PassengerInfo({ icon: Icon, label, value }) {
  if (!value) return null;

  return (
    <div className="trip-passenger-info">
      <Icon size={15} />
      <div>
        <span>{label}</span>
        <strong>{value}</strong>
      </div>
    </div>
  );
}

function PassengerCard({ passenger, index }) {
  const name = getPassengerName(passenger);

  const email = passenger?.email || passenger?.passengerEmail;

  const phone = passenger?.phone || passenger?.phoneNumber;

  const dateOfBirth = passenger?.dateOfBirth || passenger?.birthDate;

  const identityNumber =
    passenger?.identityNumber ||
    passenger?.citizenId ||
    passenger?.passportNumber;

  const passengerType = getPassengerType(passenger);

  return (
    <Card className="trip-passenger-card h-100">
      <Card.Body>
        <div className="trip-passenger-card-header">
          <div className="trip-passenger-avatar">
            <UserRound size={22} />
          </div>

          <div className="trip-passenger-name">
            <span>Hành khách {index + 1}</span>
            <h5>{name}</h5>
          </div>
        </div>

        {passengerType && (
          <div className="trip-passenger-type">{passengerType}</div>
        )}

        <div className="trip-passenger-details">
          <PassengerInfo icon={Mail} label="Email" value={email} />

          <PassengerInfo icon={Phone} label="Số điện thoại" value={phone} />

          <PassengerInfo
            icon={CalendarDays}
            label="Ngày sinh"
            value={formatDate(dateOfBirth)}
          />

          <PassengerInfo
            icon={CreditCard}
            label="Giấy tờ tùy thân"
            value={identityNumber}
          />
        </div>
      </Card.Body>
    </Card>
  );
}

export default function TripPassengersSection({
  passengers,
  loading = false,
  error = "",
  onReload,
}) {
  const items = unwrapList(passengers);

  return (
    <section className="trip-passengers-section">
      <div className="trip-passengers-heading">
        <div>
          <span className="trip-passengers-eyebrow">DANH SÁCH HÀNH KHÁCH</span>

          <h3>Hành khách trong chuyến đi</h3>

          <p>Thông tin những hành khách thuộc đơn đặt chỗ này.</p>
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

      {loading && (
        <div className="trip-passengers-state">
          <Spinner animation="border" size="sm" />
          <span>Đang tải danh sách hành khách...</span>
        </div>
      )}

      {!loading && error && (
        <Alert variant="danger" className="trip-passengers-alert">
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
      )}

      {!loading && !error && items.length === 0 && (
        <div className="trip-passengers-empty">
          <Users size={32} />
          <h5>Chưa có thông tin hành khách</h5>
          <p>Danh sách hành khách hiện chưa có dữ liệu.</p>
        </div>
      )}

      {!loading && !error && items.length > 0 && (
        <>
          <div className="trip-passengers-count">
            <Users size={16} />
            <span>
              Tổng cộng <strong>{items.length}</strong> hành khách
            </span>
          </div>

          <Row className="g-3">
            {items.map((passenger, index) => (
              <Col key={getPassengerId(passenger, index)} xs={12} md={6} xl={4}>
                <PassengerCard passenger={passenger} index={index} />
              </Col>
            ))}
          </Row>
        </>
      )}
    </section>
  );
}
