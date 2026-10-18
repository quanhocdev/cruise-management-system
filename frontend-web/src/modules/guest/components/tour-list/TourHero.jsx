import { Container } from "react-bootstrap";
import "./TourHero.css";

export default function TourHero({ totalCount }) {
  return (
    <section className="tour-hero">
      <Container>
        <h1 className="tour-hero__title">Khám phá các hành trình du thuyền</h1>
        <p className="tour-hero__sub">
          Chọn chuyến hải trình phù hợp, xem lịch trình và đặt vé trực tuyến.
        </p>
        {totalCount != null && (
          <p className="tour-hero__count">
            Hiện có <strong>{totalCount}</strong> hành trình
          </p>
        )}
      </Container>
    </section>
  );
}
