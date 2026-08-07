import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import api from "../../../api/axios";

import { Container, Card, Form, Button, Alert } from "react-bootstrap";

export default function VerifyOtpPage() {
  const location = useLocation();
  const navigate = useNavigate();

  const { userId, email } = location.state || {};

  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await api.post("/auth/verify-email", {
        userId: userId,
        otp: otp,
      });

      setSuccess("Xác thực thành công, chuyển sang đăng nhập");

      setTimeout(() => {
        navigate("/login");
      }, 1500);
    } catch (err) {
      setError(err.response?.data?.message || "OTP không đúng");
    }
  };

  return (
    <Container className="mt-5">
      <Card>
        <Card.Body>
          <h3>Xác thực Email</h3>

          <p>
            Mã OTP đã gửi tới:
            <br />
            <b>{email}</b>
          </p>

          {error && <Alert variant="danger">{error}</Alert>}

          {success && <Alert variant="success">{success}</Alert>}

          <Form onSubmit={handleSubmit}>
            <Form.Group>
              <Form.Label>Nhập OTP</Form.Label>

              <Form.Control
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                placeholder="6 chữ số"
              />
            </Form.Group>

            <Button className="mt-3 w-100" type="submit">
              Xác nhận
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
}
