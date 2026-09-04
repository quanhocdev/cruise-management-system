import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ArrowLeft, BedDouble, CalendarDays, CreditCard, UserRound } from "lucide-react";
import passengerCatalogService from "../services/passengerCatalogService";
import paymentService from "../../payment/services/paymentService";
import "../styles/PassengerCatalog.css";

const labels = { PENDING_PAYMENT: "Chờ thanh toán", CONFIRMED: "Đã xác nhận", CANCELLED: "Đã hủy" };
const formatMoney = (value) => new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND", maximumFractionDigits: 0 }).format(value || 0);
const getError = (error, fallback) => error.response?.data?.message || error.response?.data?.detail || fallback;

export default function BookingDetail() {
  const { bookingId } = useParams();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [action, setAction] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    passengerCatalogService.getBooking(bookingId)
      .then(setBooking)
      .catch((requestError) => setError(getError(requestError, "Không thể tải chi tiết booking.")))
      .finally(() => setLoading(false));
  }, [bookingId]);

  const pay = async () => {
    setAction("pay"); setError("");
    try {
      const payment = await paymentService.createVnPayPayment(booking);
      if (!payment.paymentUrl) throw new Error("PAYMENT_URL_MISSING");
      window.location.assign(payment.paymentUrl);
    } catch (requestError) {
      setError(getError(requestError, "Không thể bắt đầu thanh toán VNPay.")); setAction("");
    }
  };

  const cancel = async () => {
    if (!window.confirm("Ông chắc chắn muốn hủy booking này chứ?")) return;
    setAction("cancel"); setError("");
    try { setBooking(await passengerCatalogService.cancelBooking(booking.id)); }
    catch (requestError) { setError(getError(requestError, "Không thể hủy booking.")); }
    finally { setAction(""); }
  };

  if (loading) return <div className="catalog-state full-page">Đang tải chi tiết booking...</div>;
  if (!booking) return <div className="catalog-state full-page error-state"><p>{error}</p><Link to="/passenger/bookings">Quay lại</Link></div>;

  return (
    <main className="passenger-page booking-detail-page">
      <header className="booking-form-header">
        <Link to="/passenger/bookings" className="back-link static-back"><ArrowLeft size={18} /> Booking của tôi</Link>
        <span className="eyebrow">CHI TIẾT BOOKING</span>
        <h1>{booking.bookingCode || `Booking #${booking.id}`}</h1>
      </header>
      <section className="booking-detail-content">
        <div className="booking-detail-main">
          <section className="detail-panel booking-overview">
            <div><small>Trạng thái</small><span className={`booking-status ${booking.status.toLowerCase()}`}>{labels[booking.status] || booking.status}</span></div>
            <div><small>Tổng tiền</small><strong>{formatMoney(booking.totalAmount)}</strong></div>
            <div><small>Người liên hệ</small><strong>{booking.primaryContactName}</strong><span>{booking.primaryContactPhone}</span></div>
          </section>
          <section className="detail-panel">
            <h2>Thông tin hành khách</h2>
            <div className="passenger-detail-list">
              {booking.passengers?.map((passenger) => (
                <article key={passenger.passengerVoyageId}>
                  <span className="passenger-number"><UserRound size={20} /></span>
                  <div><strong>{passenger.fullName}</strong><small>{passenger.gender} · {passenger.dateOfBirth}</small><small>{passenger.phoneNumber || "Chưa có SĐT"} · {passenger.email || "Chưa có email"}</small></div>
                  <div className="passenger-room"><BedDouble size={18} /><span>Phòng</span><strong>{String(passenger.cabinId).slice(0, 8)}...</strong></div>
                </article>
              ))}
            </div>
          </section>
        </div>
        <aside className="booking-summary booking-actions-panel">
          <CalendarDays size={25} />
          <h2>Booking #{booking.id}</h2>
          <p>Mã chuyến: <strong>{String(booking.voyageId).slice(0, 13)}...</strong></p>
          {booking.paymentId && <p><CreditCard size={16} /> Thanh toán #{booking.paymentId}</p>}
          {booking.status === "PENDING_PAYMENT" && <>
            <button className="booking-button" type="button" disabled={Boolean(action)} onClick={pay}>{action === "pay" ? "Đang chuyển đến VNPay..." : "Thanh toán VNPay"}</button>
            <button className="cancel-booking-button" type="button" disabled={Boolean(action)} onClick={cancel}>{action === "cancel" ? "Đang hủy..." : "Hủy booking"}</button>
          </>}
          {booking.status === "CONFIRMED" && <div className="booking-confirmed-note">Booking đã được thanh toán và xác nhận.</div>}
          {error && <p className="payment-error">{error}</p>}
        </aside>
      </section>
    </main>
  );
}
