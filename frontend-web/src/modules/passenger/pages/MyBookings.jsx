import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowLeft, CalendarDays, CreditCard, Ship, Users } from "lucide-react";
import passengerCatalogService from "../services/passengerCatalogService";
import "../styles/PassengerCatalog.css";

const labels = {
  PENDING_PAYMENT: "Chờ thanh toán",
  CONFIRMED: "Đã xác nhận",
  CANCELLED: "Đã hủy",
};

const formatMoney = (value) => new Intl.NumberFormat("vi-VN", {
  style: "currency", currency: "VND", maximumFractionDigits: 0,
}).format(value || 0);

const formatDateTime = (value) => value
  ? new Intl.DateTimeFormat("vi-VN", { dateStyle: "medium", timeStyle: "short" }).format(new Date(value))
  : "—";

export default function MyBookings() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    passengerCatalogService.getMyBookings()
      .then(setBookings)
      .catch((requestError) => setError(requestError.response?.data?.message || "Không thể tải booking của bạn."))
      .finally(() => setLoading(false));
  }, []);

  return (
    <main className="passenger-page my-bookings-page">
      <header className="booking-form-header">
        <Link to="/passenger/dashboard" className="back-link static-back"><ArrowLeft size={18} /> Danh sách tour</Link>
        <span className="eyebrow">HÀNH TRÌNH CỦA TÔI</span>
        <h1>Booking của tôi</h1>
      </header>
      <section className="my-bookings-content">
        {loading && <div className="catalog-state">Đang tải danh sách booking...</div>}
        {error && <div className="catalog-state error-state">{error}</div>}
        {!loading && !error && bookings.length === 0 && (
          <div className="catalog-state empty-bookings"><Ship size={44} /><h2>Bạn chưa có booking nào</h2><Link className="primary-link" to="/passenger/dashboard">Khám phá tour</Link></div>
        )}
        <div className="booking-list">
          {bookings.map((booking) => (
            <article className="booking-list-card" key={booking.id}>
              <div className="booking-card-top">
                <div><small>MÃ BOOKING</small><strong>{booking.bookingCode || `#${booking.id}`}</strong></div>
                <span className={`booking-status ${booking.status.toLowerCase()}`}>{labels[booking.status] || booking.status}</span>
              </div>
              <div className="booking-card-facts">
                <span><CalendarDays size={18} /><span><small>Ngày tạo</small>{formatDateTime(booking.createdAt)}</span></span>
                <span><Users size={18} /><span><small>Hành khách</small>{booking.passengers?.length || 0} người</span></span>
                <span><CreditCard size={18} /><span><small>Tổng tiền</small>{formatMoney(booking.totalAmount)}</span></span>
              </div>
              <Link className="booking-detail-link" to={`/passenger/bookings/${booking.id}`}>Xem chi tiết</Link>
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}
