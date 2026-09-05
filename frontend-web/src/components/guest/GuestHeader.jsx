// src/components/guest/GuestHeader.jsx
import { Link } from "react-router-dom";
import {
  Container,
  Navbar,
  Nav,
  Button,
  Form,
  InputGroup,
} from "react-bootstrap";
import "../../styles/guest/GuestHeader.css";

export default function GuestHeader() {
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

          {/* Nút hành động tài khoản */}
          <div className="d-flex align-items-center gap-2 mt-3 mt-lg-0">
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
          </div>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
