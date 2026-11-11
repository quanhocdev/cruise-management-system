
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Navbar,
  Nav,
  Button,
  Form,
  InputGroup,
  Dropdown,
  Modal,
} from "react-bootstrap";

import { useAuth } from "../../context/AuthContext";
import { getRedirectPathByRole } from "../../routes/roleRoutes";

import useNotifications from "../../modules/passenger/hooks/useNotifications";
import NotificationBell from "../../modules/passenger/components/notifications/NotificationBell";

import "../../styles/guest/GuestHeader.css";

export default function GuestHeader() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  

  const [showLoginModal, setShowLoginModal] = useState(false);

  // Notification dành riêng cho Passenger.
  const {
    notifications,
    unreadCount,
    loading: notificationLoading,
    markAsRead,
    markAllAsRead,
    loadNotifications,
  } = useNotifications();

  const handleLogout = async () => {
    await logout();
  };

  const handleMessagesClick = () => {
    if (!user) {
      setShowLoginModal(true);
      return;
    }

    if (user.role === "PASSENGER") {
      navigate("/passenger/messages");
      return;
    }

    setShowLoginModal(true);
  };
  const handleNotificationClick = async (notification) => {
  if (!notification) return;

  // Đánh dấu đã đọc trước khi điều hướng.
  if (!notification.isRead) {
    await markAsRead(notification.id);
  }

  // Đóng dropdown thông báo sẽ được xử lý ở component Bell.
  switch (notification.referenceType) {
    case "FEEDBACK_INVITATION":
      navigate("/passenger/feedback");
      break;

    case "BOOKING":
      if (notification.referenceId) {
        navigate(`/passenger/bookings/${notification.referenceId}`);
      } else {
        navigate("/passenger/bookings");
      }
      break;

    default:
      // Chưa xác định màn hình đích thì không điều hướng.
      break;
  }
};

  const handleGoToLogin = () => {
    setShowLoginModal(false);
    navigate("/login");
  };

  return (
    <>
      <Navbar
        bg="white"
        expand="lg"
        className="guest-header shadow-sm sticky-top"
      >
        <Container>
          <Navbar.Brand as={Link} to="/" className="fw-bold text-primary fs-4">
            🚢 CruiseTour
          </Navbar.Brand>

          <Navbar.Toggle aria-controls="guest-navbar-nav" />

          <Navbar.Collapse id="guest-navbar-nav">
            <Nav className="me-auto ms-4 gap-3">
              <Nav.Link as={Link} to="/" className="fw-medium">
                Trang chủ
              </Nav.Link>

              <Nav.Link as={Link} to="/tours" className="fw-medium">
                Khám phá Tour
              </Nav.Link>

              <Nav.Link as={Link} to="/about" className="fw-medium">
                Về chúng tôi
              </Nav.Link>

              <Nav.Link as={Link} to="/contact" className="fw-medium">
                Liên hệ
              </Nav.Link>

              {(!user || user.role === "PASSENGER") && (
                <Nav.Link
                  as="button"
                  type="button"
                  onClick={handleMessagesClick}
                  className="fw-medium border-0 bg-transparent"
                >
                  💬 Tin nhắn
                </Nav.Link>
              )}
            </Nav>

            {/* Thanh tìm kiếm */}
            <Form className="d-none d-lg-flex me-3">
              <InputGroup className="search-input-group">
                <InputGroup.Text className="bg-light border-end-0">
                  🔍
                </InputGroup.Text>

                <Form.Control
                  type="text"
                  placeholder="Tìm kiếm điểm đến, du thuyền..."
                  className="bg-light border-start-0 shadow-none"
                  readOnly
                />
              </InputGroup>
            </Form>

            {/* Khu vực tài khoản */}
            <div className="d-flex align-items-center gap-2 mt-3 mt-lg-0">
              {user ? (
                <>
                  {/* Chuông thông báo chỉ dành cho Passenger */}
                  {user.role === "PASSENGER" && (
                    <NotificationBell
  notifications={notifications}
  unreadCount={unreadCount}
  loading={notificationLoading}
  onMarkAsRead={markAsRead}
  onMarkAllAsRead={markAllAsRead}
  onRefresh={loadNotifications}
  onNotificationClick={handleNotificationClick}
/>
                  )}

                  <Dropdown align="end">
                    <Dropdown.Toggle
                      variant="outline-primary"
                      size="sm"
                      className="rounded-pill px-3 fw-semibold d-flex align-items-center gap-2"
                    >
                      <span>👤 {user.username}</span>

                      <span className="badge bg-primary text-light small">
                        {user.role}
                      </span>
                    </Dropdown.Toggle>

                    <Dropdown.Menu className="shadow border-0 py-2 rounded-3 mt-2">
                      {user.role === "PASSENGER" && (
                        <>
                          <Dropdown.Item
                            as={Link}
                            to="/passenger/bookings"
                            className="py-2 small fw-medium"
                          >
                            🎫 Tour đã đặt của tôi
                          </Dropdown.Item>

                          <Dropdown.Item
                            as={Link}
                            to="/passenger/feedback"
                            className="py-2 small fw-medium"
                          >
                            ⭐ Đánh giá của tôi
                          </Dropdown.Item>
                        </>
                      )}

                      <Dropdown.Item
                        as={Link}
                        to={getRedirectPathByRole(user.role)}
                        className="py-2 small fw-medium"
                      >
                        📊 Trang Dashboard
                      </Dropdown.Item>

                      <Dropdown.Divider />

                      <Dropdown.Item
                        onClick={handleLogout}
                        className="py-2 small text-danger fw-semibold"
                      >
                        🚪 Đăng xuất
                      </Dropdown.Item>
                    </Dropdown.Menu>
                  </Dropdown>
                </>
              ) : (
                <>
                  <Button
                    as={Link}
                    to="/login"
                    variant="outline-primary"
                    size="sm"
                    className="px-3"
                  >
                    Đăng nhập
                  </Button>

                  <Button
                    as={Link}
                    to="/register"
                    variant="primary"
                    size="sm"
                    className="px-3"
                  >
                    Đăng ký
                  </Button>
                </>
              )}
            </div>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      {/* Modal yêu cầu đăng nhập */}
      <Modal
        show={showLoginModal}
        onHide={() => setShowLoginModal(false)}
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title>Đăng nhập để sử dụng tin nhắn</Modal.Title>
        </Modal.Header>

        <Modal.Body className="text-center py-4">
          <div className="fs-1 mb-3">💬</div>

          <p className="mb-2">
            Vui lòng đăng nhập bằng tài khoản hành khách để liên hệ với các
            phòng ban trên du thuyền.
          </p>

          <small className="text-muted">
            Bạn cần có tài khoản Passenger để sử dụng chức năng này.
          </small>
        </Modal.Body>

        <Modal.Footer>
          <Button
            variant="outline-secondary"
            onClick={() => setShowLoginModal(false)}
          >
            Để sau
          </Button>

          <Button variant="primary" onClick={handleGoToLogin}>
            Đăng nhập ngay
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
}
