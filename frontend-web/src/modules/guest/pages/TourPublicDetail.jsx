import { useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import {
  Container,
  Row,
  Col,
  Button,
  Spinner,
  Alert,
  Tabs,
  Tab,
} from "react-bootstrap";
import { usePublicTourDetail } from "../hooks/usePublicTours";
import { useAuth } from "../../../context/AuthContext";

import TourHeader from "../components/tour-detail/TourHeader";
import TourBookingCard from "../components/tour-detail/TourBookingCard";
import TourCruiseCard from "../components/tour-detail/TourCruiseCard";
import TourScheduleTab from "../components/tour-detail/TourScheduleTab";
import TourPackagesTab from "../components/tour-detail/TourPackagesTab";
import TourActivitiesTab from "../components/tour-detail/TourActivitiesTab";
import LoginRequiredModal from "../components/tour-detail/LoginRequiredModal";

import "../styles/TourPublicDetail.css";

export default function TourPublicDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { tour, loading, error } = usePublicTourDetail(id);
  const { user } = useAuth();
  const [showLoginModal, setShowLoginModal] = useState(false);

  const handleBookingClick = () => {
    if (!user) {
      setShowLoginModal(true);
      return;
    }
    if (user.role === "PASSENGER") {
      navigate(`/passenger/bookings/new?tourId=${id}`);
    } else {
      alert(
        "Tài khoản quản trị/nhân viên không sử dụng chức năng đặt tour cá nhân.",
      );
    }
  };

  const handleLogin = () => {
    setShowLoginModal(false);
    navigate("/login", {
      state: {
        from: {
          pathname: "/passenger/bookings/new",
          search: `?tourId=${id}`,
        },
      },
    });
  };

  if (loading) {
    return (
      <div className="tour-detail-state">
        <Spinner animation="border" variant="primary" />
        <p>Đang tải thông tin chi tiết hành trình...</p>
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
    <div className="tour-detail-page">
      <Container>
        <TourHeader tour={tour} />

        <Row className="g-4">
          {/* Cột phải: đặt tour + du thuyền (hiện trước trên mobile) */}
          <Col lg={4} className="order-1 order-lg-2">
            <aside className="tour-detail-aside">
              <TourBookingCard tour={tour} onBook={handleBookingClick} />
              <TourCruiseCard
                cruise={tour.cruise}
                maxPassengers={tour.maxPassengers}
              />
            </aside>
          </Col>

          {/* Cột trái: nội dung chi tiết */}
          <Col lg={8} className="order-2 order-lg-1">
            <div className="tour-detail-panel">
              <Tabs
                defaultActiveKey="schedules"
                id="tour-detail-tabs"
                className="tour-tabs"
              >
                <Tab eventKey="schedules" title="Lịch trình">
                  <TourScheduleTab schedules={tour.schedules} />
                </Tab>
                <Tab eventKey="packages" title="Gói tour & quyền lợi">
                  <TourPackagesTab packages={tour.packages} />
                </Tab>
                <Tab eventKey="onboard" title="Tiện ích & hoạt động">
                  <TourActivitiesTab activities={tour.onboardActivities} />
                </Tab>
              </Tabs>
            </div>
          </Col>
        </Row>
      </Container>

      <LoginRequiredModal
        show={showLoginModal}
        onHide={() => setShowLoginModal(false)}
        onLogin={handleLogin}
      />
    </div>
  );
}
