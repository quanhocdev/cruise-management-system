// src/components/guest/GuestFooter.jsx
import { Container, Row, Col } from "react-bootstrap";
import { Link } from "react-router-dom";
import "../../styles/guest/GuestFooter.css";

export default function GuestFooter() {
  return (
    <footer className="guest-footer bg-dark text-white pt-5 pb-3">
      <Container>
        <Row className="gy-4">
          <Col xs={12} md={4}>
            <h5 className="fw-bold text-primary mb-3">🚢 CruiseTour</h5>
            <p className="text-white-50 small">
              Hệ thống quản lý và đặt tour du thuyền đẳng cấp quốc tế. Mang lại
              những trải nghiệm nghỉ dưỡng tuyệt vời nhất trên biển.
            </p>
          </Col>
          <Col xs={6} md={2}>
            <h6 className="fw-semibold mb-3">Khám phá</h6>
            <ul className="list-unstyled small text-white-50 d-flex flex-column gap-2">
              <li>
                <Link to="/" className="text-decoration-none text-white-50">
                  Trang chủ
                </Link>
              </li>
              <li>
                <Link
                  to="/tours"
                  className="text-decoration-none text-white-50"
                >
                  Danh sách Tour
                </Link>
              </li>
            </ul>
          </Col>
          <Col xs={6} md={2}>
            <h6 className="fw-semibold mb-3">Hỗ trợ</h6>
            <ul className="list-unstyled small text-white-50 d-flex flex-column gap-2">
              <li>
                <a href="#help" className="text-decoration-none text-white-50">
                  Trung tâm trợ giúp
                </a>
              </li>
              <li>
                <a
                  href="#policy"
                  className="text-decoration-none text-white-50"
                >
                  Chính sách bảo mật
                </a>
              </li>
            </ul>
          </Col>
          <Col xs={12} md={4}>
            <h6 className="fw-semibold mb-3">Liên hệ</h6>
            <p className="text-white-50 small mb-1">
              📍 Địa chỉ: 1 Đại Học, TP.HCM
            </p>
            <p className="text-white-50 small mb-1">
              📧 Email: support@cruisetour.com
            </p>
            <p className="text-white-50 small">📞 Hotline: 1900 xxxx</p>
          </Col>
        </Row>
        <hr className="border-secondary my-4" />
        <div className="text-center text-white-50 small">
          © 2026 CruiseTour System. All rights reserved.
        </div>
      </Container>
    </footer>
  );
}
