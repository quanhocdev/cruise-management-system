import { Button, Col, Form, Row, Spinner } from "react-bootstrap";

const GENDERS = [
  { value: "MALE", label: "Nam" },
  { value: "FEMALE", label: "Nữ" },
  { value: "OTHER", label: "Khác" },
];

function Field({ label, name, form, onChange, type = "text", readOnly }) {
  return (
    <Form.Group className="mb-3" controlId={`pf-${name}`}>
      <Form.Label>{label}</Form.Label>
      <Form.Control
        type={type}
        name={name}
        value={form[name] || ""}
        onChange={onChange}
        readOnly={readOnly}
      />
    </Form.Group>
  );
}

export default function ProfileForm({ formState, readOnly = false }) {
  const { form, saving, handleChange, handleSubmit } = formState;

  return (
    <Form onSubmit={readOnly ? (e) => e.preventDefault() : handleSubmit}>
      <Row>
        <Col md={8}>
          <Field
            label="Họ và tên"
            name="fullName"
            form={form}
            onChange={handleChange}
            readOnly={readOnly}
          />
        </Col>

        <Col md={4}>
          <Field
            label="Ngày sinh"
            name="dateOfBirth"
            type="date"
            form={form}
            onChange={handleChange}
            readOnly={readOnly}
          />
        </Col>
      </Row>

      <Row>
        <Col md={6}>
          <Field
            label="Số điện thoại"
            name="phone"
            form={form}
            onChange={handleChange}
            readOnly={readOnly}
          />
        </Col>

        <Col md={6}>
          <Form.Group className="mb-3" controlId="pf-gender">
            <Form.Label>Giới tính</Form.Label>
            <Form.Select
              name="gender"
              value={form.gender || ""}
              onChange={handleChange}
              disabled={readOnly}
            >
              <option value="">Chưa cập nhật</option>
              {GENDERS.map((gender) => (
                <option key={gender.value} value={gender.value}>
                  {gender.label}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
        </Col>
      </Row>

      <Form.Group className="mb-3" controlId="pf-address">
        <Form.Label>Địa chỉ</Form.Label>
        <Form.Control
          as="textarea"
          rows={3}
          name="address"
          value={form.address || ""}
          onChange={handleChange}
          readOnly={readOnly}
        />
      </Form.Group>

      <Row>
        <Col md={6}>
          <Field
            label="Quốc tịch"
            name="nationality"
            form={form}
            onChange={handleChange}
            readOnly={readOnly}
          />
        </Col>

        <Col md={6}>
          <Field
            label="Số giấy tờ (CCCD/Hộ chiếu)"
            name="identityNumber"
            form={form}
            onChange={handleChange}
            readOnly={readOnly}
          />
        </Col>
      </Row>

      {!readOnly && (
        <div className="d-flex justify-content-end mt-3">
          <Button type="submit" variant="primary" disabled={saving}>
            {saving ? (
              <>
                <Spinner size="sm" className="me-2" />
                Đang lưu...
              </>
            ) : (
              "Lưu hồ sơ"
            )}
          </Button>
        </div>
      )}
    </Form>
  );
}
