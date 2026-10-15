// src/modules/auth/pages/LoginPage.jsx

import { useState } from "react";
import { useAuth } from "../../../context/AuthContext";
import { useNavigate, useLocation } from "react-router-dom";
import { getRedirectPathByRole } from "../../../routes/roleRoutes";
import useGoogleAuth from "../hooks/useGoogleAuth";

import { Container, Card, Form, Button, Alert, Spinner } from "react-bootstrap";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const { loginWithGoogle, googleLoading, googleError } = useGoogleAuth();

  const getTargetPath = (role) => {
    const fromPath = location.state?.from;

    if (role === "PASSENGER" && fromPath) {
      return `${fromPath.pathname}${fromPath.search || ""}`;
    }

    return role === "PASSENGER" ? "/" : getRedirectPathByRole(role);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const role = await login(username, password);

      navigate(getTargetPath(role), {
        replace: true,
      });
    } catch (err) {
      setError(err.response?.data?.message || "Đăng nhập thất bại!");
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = async () => {
    setError("");
    await loginWithGoogle();
  };

  return (
    <div className="bg-light min-vh-100 d-flex align-items-center justify-content-center py-5">
      <Container>
        <div className="row justify-content-center">
          <div className="col-12 col-sm-10 col-md-8 col-lg-5 col-xl-4">
            <Card className="shadow-lg border-0 rounded-4">
              <Card.Body className="p-4 p-sm-5">
                <div className="text-center mb-4">
                  <div
                    className="bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-3"
                    style={{
                      width: "60px",
                      height: "60px",
                    }}
                  >
                    ⚓
                  </div>

                  <h3 className="fw-bold text-dark">CRUISE SYSTEM</h3>

                  <p className="text-muted small">
                    Đăng nhập để truy cập hệ thống
                  </p>
                </div>

                {error && (
                  <Alert
                    variant="danger"
                    dismissible
                    onClose={() => setError("")}
                  >
                    {error}
                  </Alert>
                )}

                {googleError && <Alert variant="danger">{googleError}</Alert>}

                <Form onSubmit={handleSubmit}>
                  <Form.Group className="mb-3">
                    <Form.Label>Tài khoản</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Nhập username"
                      value={username}
                      onChange={(event) => setUsername(event.target.value)}
                      required
                      autoComplete="username"
                    />
                  </Form.Group>

                  <Form.Group className="mb-4">
                    <Form.Label>Mật khẩu</Form.Label>
                    <Form.Control
                      type="password"
                      placeholder="Nhập mật khẩu"
                      value={password}
                      onChange={(event) => setPassword(event.target.value)}
                      required
                      autoComplete="current-password"
                    />
                  </Form.Group>

                  <Button
                    type="submit"
                    variant="primary"
                    className="w-100"
                    disabled={loading || googleLoading}
                  >
                    {loading ? (
                      <>
                        <Spinner size="sm" className="me-2" />
                        Đang xử lý...
                      </>
                    ) : (
                      "Đăng nhập"
                    )}
                  </Button>
                </Form>

                <div className="d-flex align-items-center my-4">
                  <hr className="flex-grow-1" />
                  <span className="px-3 text-muted small">HOẶC</span>
                  <hr className="flex-grow-1" />
                </div>

                <Button
                  type="button"
                  variant="outline-dark"
                  className="w-100 d-flex align-items-center justify-content-center gap-2"
                  disabled={loading || googleLoading}
                  onClick={handleGoogleLogin}
                >
                  {googleLoading ? (
                    <>
                      <Spinner size="sm" />
                      Đang đăng nhập Google...
                    </>
                  ) : (
                    <>
                      <span className="fw-bold">G</span>
                      Tiếp tục với Google
                    </>
                  )}
                </Button>
              </Card.Body>
            </Card>
          </div>
        </div>
      </Container>
    </div>
  );
}
