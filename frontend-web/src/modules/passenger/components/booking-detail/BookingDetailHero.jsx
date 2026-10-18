import { Link } from "react-router-dom";
import { Container } from "react-bootstrap";
import { ArrowLeft } from "lucide-react";
import { BOOKING_STATUS } from "../my-bookings/constants";
import { formatDateTime } from "../my-bookings/helpers";
import "./BookingDetailHero.css";

export default function BookingDetailHero({ booking }) {
  const status = BOOKING_STATUS[booking.status] || {
    label: booking.status,
    tone: "cancelled",
  };

  return (
    <section className="bd-hero">
      <Container>
        <Link to="/passenger/bookings" className="bd-hero__back">
          <ArrowLeft size={16} /> Quay lại đặt chỗ của tôi
        </Link>

        <div className="bd-hero__row">
          <h1 className="bd-hero__title">
            Đơn {booking.bookingCode || `#${booking.id}`}
          </h1>
          <span className={`bd-hero__status is-${status.tone}`}>
            {status.label}
          </span>
        </div>

        <p className="bd-hero__sub">
          Tạo lúc {formatDateTime(booking.createdAt)}
        </p>
      </Container>
    </section>
  );
}
