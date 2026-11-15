// src/modules/profile/components/ProfileForm.jsx
import { Alert, Button, Col, Form, Row, Spinner } from "react-bootstrap";

const GENDERS = [
  { value: "MALE", label: "Nam" },
  { value: "FEMALE", label: "Nữ" },
  { value: "OTHER", label: "Khác" },
];

function Field({
  label,
  name,
  form,
  errors,
  setField,
  type = "text",
  as,
  children,
  ...rest
}) {
  return (
    <Form.Group className="mb-3" controlId={`pf-${name}`}>
      <Form.Label>{label}</Form.Label>
      <Form.Control
        as={as}
        type={type}
        value={form[name]}
        isInvalid={!!errors[name]}
        onChange={(e) => setField(name, e.target.value)}
        {...rest}
      >
        {children}
      </Form.Control>
      <Form.Control.Feedback type="invalid">
        {errors[name]}
      </Form.Control.Feedback>
    </Form.Group>
  );
}

export default function ProfileForm({ formState }) {
  const { form, errors, saving, message, dirty, setField, reset, submit } =
    formState;
  const shared = { form, errors, setField };

  return (
    <Form onSubmit={submit} noValidate>
      {message && (
        <Alert variant={message.type} dismissible onClose={reset}>
          {message.text}
        </Alert>
      )}

      <Row>
        <Col md={8}>
          <Field
            label="Họ và tên"
            name="fullName"
            maxLength={150}
            {...shared}
          />
        </Col>
        <Col md={4}>
          <Field label="Ngày sinh" name="dateOfBirth" type="date" {...shared} />
        </Col>
      </Row>

      <Row>
        <Col md={6}>
          <Field
            label="Số điện thoại"
            name="phone"
            maxLength={20}
            {...shared}
          />
        </Col>
        <Col md={6}>
          <Field label="Giới tính" name="gender" as="select" {...shared}>
            <option value="">-- Chọn --</option>
            {GENDERS.map((g) => (
              <option key={g.value} value={g.value}>
                {g.label}
              </option>
            ))}
            {/* Giữ lại giá trị cũ nếu khác danh sách trên */}
            {form.gender && !GENDERS.some((g) => g.value === form.gender) && (
              <option value={form.gender}>{form.gender}</option>
            )}
          </Field>
        </Col>
      </Row>

      <Field label="Địa chỉ" name="address" maxLength={500} {...shared} />

      <Row>
        <Col md={6}>
          <Field
            label="Quốc tịch"
            name="nationality"
            maxLength={100}
            {...shared}
          />
        </Col>
        <Col md={6}>
          <Field
            label="Số giấy tờ (CCCD/Hộ chiếu)"
            name="identityNumber"
            maxLength={50}
            {...shared}
          />
        </Col>
      </Row>

      <div className="d-flex gap-2 justify-content-end">
        <Button
          variant="outline-secondary"
          onClick={reset}
          disabled={!dirty || saving}
        >
          Hoàn tác
        </Button>
        <Button type="submit" variant="primary" disabled={!dirty || saving}>
          {saving ? <Spinner size="sm" animation="border" /> : "Lưu thay đổi"}
        </Button>
      </div>
    </Form>
  );
}
