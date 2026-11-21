import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Container,
  Navbar,
  Button,
  Form,
  Dropdown,
  Modal,
} from "react-bootstrap";

import { useAuth } from "../../context/AuthContext";

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
        className="guest-header shadow-sm sticky-top py-2"
      >
        <Container className="d-flex align-items-center justify-content-between gap-2">
          {/* Logo bên trái */}
          <Navbar.Brand
            as={Link}
            to="/"
            className="fw-bold text-primary fs-4 m-0 d-flex align-items-center gap-2"
          >
            <span>🚢</span>
            <span>CruiseTour</span>
          </Navbar.Brand>

          {/* Cụm bên phải: Tìm kiếm + Chuông + Tài khoản */}
          <div className="d-flex align-items-center gap-2 gap-md-3 ms-auto">
            {/* Thanh tìm kiếm làm đẹp dạng pill bên trái chuông */}
            <Form className="d-none d-md-block">
              <div className="guest-search-box">
                <svg
                  className="guest-search-box__icon"
                  viewBox="0 0 24 24"
                  width="17"
                  height="17"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <circle cx="11" cy="11" r="8" />
                  <path d="m21 21-4.3-4.3" />
                </svg>
                <Form.Control
                  type="text"
                  placeholder="Tìm kiếm điểm đến, du thuyền..."
                  className="guest-search-box__input"
                  readOnly
                />
              </div>
            </Form>

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

                {/* Nút icon tài khoản (không hiện tên/role bên ngoài) */}
                <Dropdown align="end">
                  <Dropdown.Toggle
                    as="button"
                    type="button"
                    className="guest-account-trigger"
                    aria-label="Menu tài khoản"
                  >
                    <svg
                      viewBox="0 0 24 24"
                      width="20"
                      height="20"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    >
                      <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                      <circle cx="12" cy="7" r="4" />
                    </svg>
                  </Dropdown.Toggle>

                  <Dropdown.Menu className="guest-account-menu shadow-lg border-0">
                    {/* 1. Thông tin tài khoản (Tên & Role) */}
                    <div className="guest-account-header">
                      <div className="guest-account-avatar">
                        {user.username?.charAt(0)?.toUpperCase() || "U"}
                      </div>
                      <div className="guest-account-info">
                        <div className="guest-account-name">
                          {user.username}
                        </div>
                        <span className="guest-account-role">{user.role}</span>
                      </div>
                    </div>

                    {/* 2. Lưới 4 card (2 cột x 2 hàng): Icon trên, tên dưới */}
                    <div className="guest-account-grid">
                      <Dropdown.Item
                        as={Link}
                        to="/passenger/bookings"
                        className="guest-grid-card"
                      >
                        <div className="guest-grid-card__icon bg-primary-subtle text-primary">
                          <svg
                            viewBox="0 0 24 24"
                            width="20"
                            height="20"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                          >
                            <path d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z" />
                            <path d="M13 5v2" />
                            <path d="M13 17v2" />
                            <path d="M13 11v2" />
                          </svg>
                        </div>
                        <span className="guest-grid-card__label">
                          Tour đã đặt
                        </span>
                      </Dropdown.Item>

                      <Dropdown.Item
                        as={Link}
                        to="/passenger/tracking"
                        className="guest-grid-card"
                      >
                        <div className="guest-grid-card__icon bg-info-subtle text-info">
                          <svg
                            viewBox="0 0 24 24"
                            width="20"
                            height="20"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                          >
                            <circle cx="12" cy="12" r="10" />
                            <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76" />
                          </svg>
                        </div>
                        <span className="guest-grid-card__label">
                          Giám sát hành trình
                        </span>
                      </Dropdown.Item>

                      <Dropdown.Item
                        as={Link}
                        to="/passenger/feedback"
                        className="guest-grid-card"
                      >
                        <div className="guest-grid-card__icon bg-warning-subtle text-warning">
                          <svg
                            viewBox="0 0 24 24"
                            width="20"
                            height="20"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                          >
                            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
                          </svg>
                        </div>
                        <span className="guest-grid-card__label">Đánh giá</span>
                      </Dropdown.Item>

                      <Dropdown.Item
                        as="button"
                        type="button"
                        onClick={handleMessagesClick}
                        className="guest-grid-card"
                      >
                        <div className="guest-grid-card__icon bg-success-subtle text-success">
                          <svg
                            viewBox="0 0 24 24"
                            width="20"
                            height="20"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                          >
                            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
                          </svg>
                        </div>
                        <span className="guest-grid-card__label">Nhắn tin</span>
                      </Dropdown.Item>
                    </div>

                    <Dropdown.Divider className="my-2" />

                    {/* 3. Danh sách 4 mục: Icon bên trái, chữ bên phải */}
                    <div className="guest-account-list">
                      <Dropdown.Item
                        as="button"
                        type="button"
                        onClick={(e) => e.preventDefault()}
                        className="guest-list-item"
                      >
                        <span className="guest-list-item__icon">
                          <svg
                            viewBox="0 0 24 24"
                            width="18"
                            height="18"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                          >
                            <circle cx="12" cy="12" r="10" />
                            <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3" />
                            <path d="M12 17h.01" />
                          </svg>
                        </span>
                        <span>Trợ giúp</span>
                      </Dropdown.Item>

                      <Dropdown.Item
                        as="button"
                        type="button"
                        onClick={(e) => e.preventDefault()}
                        className="guest-list-item"
                      >
                        <span className="guest-list-item__icon">
                          <svg
                            viewBox="0 0 24 24"
                            width="18"
                            height="18"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                          >
                            <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z" />
                          </svg>
                        </span>
                        <span>Giao diện: Sáng</span>
                      </Dropdown.Item>

                      <Dropdown.Item
                        as={Link}
                        to="/passenger/profile"
                        className="guest-list-item"
                      >
                        <span className="guest-list-item__icon">
                          <svg
                            viewBox="0 0 24 24"
                            width="18"
                            height="18"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                          >
                            <rect width="18" height="18" x="3" y="3" rx="2" />
                            <circle cx="12" cy="10" r="3" />
                            <path d="M7 21v-2a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v2" />
                          </svg>
                        </span>
                        <span>Hồ sơ cá nhân</span>
                      </Dropdown.Item>

                      <Dropdown.Divider className="my-1" />

                      <Dropdown.Item
                        as="button"
                        type="button"
                        onClick={handleLogout}
                        className="guest-list-item guest-list-item--danger"
                      >
                        <span className="guest-list-item__icon">
                          <svg
                            viewBox="0 0 24 24"
                            width="18"
                            height="18"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                          >
                            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                            <polyline points="16 17 21 12 16 7" />
                            <line x1="21" x2="9" y1="12" y2="12" />
                          </svg>
                        </span>
                        <span>Đăng xuất</span>
                      </Dropdown.Item>
                    </div>
                  </Dropdown.Menu>
                </Dropdown>
              </>
            ) : (
              <div className="d-flex align-items-center gap-2">
                <Button
                  as={Link}
                  to="/login"
                  variant="outline-primary"
                  size="sm"
                  className="rounded-pill px-3 fw-medium"
                >
                  Đăng nhập
                </Button>

                <Button
                  as={Link}
                  to="/register"
                  variant="primary"
                  size="sm"
                  className="rounded-pill px-3 fw-medium"
                >
                  Đăng ký
                </Button>
              </div>
            )}
          </div>
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
