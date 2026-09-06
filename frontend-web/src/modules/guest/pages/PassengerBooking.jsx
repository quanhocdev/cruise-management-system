// src/modules/guest/pages/PassengerBooking.jsx

import { Container, Alert, Button } from "react-bootstrap";
import { useSearchParams, useNavigate } from "react-router-dom";

export default function PassengerBooking() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const tourId = searchParams.get("tourId");

  return (
    <Container className="py-5 text-center">
      <Alert variant="success" className="py-4 shadow-sm">
        <h2 className="fw-bold mb-3">
          🎉 Chúc mừng bạn đã vào đúng trang Đặt Vé (Passenger Booking)!
        </h2>
        <p className="text-muted mb-2">
          Đang thực hiện đặt vé cho Tour có ID:{" "}
          <strong>{tourId || "Không xác định"}</strong>
        </p>
        <hr />
        <Button
          variant="outline-primary"
          onClick={() => navigate("/tours")}
          className="mt-3 rounded-pill px-4"
        >
          ← Quay lại danh sách tour
        </Button>
      </Alert>
    </Container>
  );
}
