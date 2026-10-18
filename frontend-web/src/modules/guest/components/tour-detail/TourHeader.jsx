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
      {tour.description && (
        <p className="tour-header__desc">{tour.description}</p>
      )}
    </header>
  );
}
