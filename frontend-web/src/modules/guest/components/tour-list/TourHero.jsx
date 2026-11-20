import { Container } from "react-bootstrap";
import TourSearchBar from "./TourSearchBar";
import "./TourHero.css";

export default function TourHero({ totalCount, onSearch }) {
  return (
    <section className="tour-hero-wrapper">
      <div className="tour-hero">
        <div className="tour-hero__overlay" />
        <Container className="tour-hero__content">
          <div className="tour-hero__eyebrow">
            Tour du lịch trong nước - Quốc tế
          </div>

          <h1 className="tour-hero__title">Hành trình du thuyền</h1>


          <div className="tour-hero__highlights">
            Du thuyền hiện đại - Lịch trình rõ ràng - Hỗ trợ tận tâm
          </div>

        </Container>
      </div>

      <Container className="tour-hero__search-container">
        <TourSearchBar onSearch={onSearch} />
      </Container>
    </section>
  );
}