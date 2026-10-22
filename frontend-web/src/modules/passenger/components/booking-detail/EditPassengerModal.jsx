import { useState } from "react";
import { Modal, Form, Row, Col, Button, Spinner } from "react-bootstrap";
import "./EditPassengerModal.css";

export default function EditPassengerModal({ passenger, onSave, onHide }) {
  const [draft, setDraft] = useState({ ...passenger });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const set = (field) => (e) =>
    setDraft((prev) => ({ ...prev, [field]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      await onSave(draft);
      onHide();
    } catch (err) {
      setError(err.response?.data?.message || "Không thể cập nhật hành khách.");
      setSaving(false);
    }
  };

  return (
    <Modal show onHide={onHide} centered className="bd-modal">
      <Modal.Header closeButton>
        <Modal.Title>Chỉnh sửa thông tin hành khách</Modal.Title>
      </Modal.Header>

      <Form onSubmit={handleSubmit}>
        <Modal.Body>
          {error && (
            <p className="bd-modal__error" role="alert">
              {error}
            </p>
          )}

          <Row className="g-3">
            <Col xs={12}>
              <Form.Label>Họ và tên</Form.Label>
              <Form.Control
                type="text"
                required
                value={draft.fullName || ""}
                onChange={set("fullName")}
              />
            </Col>

            <Col md={6}>
              <Form.Label>Ngày sinh</Form.Label>
              <Form.Control
                type="date"
                required
                value={draft.dateOfBirth || ""}
                onChange={set("dateOfBirth")}
              />
            </Col>
            <Col md={6}>
              <Form.Label>Giới tính</Form.Label>
              <Form.Select
                value={draft.gender || "Nam"}
                onChange={set("gender")}
              >
                <option value="Nam">Nam</option>
                <option value="Nữ">Nữ</option>
                <option value="Khác">Khác</option>
              </Form.Select>
            </Col>

            <Col md={6}>
              <Form.Label>Số điện thoại</Form.Label>
              <Form.Control
                type="tel"
                value={draft.phoneNumber || ""}
                onChange={set("phoneNumber")}
              />
            </Col>
            <Col md={6}>
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                value={draft.email || ""}
                onChange={set("email")}
              />
            </Col>

            <Col md={6}>
              <Form.Label>Loại giấy tờ</Form.Label>
              <Form.Select
                value={draft.idCardType || "CCCD"}
                onChange={set("idCardType")}
              >
                <option value="CCCD">CCCD</option>
                <option value="CMND">CMND</option>
                <option value="Passport">Passport</option>
              </Form.Select>
            </Col>
            <Col md={6}>
              <Form.Label>Số định danh / CCCD</Form.Label>
              <Form.Control
                type="text"
                required
                value={draft.identificationNumber || ""}
                onChange={set("identificationNumber")}
              />
            </Col>

            <Col xs={12}>
              <Form.Label>Ghi chú giấy tờ</Form.Label>
              <Form.Control
                type="text"
                value={draft.documentNote || ""}
                onChange={set("documentNote")}
              />
            </Col>
          </Row>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="light" onClick={onHide} disabled={saving}>
            Đóng
          </Button>
          <Button type="submit" className="bd-modal__confirm" disabled={saving}>
            {saving ? <Spinner animation="border" size="sm" /> : "Lưu thay đổi"}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
