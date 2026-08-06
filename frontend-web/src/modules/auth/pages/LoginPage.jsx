// src/modules/auth/pages/LoginPage.jsx

import { useState } from "react";
import { useAuth } from "../../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { getRedirectPathByRole } from "../../../routes/roleRoutes";

import { Container, Card, Form, Button, Alert, Spinner } from "react-bootstrap";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {
      const role = await login(username, password);

      const targetPath = getRedirectPathByRole(role);

      navigate(targetPath, {
        replace: true,
      });
    } catch (err) {
      setError(err.response?.data?.message || "Đăng nhập thất bại!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-light min-vh-100 d-flex align-items-center justify-content-center py-5">
      <Container>
        <div className="row justify-content-center">
          <div className="col-12 col-sm-10 col-md-8 col-lg-5 col-xl-4">
            <Card className="shadow-lg border-0 rounded-4">
              <Card.Body className="p-4 p-sm-5">
                {/* Header */}
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

                {/* Error */}
                {error && (
                  <Alert
                    variant="danger"
                    dismissible
                    onClose={() => setError("")}
                  >
                    {error}
                  </Alert>
                )}

                <Form onSubmit={handleSubmit}>
                  <Form.Group className="mb-3">
                    <Form.Label>Tài khoản</Form.Label>

                    <Form.Control
                      type="text"
                      placeholder="Nhập username"
                      value={username}
                      onChange={(e) => setUsername(e.target.value)}
                      required
                    />
                  </Form.Group>

                  <Form.Group className="mb-4">
                    <Form.Label>Mật khẩu</Form.Label>

                    <Form.Control
                      type="password"
                      placeholder="Nhập mật khẩu"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                    />
                  </Form.Group>

                  <Button
                    type="submit"
                    variant="primary"
                    className="w-100"
                    disabled={loading}
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
              </Card.Body>
            </Card>
          </div>
        </div>
      </Container>
    </div>
  );
}
