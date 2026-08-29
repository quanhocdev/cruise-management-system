import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  ArrowLeft,
  BedDouble,
  CalendarDays,
  CheckCircle2,
  MapPin,
  Ship,
  Users,
} from "lucide-react";
import passengerCatalogService from "../services/passengerCatalogService";
import "../styles/PassengerCatalog.css";

const formatDate = (value) =>
  value
    ? new Intl.DateTimeFormat("vi-VN", { dateStyle: "long" }).format(
        new Date(`${value}T00:00:00`),
      )
    : "Đang cập nhật";

const formatMoney = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(value || 0);

export default function TourDetail() {
  const { tourId } = useParams();
  const [tour, setTour] = useState(null);
  const [departures, setDepartures] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [selectedRoomId, setSelectedRoomId] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadDetail = async () => {
      try {
        const [tourData, departureData] = await Promise.all([
          passengerCatalogService.getTourDetail(tourId),
          passengerCatalogService.getDepartures(tourId),
        ]);
        setTour(tourData);
        setDepartures(departureData);

        if (departureData.length > 0) {
          setRooms(
            await passengerCatalogService.getAvailableRooms(
              departureData[0].voyageId,
            ),
          );
        }
      } catch (requestError) {
        setError(
          requestError.response?.data?.message ||
            "Không thể tải thông tin hành trình.",
        );
      } finally {
        setLoading(false);
      }
    };

    loadDetail();
  }, [tourId]);

  if (loading) return <div className="catalog-state full-page">Đang tải hành trình...</div>;
  if (error || !tour) {
    return (
      <div className="catalog-state full-page error-state">
        <p>{error || "Không tìm thấy tour."}</p>
        <Link to="/passenger/dashboard">Quay lại danh sách</Link>
      </div>
    );
  }

  const departure = departures[0];
  const selectedRoom = rooms.find((room) => room.roomId === selectedRoomId);

  return (
    <main className="passenger-page detail-page">
      <section className="detail-hero">
        {tour.cruiseImageUrl && <img src={tour.cruiseImageUrl} alt={tour.cruiseName} />}
        <div className="detail-overlay" />
        <div className="detail-hero-content">
          <Link className="back-link" to="/passenger/dashboard"><ArrowLeft size={18} /> Tất cả tour</Link>
          <span className="eyebrow">{tour.code}</span>
          <h1>{tour.name}</h1>
          <p><Ship size={19} /> {tour.cruiseName}</p>
        </div>
      </section>

      <section className="detail-content">
        <div className="detail-main">
          <article className="detail-panel">
            <h2>Thông tin hành trình</h2>
            <p>{tour.description || tour.cruiseDescription || "Thông tin đang được cập nhật."}</p>
            <div className="detail-facts">
              <div><CalendarDays /><span><small>Khởi hành</small>{formatDate(tour.startDate)}</span></div>
              <div><CalendarDays /><span><small>Kết thúc</small>{formatDate(tour.endDate)}</span></div>
              <div><Users /><span><small>Sức chứa tàu</small>{tour.maxPassengers} hành khách</span></div>
            </div>
          </article>

          {tour.itinerary?.length > 0 && (
            <article className="detail-panel">
              <h2>Lịch trình</h2>
              <div className="itinerary-list">
                {tour.itinerary.map((day) => (
                  <div className="itinerary-item" key={day.id}>
                    <span className="day-number">Ngày {day.dayNumber}</span>
                    <div><h3>{day.name}</h3><p>{day.description}</p></div>
                  </div>
                ))}
              </div>
            </article>
          )}

          <article className="detail-panel">
            <h2>Chọn phòng</h2>
            <p className="section-note">Giá hiển thị là giá của loại phòng cho chuyến đã chọn.</p>
            <div className="room-list">
              {rooms.filter((room) => room.available).map((room) => (
                <button
                  type="button"
                  key={room.roomId}
                  onClick={() => setSelectedRoomId(room.roomId)}
                  className={`room-option ${selectedRoomId === room.roomId ? "selected" : ""}`}
                >
                  <span className="room-icon"><BedDouble /></span>
                  <span className="room-copy">
                    <strong>{room.roomTypeName} · Phòng {room.roomCode}</strong>
                    <small>Tầng {room.deckNumber} · Còn {room.remainingCapacity}/{room.capacity} chỗ</small>
                  </span>
                  <span className="room-price">{formatMoney(room.price)}</span>
                  {selectedRoomId === room.roomId && <CheckCircle2 className="selected-check" />}
                </button>
              ))}
              {rooms.filter((room) => room.available).length === 0 && (
                <div className="catalog-state">Chuyến này hiện không còn phòng trống.</div>
              )}
            </div>
          </article>
        </div>

        <aside className="booking-summary">
          <span className="eyebrow">CHUYẾN ĐÃ CHỌN</span>
          <h2>{tour.name}</h2>
          <div className="summary-row"><MapPin size={18} /> {tour.cruiseName}</div>
          <div className="summary-row"><CalendarDays size={18} /> {formatDate(departure?.departureDate)}</div>
          <div className="summary-divider" />
          <p>{selectedRoomId ? "Phòng đã được chọn. Bạn có thể sang bước tạo booking." : "Vui lòng chọn một phòng còn trống."}</p>
          {selectedRoom && departure ? (
            <Link
              className="booking-button"
              to={`/passenger/bookings/new?tourId=${tour.id}&voyageId=${departure.voyageId}&roomId=${selectedRoom.roomId}`}
            >
              Tiếp tục đặt tour
            </Link>
          ) : (
            <button className="booking-button" disabled type="button">Tiếp tục đặt tour</button>
          )}
          <small>Thông tin và giá phòng sẽ được xác nhận lại trước khi tạo booking.</small>
        </aside>
      </section>
    </main>
  );
}
