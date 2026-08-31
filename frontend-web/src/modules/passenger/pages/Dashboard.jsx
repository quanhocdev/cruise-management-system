// passenger/pages/Dashboard.jsx
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { CalendarDays, ClipboardList, LogOut, Search, Ship, Users } from "lucide-react";
import { useAuth } from "../../../context/AuthContext";
import passengerCatalogService from "../services/passengerCatalogService";
import "../styles/PassengerCatalog.css";

const formatDate = (value) =>
  value
    ? new Intl.DateTimeFormat("vi-VN", { dateStyle: "long" }).format(
        new Date(`${value}T00:00:00`),
      )
    : "Đang cập nhật";

export default function PassengerDashboard() {
  const { user, logout } = useAuth();
  const [tours, setTours] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadTours = async () => {
      try {
        setTours(await passengerCatalogService.getOpenTours());
      } catch (requestError) {
        setError(
          requestError.response?.data?.message ||
            "Không thể tải danh sách tour. Vui lòng thử lại.",
        );
      } finally {
        setLoading(false);
      }
    };

    loadTours();
  }, []);

  const visibleTours = tours.filter((tour) => {
    const text = `${tour.name} ${tour.code} ${tour.cruiseName}`.toLowerCase();
    return text.includes(keyword.trim().toLowerCase());
  });

  return (
    <main className="passenger-page">
      <header className="passenger-header">
        <Link className="passenger-brand" to="/passenger/dashboard">
          <span className="brand-mark"><Ship size={25} /></span>
          <span>Blue Horizon</span>
        </Link>
        <div className="passenger-account">
          <Link to="/passenger/bookings" className="ghost-button account-link">
            <ClipboardList size={17} /> Booking của tôi
          </Link>
          <span>Xin chào, <strong>{user?.username}</strong></span>
          <button type="button" onClick={logout} className="ghost-button">
            <LogOut size={17} /> Đăng xuất
          </button>
        </div>
      </header>

      <section className="catalog-hero">
        <div>
          <span className="eyebrow">HÀNH TRÌNH ĐÁNG NHỚ</span>
          <h1>Khám phá đại dương<br />theo cách của bạn</h1>
          <p>Chọn chuyến đi, căn phòng phù hợp và sẵn sàng tận hưởng kỳ nghỉ trên biển.</p>
        </div>
        <div className="hero-stat">
          <Users size={28} />
          <strong>{tours.length}</strong>
          <span>tour đang mở bán</span>
        </div>
      </section>

      <section className="catalog-content">
        <div className="catalog-heading">
          <div>
            <span className="eyebrow">CHỌN HÀNH TRÌNH</span>
            <h2>Tour đang mở bán</h2>
          </div>
          <label className="tour-search">
            <Search size={19} />
            <input
              value={keyword}
              onChange={(event) => setKeyword(event.target.value)}
              placeholder="Tìm tour hoặc du thuyền..."
            />
          </label>
        </div>

        {loading && <div className="catalog-state">Đang tải các hành trình...</div>}
        {error && <div className="catalog-state error-state">{error}</div>}
        {!loading && !error && visibleTours.length === 0 && (
          <div className="catalog-state">Chưa tìm thấy tour phù hợp.</div>
        )}

        <div className="tour-grid">
          {visibleTours.map((tour) => (
            <article className="tour-card" key={tour.id}>
              <div className="tour-image-wrap">
                {tour.cruiseImageUrl ? (
                  <img src={tour.cruiseImageUrl} alt={tour.cruiseName} />
                ) : (
                  <div className="tour-image-placeholder"><Ship size={52} /></div>
                )}
                <span className="tour-code">{tour.code}</span>
              </div>
              <div className="tour-card-body">
                <p className="cruise-name"><Ship size={16} /> {tour.cruiseName}</p>
                <h3>{tour.name}</h3>
                <p className="tour-description">
                  {tour.description || "Một hành trình nghỉ dưỡng đáng nhớ đang chờ bạn."}
                </p>
                <div className="tour-dates">
                  <CalendarDays size={18} />
                  <span>{formatDate(tour.startDate)} — {formatDate(tour.endDate)}</span>
                </div>
                <Link className="primary-link" to={`/passenger/tours/${tour.id}`}>
                  Xem hành trình
                </Link>
              </div>
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}
