import { Button } from "react-bootstrap";
import { formatDate, formatDateTime, formatCurrency } from "./formatters";
import "./TourBookingCard.css";

export default function TourBookingCard({ tour, onBook }) {
  const isOpen = tour.statusBooking === "OPEN";

  const prices = (tour.packages || [])
    .map((p) => Number(p.price))
    .filter((n) => !isNaN(n));
  const minPrice = prices.length ? Math.min(...prices) : null;

  return (
    <section className="booking-card">
      {minPrice !== null && (
        <div className="booking-card__price">
          <span className="booking-card__price-label">Giá từ</span>
          <span className="booking-card__price-value">
            {formatCurrency(minPrice)}
          </span>
        </div>
      )}

      <dl className="booking-card__list">
        <div className="booking-card__row">
          <dt>Khởi hành</dt>
          <dd>{formatDate(tour.startDate)}</dd>
        </div>
        <div className="booking-card__row">
          <dt>Kết thúc</dt>
          <dd>{formatDate(tour.endDate)}</dd>
        </div>
        <div className="booking-card__row">
          <dt>Mở bán vé</dt>
          <dd>{formatDateTime(tour.bookingStart)}</dd>
        </div>
        <div className="booking-card__row">
          <dt>Đóng bán vé</dt>
          <dd className="booking-card__deadline">
            {formatDateTime(tour.bookingEnd)}
          </dd>
        </div>
      </dl>

      <Button className="booking-card__btn" onClick={onBook} disabled={!isOpen}>
        {isOpen ? "Đặt tour ngay" : "Tạm đóng vé"}
      </Button>
    </section>
  );
}
