// src/modules/guest/pages/TourPublicDetail.jsx

import { useParams, Link } from "react-router-dom";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Badge,
  Spinner,
  Alert,
  Tabs,
  Tab,
} from "react-bootstrap";
import { usePublicTourDetail } from "../hooks/usePublicTours";
import "../styles/TourPublicDetail.css";

export default function TourPublicDetail() {
  const { id } = useParams();
  const { tour, loading, error } = usePublicTourDetail(id);

  if (loading) {
    return (
      <div className="text-center py-5 min-vh-100 d-flex flex-column align-items-center justify-content-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-3 text-muted">
          Đang tải thông tin chi tiết hành trình...
        </p>
      </div>
    );
  }

  if (error) {
    return (
      <Container className="py-5">
        <Alert variant="danger">{error}</Alert>
        <Button as={Link} to="/tours" variant="outline-primary">
          Quay lại danh sách tour
        </Button>
      </Container>
    );
  }

  if (!tour) return null;

  return (
    <div className="tour-detail-page bg-light py-5">
      <Container>
        {/* Điều hướng breadcrumb cơ bản */}
        <div className="mb-4">
          <Link
            to="/tours"
            className="text-decoration-none text-muted small hover-primary"
          >
            ← Trở lại danh sách tour
          </Link>
        </div>

        {/* Tiêu đề & Thông tin cơ bản */}
        <Row className="mb-4 align-items-center">
          <Col lg={8}>
            <div className="d-flex align-items-center gap-2 mb-2">
              <Badge bg="info" className="text-dark fw-semibold">
                Mã: {tour.code}
              </Badge>
              <Badge
                bg={tour.statusBooking === "OPEN" ? "success" : "secondary"}
              >
                {tour.statusBooking === "OPEN" ? "Đang mở Booking" : "Tạm đóng"}
              </Badge>
            </div>
            <h1 className="fw-bold text-dark mb-2">{tour.name}</h1>
            <p className="text-muted mb-0">{tour.description}</p>
          </Col>
          <Col lg={4} className="text-lg-end mt-3 mt-lg-0">
            <Card className="shadow-sm border-0 p-3 bg-white rounded-4">
              <div className="text-muted small mb-1">Thời gian chuyến đi</div>
              <div className="fw-bold text-dark mb-3">
                📅 {tour.startDate} đến {tour.endDate}
              </div>
              <Button
                variant="success"
                size="lg"
                className="w-100 rounded-pill fw-bold shadow-sm"
              >
                Đặt Tour Ngay
              </Button>
            </Card>
          </Col>
        </Row>

        {/* Thông tin du thuyền */}
        {tour.cruise && (
          <Card className="shadow-sm border-0 rounded-4 mb-4 overflow-hidden">
            <Card.Header className="bg-white border-bottom py-3">
              <h5 className="fw-bold m-0 text-primary">
                🚢 Thông Tin Du Thuyền
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              <Row className="align-items-center">
                <Col md={4} className="mb-3 mb-md-0">
                  {tour.cruise.imageUrl ? (
                    <img
                      src={tour.cruise.imageUrl}
                      alt={tour.cruise.name}
                      className="img-fluid rounded-3 w-100 object-fit-cover shadow-sm"
                      style={{ height: "180px" }}
                    />
                  ) : (
                    <div
                      className="bg-secondary text-white rounded-3 h-100 d-flex align-items-center justify-content-center"
                      style={{ height: "180px" }}
                    >
                      Không có ảnh
                    </div>
                  )}
                </Col>
                <Col md={8}>
                  <h4 className="fw-bold">
                    {tour.cruise.name}{" "}
                    <span className="text-muted fs-6">
                      ({tour.cruise.code})
                    </span>
                  </h4>
                  <p className="text-muted small">
                    {tour.cruise.description ||
                      "Du thuyền tiêu chuẩn quốc tế với đầy đủ tiện ích giải trí sang trọng."}
                  </p>
                  <div className="d-flex gap-4 small text-secondary fw-medium">
                    <div>
                      👥 Tối đa:{" "}
                      <strong>{tour.cruise.maxPassengers} hành khách</strong>
                    </div>
                  </div>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        )}

        {/* Các tab thông tin chi tiết: Lịch trình, Gói dịch vụ, Tiện ích tàu */}
        <Card className="shadow-sm border-0 rounded-4 overflow-hidden">
          <Card.Body className="p-4">
            <Tabs
              defaultActiveKey="schedules"
              id="tour-detail-tabs"
              className="mb-4 custom-tabs"
            >
              {/* Tab Lịch trình */}
              <Tab eventKey="schedules" title="📅 Lịch Trình Chi Tiết">
                <div className="schedule-timeline mt-3">
                  {tour.schedules &&
                    tour.schedules.map((schedule) => (
                      <div
                        key={schedule.id}
                        className="schedule-day-block mb-4 p-3 bg-light rounded-3 border-start border-4 border-primary"
                      >
                        <h5 className="fw-bold text-primary mb-1">
                          Ngày {schedule.dayNumber}: {schedule.name}
                        </h5>
                        <p className="text-muted small mb-3">
                          {schedule.description} ({schedule.realDay})
                        </p>

                        {schedule.stops && schedule.stops.length > 0 && (
                          <div className="stops-list ms-2 ps-3 border-start">
                            {schedule.stops.map((stop) => (
                              <div key={stop.id} className="stop-item mb-3">
                                <div className="fw-semibold text-dark">
                                  📍 Cảng dừng: {stop.portName} ({stop.portCity}
                                  , {stop.portCountry})
                                </div>
                                <div className="text-muted small">
                                  Thời gian đến: {stop.arriveAt} - Rời đi:{" "}
                                  {stop.leaveAt}
                                </div>
                                {stop.portDescription && (
                                  <div className="text-muted small fst-italic">
                                    {stop.portDescription}
                                  </div>
                                )}

                                {/* Hoạt động tham quan tại điểm dừng nếu có */}
                                {stop.visitActivity && (
                                  <div className="mt-2 p-2 bg-white rounded border border-warning small">
                                    <span className="fw-bold text-dark">
                                      🎯 Hoạt động tham quan:{" "}
                                    </span>
                                    <span>{stop.visitActivity.visitName}</span>{" "}
                                    -{" "}
                                    <span className="text-danger fw-semibold">
                                      {stop.visitActivity.price
                                        ? Number(
                                            stop.visitActivity.price,
                                          ).toLocaleString() + " đ"
                                        : "Miễn phí"}
                                    </span>
                                  </div>
                                )}
                              </div>
                            ))}
                          </div>
                        )}
                      </div>
                    ))}
                </div>
              </Tab>

              {/* Tab Gói Tour & Quyền lợi */}
              <Tab eventKey="packages" title="🎁 Các Gói Tour & Quyền Lợi">
                <Row xs={1} md={2} className="g-4 mt-2">
                  {tour.packages &&
                    tour.packages.map((pkg) => (
                      <Col key={pkg.id}>
                        <Card className="h-100 border rounded-3 p-3 shadow-sm">
                          <Card.Body>
                            <h5 className="fw-bold text-dark">{pkg.name}</h5>
                            <p className="text-muted small">
                              {pkg.description}
                            </p>
                            <div className="fw-bold text-primary fs-5 mb-3">
                              {Number(pkg.price).toLocaleString("vi-VN")} đ
                            </div>

                            <h6 className="fw-semibold small text-uppercase text-secondary">
                              Quyền lợi bao gồm:
                            </h6>
                            <ul className="small text-muted ps-3 mb-0">
                              {pkg.benefits &&
                                pkg.benefits.map((benefit, idx) => (
                                  <li key={idx}>
                                    {benefit.type} (Số lượng: {benefit.quantity}{" "}
                                    {benefit.discountPercent
                                      ? `, Giảm: ${benefit.discountPercent}%`
                                      : ""}
                                    )
                                  </li>
                                ))}
                            </ul>
                          </Card.Body>
                        </Card>
                      </Col>
                    ))}
                </Row>
              </Tab>

              {/* Tab Tiện ích & Hoạt động trên tàu */}
              <Tab eventKey="onboard" title="⚓ Tiện Ích & Hoạt Động Tàu">
                <div className="mt-3">
                  <h6 className="fw-bold mb-3">Hoạt động giải trí trên tàu</h6>
                  <Row xs={1} md={3} className="g-3">
                    {tour.onboardActivities &&
                      tour.onboardActivities.map((act) => (
                        <Col key={act.id}>
                          <Card className="h-100 border-0 bg-light p-3">
                            <h6 className="fw-bold">{act.activityName}</h6>
                            <p className="text-muted small mb-2">
                              {act.activityDescription}
                            </p>
                            <span className="fw-bold text-primary small">
                              Giá:{" "}
                              {act.price
                                ? Number(act.price).toLocaleString() + " đ"
                                : "Miễn phí"}
                            </span>
                          </Card>
                        </Col>
                      ))}
                  </Row>
                </div>
              </Tab>
            </Tabs>
          </Card.Body>
        </Card>
      </Container>
    </div>
  );
}
