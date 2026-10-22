import { Link } from "react-router-dom";
import { TRIP_STATUS, BOOKING_STATUS } from "./constants";
import { formatDate, formatDateTime, formatCurrency } from "./formatters";
import "./TourCard.css";

// Hiện đúng mốc thời gian mà người xem quan tâm theo trạng thái vé
const getBookingDeadline = (tour) => {
  switch (tour.statusBooking) {
    case "OPEN":
      return { label: "Hạn đặt vé", value: tour.bookingEnd, urgent: true };
    case "WAITING":
    case "NOT_OPEN":
      return { label: "Mở bán từ", value: tour.bookingStart, urgent: false };
    default:
      return { label: "Đã đóng lúc", value: tour.bookingEnd, urgent: false };
  }
};

export default function TourCard({ tour }) {
  const trip = TRIP_STATUS[tour.statusTrip] || {
    label: tour.statusTrip,
    tone: "done",
  };
  const booking = BOOKING_STATUS[tour.statusBooking] || {
    label: tour.statusBooking,
    tone: "closed",
  };
  const deadline = getBookingDeadline(tour);

  return (
    <article className="tour-card">
      <div className="tour-card__media">
        {tour.cruiseImageUrl ? (
          <img
            src={tour.cruiseImageUrl}
            alt={tour.cruiseName || "Du thuyền"}
            loading="lazy"
          />
        ) : (
          <div className="tour-card__placeholder">Chưa có hình ảnh</div>
        )}
        <span className={`tour-card__badge is-${booking.tone}`}>
          {booking.label}
        </span>
      </div>

      <div className="tour-card__body">
        <div className="tour-card__meta">
          <span className="tour-card__code">Mã tour {tour.code}</span>
          <span className={`tour-card__trip is-${trip.tone}`}>
            {trip.label}
          </span>
        </div>

        <h2 className="tour-card__title">
          <Link to={`/tours/${tour.id}`} className="tour-card__link">
            {tour.name}
          </Link>
        </h2>

        <p className="tour-card__desc">
          {tour.description || "Chưa có mô tả chi tiết cho hành trình này."}
        </p>

        <dl className="tour-card__info">
          <div>
            <dt>Khởi hành</dt>
            <dd>
              {formatDate(tour.startDate)} – {formatDate(tour.endDate)}
            </dd>
          </div>
          <div>
            <dt>{deadline.label}</dt>
            <dd className={deadline.urgent ? "is-urgent" : ""}>
              {formatDateTime(deadline.value)}
            </dd>
          </div>
          <div>
            <dt>Du thuyền</dt>
            <dd>{tour.cruiseName || "N/A"}</dd>
          </div>
        </dl>

        <div className="tour-card__footer">
          <div className="tour-card__price">
            <span>Giá từ</span>
            <strong>
              {tour.startingPrice
                ? formatCurrency(tour.startingPrice)
                : "Liên hệ"}
            </strong>
          </div>
          <span className="tour-card__cta" aria-hidden="true">
            Xem chi tiết
          </span>
        </div>
      </div>
    </article>
  );
}
