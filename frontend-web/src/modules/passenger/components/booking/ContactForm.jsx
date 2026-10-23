import { Row, Col, Form } from "react-bootstrap";
import "./ContactForm.css";

export default function ContactForm({
  name,
  phone,
  onNameChange,
  onPhoneChange,
}) {
  return (
    <Row className="contact-form g-3">
      <Col md={6}>
        <Form.Label>Họ tên người liên hệ</Form.Label>
        <Form.Control
          type="text"
          required
          value={name}
          onChange={(e) => onNameChange(e.target.value)}
          placeholder="Nhập họ tên"
        />
      </Col>
      <Col md={6}>
        <Form.Label>Số điện thoại liên hệ</Form.Label>
        <Form.Control
          type="tel"
          required
          value={phone}
          onChange={(e) => onPhoneChange(e.target.value)}
          placeholder="Nhập số điện thoại"
        />
      </Col>
    </Row>
  );
}
