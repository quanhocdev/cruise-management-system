import { Link } from "react-router-dom";
import { Container } from "react-bootstrap";
import { ArrowLeft } from "lucide-react";
import "./MyBookingsHero.css";

export default function MyBookingsHero({ totalCount }) {
  return (
    <section className="my-bookings-hero">
      <Container>
        <Link to="/passenger/dashboard" className="my-bookings-hero__back">
          <ArrowLeft size={16} /> Quay lại danh sách tour
        </Link>

        <h1 className="my-bookings-hero__title">Đặt chỗ của tôi</h1>
        <p className="my-bookings-hero__sub">
          Theo dõi các đơn đặt vé và quản lý hành khách cho từng chuyến đi.
        </p>

        {totalCount != null && (
          <p className="my-bookings-hero__count">
            Bạn có <strong>{totalCount}</strong> đơn đặt vé
          </p>
        )}
      </Container>
    </section>
  );
}
