import { useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { ArrowLeft, BedDouble, CalendarDays, CheckCircle2, Ship, UserPlus } from "lucide-react";
import passengerCatalogService from "../services/passengerCatalogService";
import "../styles/PassengerCatalog.css";

const emptyPassenger = () => ({
  fullName: "",
  dateOfBirth: "",
  gender: "",
  phoneNumber: "",
  email: "",
});

const formatMoney = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(value || 0);

const getErrorMessage = (error) =>
  error.response?.data?.message ||
  error.response?.data?.detail ||
  "Không thể tạo booking. Vui lòng kiểm tra lại thông tin.";

export default function CreateBooking() {
  const [searchParams] = useSearchParams();
  const tourId = searchParams.get("tourId");
  const voyageId = searchParams.get("voyageId");
  const roomId = searchParams.get("roomId");
  const [tour, setTour] = useState(null);
  const [room, setRoom] = useState(null);
  const [contact, setContact] = useState({ name: "", phone: "" });
  const [passengers, setPassengers] = useState([emptyPassenger()]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [booking, setBooking] = useState(null);

  useEffect(() => {
    const loadSelection = async () => {
      if (!tourId || !voyageId || !roomId) {
        setError("Thông tin tour hoặc phòng đã chọn không hợp lệ.");
        setLoading(false);
        return;
      }
      try {
        const [tourData, rooms] = await Promise.all([
          passengerCatalogService.getTourDetail(tourId),
          passengerCatalogService.getAvailableRooms(voyageId),
        ]);
        const selectedRoom = rooms.find((item) => item.roomId === roomId && item.available);
        if (!selectedRoom) throw new Error("ROOM_NOT_AVAILABLE");
        setTour(tourData);
        setRoom(selectedRoom);
      } catch (requestError) {
        setError(
          requestError.message === "ROOM_NOT_AVAILABLE"
            ? "Phòng này không còn trống. Vui lòng chọn phòng khác."
            : getErrorMessage(requestError),
        );
      } finally {
        setLoading(false);
      }
    };
    loadSelection();
  }, [roomId, tourId, voyageId]);

  const canAddPassenger = passengers.length < (room?.remainingCapacity || 1);
  const maxBirthDate = useMemo(() => {
    const date = new Date();
    date.setDate(date.getDate() - 1);
    return date.toISOString().slice(0, 10);
  }, []);

  const updatePassenger = (index, field, value) => {
    setPassengers((current) => current.map((item, itemIndex) =>
      itemIndex === index ? { ...item, [field]: value } : item,
    ));
  };

  const submitBooking = async (event) => {
    event.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const result = await passengerCatalogService.createBooking({
        voyageId,
        primaryContactName: contact.name.trim(),
        primaryContactPhone: contact.phone.trim(),
        passengers: passengers.map((passenger) => ({
          userId: null,
          fullName: passenger.fullName.trim(),
          dateOfBirth: passenger.dateOfBirth,
          gender: passenger.gender,
          phoneNumber: passenger.phoneNumber.trim() || null,
          email: passenger.email.trim() || null,
          cabinId: roomId,
        })),
      });
      setBooking(result);
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="catalog-state full-page">Đang kiểm tra phòng đã chọn...</div>;
  if (error && !room) {
    return <div className="catalog-state full-page error-state"><p>{error}</p><Link to={`/passenger/tours/${tourId || ""}`}>Chọn lại phòng</Link></div>;
  }

  if (booking) {
    return (
      <main className="passenger-page booking-result-page">
        <section className="booking-result-card">
          <CheckCircle2 size={62} />
          <span className="eyebrow">TẠO BOOKING THÀNH CÔNG</span>
          <h1>Giữ chỗ thành công</h1>
          <p>Booking <strong>#{booking.id}</strong> đang chờ thanh toán.</p>
          <div className="result-total"><small>Tổng thanh toán</small><strong>{formatMoney(booking.totalAmount)}</strong></div>
          <div className="result-actions">
            <button type="button" className="booking-button">Thanh toán VNPay</button>
            <Link to="/passenger/dashboard">Về danh sách tour</Link>
          </div>
          <small>Nút VNPay sẽ được nối ở bước tiếp theo.</small>
        </section>
      </main>
    );
  }

  return (
    <main className="passenger-page create-booking-page">
      <header className="booking-form-header">
        <Link to={`/passenger/tours/${tourId}`} className="back-link static-back"><ArrowLeft size={18} /> Chọn lại phòng</Link>
        <div><span className="eyebrow">HOÀN TẤT THÔNG TIN</span><h1>Đặt chỗ hành trình</h1></div>
      </header>

      <form className="booking-form-layout" onSubmit={submitBooking}>
        <div className="booking-form-main">
          <section className="detail-panel form-section">
            <h2>Người liên hệ</h2>
            <div className="form-grid">
              <label>Họ và tên *<input required maxLength="150" value={contact.name} onChange={(e) => setContact({ ...contact, name: e.target.value })} /></label>
              <label>Số điện thoại *<input required maxLength="30" pattern="[0-9+ ]{8,15}" value={contact.phone} onChange={(e) => setContact({ ...contact, phone: e.target.value })} /></label>
            </div>
          </section>

          <section className="detail-panel form-section">
            <div className="form-section-title">
              <div><h2>Thông tin hành khách</h2><p>Tối đa {room.remainingCapacity} hành khách cho phòng này.</p></div>
              <button type="button" className="add-passenger" disabled={!canAddPassenger} onClick={() => setPassengers([...passengers, emptyPassenger()])}><UserPlus size={17} /> Thêm hành khách</button>
            </div>
            {passengers.map((passenger, index) => (
              <fieldset className="passenger-fields" key={index}>
                <legend>Hành khách {index + 1}</legend>
                {passengers.length > 1 && <button type="button" className="remove-passenger" onClick={() => setPassengers(passengers.filter((_, itemIndex) => itemIndex !== index))}>Xóa</button>}
                <div className="form-grid three-columns">
                  <label>Họ và tên *<input required maxLength="150" value={passenger.fullName} onChange={(e) => updatePassenger(index, "fullName", e.target.value)} /></label>
                  <label>Ngày sinh *<input required type="date" max={maxBirthDate} value={passenger.dateOfBirth} onChange={(e) => updatePassenger(index, "dateOfBirth", e.target.value)} /></label>
                  <label>Giới tính *<select required value={passenger.gender} onChange={(e) => updatePassenger(index, "gender", e.target.value)}><option value="">Chọn giới tính</option><option value="MALE">Nam</option><option value="FEMALE">Nữ</option><option value="OTHER">Khác</option></select></label>
                  <label>Số điện thoại<input maxLength="30" value={passenger.phoneNumber} onChange={(e) => updatePassenger(index, "phoneNumber", e.target.value)} /></label>
                  <label>Email<input type="email" maxLength="255" value={passenger.email} onChange={(e) => updatePassenger(index, "email", e.target.value)} /></label>
                </div>
              </fieldset>
            ))}
          </section>
          {error && <div className="catalog-state error-state">{error}</div>}
        </div>

        <aside className="booking-summary booking-form-summary">
          <span className="eyebrow">TÓM TẮT ĐẶT CHỖ</span>
          <h2>{tour.name}</h2>
          <div className="summary-row"><Ship size={18} /> {tour.cruiseName}</div>
          <div className="summary-row"><CalendarDays size={18} /> {tour.startDate}</div>
          <div className="summary-divider" />
          <div className="selected-room-summary"><BedDouble /><span><strong>{room.roomTypeName}</strong><small>Phòng {room.roomCode} · Tầng {room.deckNumber}</small></span></div>
          <div className="summary-count"><span>Số hành khách</span><strong>{passengers.length}</strong></div>
          <div className="summary-total"><span>Tổng tiền phòng</span><strong>{formatMoney(room.price)}</strong></div>
          <button type="submit" className="booking-button" disabled={submitting}>{submitting ? "Đang tạo booking..." : "Xác nhận giữ chỗ"}</button>
          <small>Giá chính thức được Booking Service tính lại từ dữ liệu phòng.</small>
        </aside>
      </form>
    </main>
  );
}
