// src/modules/auth/pages/RegisterPage.jsx

import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { Container, Card, Form, Button, Alert, Spinner } from "react-bootstrap";

import api from "../../../api/axios";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: "",
    password: "",
    email: "",
  });

  const [error, setError] = useState("");

  const [success, setSuccess] = useState("");

  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setSuccess("");
    setLoading(true);

    try {
      const res = await api.post("/auth/register", form);

      navigate("/verify-email", {
        state: {
          userId: res.data.id,
          email: res.data.email,
        },
      });
    } catch (err) {
      setError(err.response?.data?.message || "Đăng ký thất bại!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-light min-vh-100 d-flex align-items-center justify-content-center">
      <Container>
        <div className="row justify-content-center">
          <div className="col-md-5">
            <Card className="shadow border-0">
              <Card.Body className="p-4">
                <h3 className="text-center mb-4">Đăng ký tài khoản</h3>

                {error && <Alert variant="danger">{error}</Alert>}

                {success && <Alert variant="success">{success}</Alert>}

                <Form onSubmit={handleSubmit}>
                  <Form.Group className="mb-3">
                    <Form.Label>Username</Form.Label>

                    <Form.Control
                      name="username"
                      value={form.username}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>

                  <Form.Group className="mb-3">
                    <Form.Label>Email</Form.Label>

                    <Form.Control
                      type="email"
                      name="email"
                      value={form.email}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>

                  <Form.Group className="mb-4">
                    <Form.Label>Password</Form.Label>

                    <Form.Control
                      type="password"
                      name="password"
                      value={form.password}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>

                  <Button type="submit" className="w-100" disabled={loading}>
                    {loading ? (
                      <>
                        <Spinner size="sm" /> Đang tạo...
                      </>
                    ) : (
                      "Đăng ký"
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
