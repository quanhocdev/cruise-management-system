// src/modules/profile/components/ProfileForm.jsx
import { Col, Form, Row } from "react-bootstrap";

const GENDERS = {
  MALE: "Nam",
  FEMALE: "Nữ",
  OTHER: "Khác",
};

function Field({ label, name, form, type = "text" }) {
  return (
    <Form.Group className="mb-3" controlId={`pf-${name}`}>
      <Form.Label>{label}</Form.Label>
      <Form.Control type={type} value={form[name] || ""} readOnly />
    </Form.Group>
  );
}

export default function ProfileForm({ formState }) {
  const { form } = formState;

  return (
    <Form>
      <Row>
        <Col md={8}>
          <Field label="Họ và tên" name="fullName" form={form} />
        </Col>

        <Col md={4}>
          <Field label="Ngày sinh" name="dateOfBirth" type="date" form={form} />
        </Col>
      </Row>

      <Row>
        <Col md={6}>
          <Field label="Số điện thoại" name="phone" form={form} />
        </Col>

        <Col md={6}>
          <Form.Group className="mb-3" controlId="pf-gender">
            <Form.Label>Giới tính</Form.Label>
            <Form.Control
              value={GENDERS[form.gender] || form.gender || ""}
              readOnly
            />
          </Form.Group>
        </Col>
      </Row>

      <Field label="Địa chỉ" name="address" form={form} />

      <Row>
        <Col md={6}>
          <Field label="Quốc tịch" name="nationality" form={form} />
        </Col>

        <Col md={6}>
          <Field
            label="Số giấy tờ (CCCD/Hộ chiếu)"
            name="identityNumber"
            form={form}
          />
        </Col>
      </Row>
    </Form>
  );
}
