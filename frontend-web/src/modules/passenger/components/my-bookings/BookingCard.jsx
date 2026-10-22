import { Link } from "react-router-dom";
import { BOOKING_STATUS, TRIP_STATUS } from "./constants";
import {
  formatMoney,
  formatDateTime,
  getTripStatus,
  getTourName,
} from "./helpers";
import "./BookingCard.css";

export default function BookingCard({ booking }) {
  const status = BOOKING_STATUS[booking.status] || {
    label: booking.status,
    tone: "cancelled",
  };
  const tripCode = getTripStatus(booking);
  const tripLabel = tripCode ? TRIP_STATUS[tripCode] || tripCode : null;

  return (
    <article className={`my-booking-card is-${status.tone}`}>
      <div className="my-booking-card__top">
        <span className="my-booking-card__code">
          Mã đơn {booking.bookingCode || `#${booking.id}`}
        </span>
        <span className={`my-booking-card__status is-${status.tone}`}>
          {status.label}
        </span>
      </div>

      <h2 className="my-booking-card__title">
        <Link
          to={`/passenger/bookings/${booking.id}`}
          className="my-booking-card__link"
        >
          {getTourName(booking)}
        </Link>
      </h2>

      <dl className="my-booking-card__info">
        {tripLabel && (
          <div>
            <dt>Chuyến đi</dt>
            <dd>{tripLabel}</dd>
          </div>
        )}
        <div>
          <dt>Ngày tạo đơn</dt>
          <dd>{formatDateTime(booking.createdAt)}</dd>
        </div>
        <div>
          <dt>Hành khách</dt>
          <dd>{booking.passengers?.length || 0} người</dd>
        </div>
      </dl>

      <div className="my-booking-card__footer">
        <div className="my-booking-card__total">
          <span>Tổng tiền</span>
          <strong>{formatMoney(booking.totalAmount)}</strong>
        </div>
        <span className="my-booking-card__cta" aria-hidden="true">
          Xem chi tiết
        </span>
      </div>
    </article>
  );
}
