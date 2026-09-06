// src/components/guest/GuestHeader.jsx

import { Link, useNavigate } from "react-router-dom";
import {
  Container,
  Navbar,
  Nav,
  Button,
  Form,
  InputGroup,
  Dropdown,
} from "react-bootstrap";
import { useAuth } from "../../context/AuthContext";
import { getRedirectPathByRole } from "../../routes/roleRoutes";
import "../../styles/guest/GuestHeader.css";

export default function GuestHeader() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
  };

  return (
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
          </Nav>

          {/* Thanh tìm kiếm tĩnh */}
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

          {/* Khu vực hiển thị theo trạng thái Đăng nhập */}
          <div className="d-flex align-items-center gap-2 mt-3 mt-lg-0">
            {user ? (
              // TRƯỜNG HỢP ĐÃ ĐĂNG NHẬP: Hiển thị tên/role và Dropdown hoặc nút Đăng xuất
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
                    <Dropdown.Item
                      as={Link}
                      to="/passenger/bookings"
                      className="py-2 small fw-medium"
                    >
                      🎫 Tour đã đặt của tôi
                    </Dropdown.Item>
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
            ) : (
              // TRƯỜNG HỢP CHƯA ĐĂNG NHẬP: Hiển thị nút Đăng nhập & Đăng ký
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
  );
}
