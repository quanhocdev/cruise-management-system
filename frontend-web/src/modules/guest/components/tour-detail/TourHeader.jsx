import { Link } from "react-router-dom";
import "./TourHeader.css";

export default function TourHeader({ tour }) {
  const isOpen = tour.statusBooking === "OPEN";

  return (
    <header className="tour-header">
      <Link to="/tours" className="tour-header__back">
        ← Trở lại danh sách tour
      </Link>

      <div className="tour-header__meta">
        <span className="tour-header__code">Mã tour {tour.code}</span>
        <span
          className={`tour-header__status ${isOpen ? "is-open" : "is-closed"}`}
        >
          {isOpen ? "Đang mở booking" : "Tạm đóng"}
        </span>
      </div>

      <h1 className="tour-header__title">{tour.name}</h1>

      {/* --- BỔ SUNG: Hiển thị thông tin cảng đi và cảng đến ở phần Header --- */}
      {(tour.departurePort || tour.arrivalPort) && (
        <div className="tour-header__ports">
          {tour.departurePort && (
            <div className="tour-port-badge departure">
              <span className="label">📍 Điểm check-in khởi hành:</span>
              <strong>{tour.departurePort.name}</strong> (
              {tour.departurePort.city}, {tour.departurePort.country})
              {tour.departurePort.address && (
                <span className="address"> - {tour.departurePort.address}</span>
              )}
            </div>
          )}
          {tour.arrivalPort && (
            <div className="tour-port-badge arrival">
              <span className="label">🏁 Cảng kết thúc:</span>
              <strong>{tour.arrivalPort.name}</strong> ({tour.arrivalPort.city},{" "}
              {tour.arrivalPort.country})
            </div>
          )}
        </div>
      )}

      {tour.description && (
        <p className="tour-header__desc">{tour.description}</p>
      )}
    </header>
  );
}
