// src/modules/guest/pages/TourPublic.jsx

import { useState } from "react";
import { Link } from "react-router-dom";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Badge,
  Spinner,
  Alert,
} from "react-bootstrap";
import { usePublicTours } from "../hooks/usePublicTours";
import "../styles/TourPublic.css";

export default function TourPublic() {
  const { tours, loading, error } = usePublicTours();
  const [filterStatus, setFilterStatus] = useState("ALL");
  const [filterBooking, setFilterBooking] = useState("ALL");

  const filteredTours = tours.filter((tour) => {
    const matchTrip =
      filterStatus === "ALL" || tour.statusTrip === filterStatus;
    const matchBooking =
      filterBooking === "ALL" || tour.statusBooking === filterBooking;
    return matchTrip && matchBooking;
  });

  return (
    <div className="tour-public-page bg-light min-vh-100 pb-5">
      {/* Banner đầu trang */}
      <div className="tour-public-hero text-white py-5 mb-4 shadow-sm text-center">
        <Container>
          <h1 className="fw-bold mb-2">🌊 Khám Phá Các Hành Trình Du Thuyền</h1>
          <p className="lead text-white-50 mb-0">
            Lựa chọn những chuyến hải trình đẳng cấp và tận hưởng kỳ nghỉ đáng
            nhớ cùng chúng tôi.
          </p>
        </Container>
      </div>

      <Container>
        {/* Bộ lọc trạng thái tour (Trạng thái chuyến đi) */}
        <div className="d-flex align-items-center justify-content-between flex-wrap gap-3 mb-3">
          <div className="tour-filter-buttons d-flex gap-2 overflow-x-auto pb-1">
            <Button
              variant={filterStatus === "ALL" ? "primary" : "outline-secondary"}
              size="sm"
              className="rounded-pill px-3 fw-medium flex-shrink-0"
              onClick={() => setFilterStatus("ALL")}
            >
              Tất cả ({tours.length})
            </Button>
            <Button
              variant={
                filterStatus === "READY" ? "success" : "outline-secondary"
              }
              size="sm"
              className="rounded-pill px-3 fw-medium flex-shrink-0"
              onClick={() => setFilterStatus("READY")}
            >
              Sắp diễn ra
            </Button>
            <Button
              variant={
                filterStatus === "IN_PROGRESS" ? "warning" : "outline-secondary"
              }
              size="sm"
              className="rounded-pill px-3 fw-medium text-dark flex-shrink-0"
              onClick={() => setFilterStatus("IN_PROGRESS")}
            >
              Đang diễn ra
            </Button>
            <Button
              variant={
                filterStatus === "COMPLETED" ? "secondary" : "outline-secondary"
              }
              size="sm"
              className="rounded-pill px-3 fw-medium flex-shrink-0"
              onClick={() => setFilterStatus("COMPLETED")}
            >
              Đã hoàn thành
            </Button>
          </div>

          <span className="text-muted small">
            Hiển thị {filteredTours.length} tour
          </span>
        </div>

        {/* Bộ lọc trạng thái mở bán Booking */}
        <div className="d-flex align-items-center gap-2 mb-4 overflow-x-auto pb-2 tour-booking-filter-wrapper">
          <span className="text-muted small fw-semibold me-2 flex-shrink-0">
            Trạng thái vé:
          </span>
          <div className="tour-booking-filter-buttons d-flex gap-2 overflow-x-auto pb-1">
            <Button
              variant={filterBooking === "ALL" ? "dark" : "outline-secondary"}
              size="sm"
              className="rounded-pill px-3 flex-shrink-0"
              onClick={() => setFilterBooking("ALL")}
            >
              Tất cả vé
            </Button>
            <Button
              variant={filterBooking === "OPEN" ? "success" : "outline-secondary"}
              size="sm"
              className="rounded-pill px-3 flex-shrink-0"
              onClick={() => setFilterBooking("OPEN")}
            >
              Đang mở bán (OPEN)
            </Button>
            <Button
              variant={filterBooking === "WAITING" ? "info" : "outline-secondary"}
              size="sm"
              className="rounded-pill px-3 text-dark flex-shrink-0"
              onClick={() => setFilterBooking("WAITING")}
            >
              Sắp mở (WAITING)
            </Button>
            <Button
              variant={
                filterBooking === "NOT_OPEN" ? "warning" : "outline-secondary"
              }
              size="sm"
              className="rounded-pill px-3 text-dark flex-shrink-0"
              onClick={() => setFilterBooking("NOT_OPEN")}
            >
              Chưa mở (NOT_OPEN)
            </Button>
            <Button
              variant={
                filterBooking === "CLOSED" ? "secondary" : "outline-secondary"
              }
              size="sm"
              className="rounded-pill px-3 flex-shrink-0"
              onClick={() => setFilterBooking("CLOSED")}
            >
              Đã đóng (CLOSED)
            </Button>
          </div>
        </div>

        {/* Trạng thái tải dữ liệu */}
        {loading && (
          <div className="text-center py-5">
            <Spinner animation="border" variant="primary" />
            <p className="mt-2 text-muted">Đang tải danh sách tour...</p>
          </div>
        )}

        {/* Thông báo lỗi */}
        {error && (
          <Alert variant="danger" className="my-3">
            {error}
          </Alert>
        )}

        {/* Khi không có dữ liệu */}
        {!loading && !error && filteredTours.length === 0 && (
          <Alert variant="info" className="text-center py-4">
            Không tìm thấy tour du lịch nào phù hợp với bộ lọc này.
          </Alert>
        )}

        {/* Lưới hiển thị danh sách Tour */}
        <Row xs={1} md={2} lg={3} className="g-4">
          {filteredTours.map((tour) => (
            <Col key={tour.id}>
              <Card className="tour-card h-100 shadow-sm border-0 rounded-4 overflow-hidden">
                <div className="tour-card-img-wrapper position-relative">
                  {tour.cruiseImageUrl ? (
                    <Card.Img
                      variant="top"
                      src={tour.cruiseImageUrl}
                      alt={tour.cruiseName || "Cruise"}
                      className="w-100 h-100 object-fit-cover"
                    />
                  ) : (
                    <div className="d-flex align-items-center justify-content-center h-100 bg-secondary text-white small">
                      Chưa có hình ảnh
                    </div>
                  )}
                  <div className="position-absolute top-0 end-0 m-3">
                    <Badge
                      bg={
                        tour.statusBooking === "OPEN" ? "success" : "secondary"
                      }
                      className="px-3 py-2 rounded-pill shadow-sm"
                    >
                      {tour.statusBooking === "OPEN"
                        ? "Đang mở Booking"
                        : "Tạm đóng"}
                    </Badge>
                  </div>
                </div>

                <Card.Body className="d-flex flex-column p-4">
                  <div className="text-muted small fw-semibold mb-1 d-flex justify-content-between align-items-center">
                    <span>MÃ TOUR: {tour.code}</span>
                    <span className="badge bg-light text-dark border">
                      {tour.statusTrip}
                    </span>
                  </div>
                  <Card.Title className="fw-bold text-dark fs-5 mb-2 tour-title-clamp">
                    {tour.name}
                  </Card.Title>

                  <p className="text-muted small flex-grow-1 tour-desc-clamp mb-3">
                    {tour.description ||
                      "Chưa có mô tả chi tiết cho hành trình này."}
                  </p>

                  <div className="tour-info-box bg-light p-3 rounded-3 mb-3 small text-secondary">
                    <div className="mb-1">
                      📅 <strong>Khởi hành:</strong> {tour.startDate}
                    </div>
                    <div>
                      🚢 <strong>Du thuyền:</strong> {tour.cruiseName || "N/A"}
                    </div>
                  </div>

                  <div className="d-flex align-items-center justify-content-between mt-auto pt-3 border-top">
                    <div>
                      <span className="small text-muted d-block">Giá từ</span>
                      <span className="fw-bold text-primary fs-5">
                        {tour.startingPrice
                          ? Number(tour.startingPrice).toLocaleString("vi-VN") +
                            " đ"
                          : "Liên hệ"}
                      </span>
                    </div>

                    <Button
                      as={Link}
                      to={`/tours/${tour.id}`}
                      variant="primary"
                      className="rounded-pill px-4"
                      size="sm"
                    >
                      Xem chi tiết
                    </Button>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      </Container>
    </div>
  );
}