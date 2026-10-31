// src/modules/auth/pages/GoogleVerifyOtpPage.jsx

import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Alert, Button, Card, Container, Form, Spinner } from "react-bootstrap";

import googleAuthService from "../services/googleAuthService";
import { useAuth } from "../../../context/AuthContext";
import { getRedirectPathByRole } from "../../../routes/roleRoutes";

export default function GoogleVerifyOtpPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { refreshUser } = useAuth();

  const userId = location.state?.userId;
  const email = location.state?.email || "";

  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (!userId) {
    return (
      <Container className="py-5">
        <Card className="mx-auto shadow-sm" style={{ maxWidth: 480 }}>
          <Card.Body>
            <Alert variant="warning">
              Phiên xác minh Google không hợp lệ hoặc đã bị mất. Vui lòng đăng
              nhập Google lại.
            </Alert>
            <Button
              className="w-100"
              onClick={() => navigate("/login", { replace: true })}
            >
              Quay lại đăng nhập
            </Button>
          </Card.Body>
        </Card>
      </Container>
    );
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (!/^\d{6}$/.test(otp)) {
      setError("Vui lòng nhập OTP gồm đúng 6 chữ số.");
      return;
    }

    setLoading(true);

    try {
      await googleAuthService.verifyOtp(userId, otp);

      // Backend vừa xác minh OTP và cấp cookie phiên nội bộ.
      const user = await refreshUser();

      if (!user?.role) {
        throw new Error(
          "Xác minh thành công nhưng không lấy được thông tin tài khoản.",
        );
      }

      const fromPath = location.state?.from;

      const targetPath =
        user.role === "PASSENGER" && fromPath
          ? `${fromPath.pathname}${fromPath.search || ""}`
          : user.role === "PASSENGER"
            ? "/"
            : getRedirectPathByRole(user.role);

      navigate(targetPath, { replace: true });
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.message ||
          "Xác minh OTP thất bại. Vui lòng thử lại.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-light min-vh-100 d-flex align-items-center justify-content-center py-5">
      <Container>
        <div className="row justify-content-center">
          <div className="col-12 col-sm-10 col-md-7 col-lg-5">
            <Card className="shadow border-0 rounded-4">
              <Card.Body className="p-4 p-sm-5">
                <div className="text-center mb-4">
                  <div className="fs-1 mb-2">✉️</div>
                  <h3 className="fw-bold">Xác minh Google Login</h3>
                  <p className="text-muted mb-0">
                    Nhập mã OTP được gửi đến email của bạn.
                  </p>
                  {email && <p className="fw-semibold mt-2 mb-0">{email}</p>}
                </div>

                {error && <Alert variant="danger">{error}</Alert>}

                <Form onSubmit={handleSubmit}>
                  <Form.Group className="mb-4">
                    <Form.Label>Mã OTP</Form.Label>
                    <Form.Control
                      type="text"
                      inputMode="numeric"
                      autoComplete="one-time-code"
                      placeholder="Nhập 6 chữ số"
                      value={otp}
                      onChange={(event) =>
                        setOtp(
                          event.target.value.replace(/\D/g, "").slice(0, 6),
                        )
                      }
                      maxLength={6}
                      required
                    />
                  </Form.Group>

                  <Button
                    type="submit"
                    className="w-100"
                    disabled={loading || otp.length !== 6}
                  >
                    {loading ? (
                      <>
                        <Spinner size="sm" className="me-2" />
                        Đang xác minh...
                      </>
                    ) : (
                      "Xác nhận OTP"
                    )}
                  </Button>

                  <Button
                    type="button"
                    variant="link"
                    className="w-100 mt-2"
                    disabled={loading}
                    onClick={() => navigate("/login", { replace: true })}
                  >
                    Quay lại đăng nhập
                  </Button>
                </Form>
              </Card.Body>
            </Card>
          </div>
        </div>
      </Container>
    </div>
  );
}
