// src/modules/guest/pages/HomePage.jsx

import { Link } from "react-router-dom";
import { Container, Button, Card } from "react-bootstrap";

export default function HomePage() {
  return (
    <div className="bg-light min-vh-100">
      <Container className="py-5">
        <Card className="shadow border-0 text-center">
          <Card.Body className="p-5">
            <h1 className="fw-bold mb-3">🚢 CRUISE SYSTEM</h1>

            <p className="text-muted mb-4">
              Hệ thống quản lý chuyến du lịch tàu biển
            </p>

            <div className="d-flex justify-content-center gap-3">
              <Button as={Link} to="/login" variant="primary">
                Đăng nhập
              </Button>

              <Button as={Link} to="/register" variant="outline-primary">
                Đăng ký
              </Button>
            </div>
          </Card.Body>
        </Card>
      </Container>
    </div>
  );
}
