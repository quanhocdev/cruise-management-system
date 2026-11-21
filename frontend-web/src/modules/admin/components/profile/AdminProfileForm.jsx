import { Button, Card, Col, Form, Row, Spinner } from "react-bootstrap";

const GENDERS = [
  { value: "MALE", label: "Nam" },
  { value: "FEMALE", label: "Nữ" },
  { value: "OTHER", label: "Khác" },
];

function ProfileField({
  label,
  name,
  value,
  onChange,
  type = "text",
  as,
  options,
  rows,
}) {
  return (
    <Form.Group as={Col} md={6} className="mb-3">
      <Form.Label>{label}</Form.Label>

      {as === "select" ? (
        <Form.Select name={name} value={value} onChange={onChange}>
          <option value="">Chưa cập nhật</option>

          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </Form.Select>
      ) : (
        <Form.Control
          type={type}
          as={as}
          rows={rows}
          name={name}
          value={value}
          onChange={onChange}
          placeholder="Chưa cập nhật"
        />
      )}
    </Form.Group>
  );
}

export default function AdminProfileForm({
  form,
  hasProfile,
  saving,
  deleting,
  onChange,
  onSubmit,
  onDelete,
}) {
  return (
    <Card className="admin-profile-card">
      <Card.Header>
        <div className="d-flex justify-content-between align-items-center">
          <h5>Thông tin cá nhân</h5>

          <span
            className={`badge ${hasProfile ? "bg-success" : "bg-secondary"}`}
          >
            {hasProfile ? "Đã có hồ sơ" : "Chưa có hồ sơ"}
          </span>
        </div>
      </Card.Header>

      <Card.Body className="p-4">
        <Form onSubmit={onSubmit}>
          <Row>
            <ProfileField
              label="Họ và tên"
              name="fullName"
              value={form.fullName}
              onChange={onChange}
            />

            <ProfileField
              label="Ngày sinh"
              name="dateOfBirth"
              type="date"
              value={form.dateOfBirth}
              onChange={onChange}
            />

            <ProfileField
              label="Số điện thoại"
              name="phone"
              value={form.phone}
              onChange={onChange}
            />

            <ProfileField
              label="Giới tính"
              name="gender"
              as="select"
              options={GENDERS}
              value={form.gender}
              onChange={onChange}
            />

            <ProfileField
              label="Quốc tịch"
              name="nationality"
              value={form.nationality}
              onChange={onChange}
            />

            <ProfileField
              label="Số giấy tờ tùy thân"
              name="identityNumber"
              value={form.identityNumber}
              onChange={onChange}
            />

            <ProfileField
              label="Địa chỉ"
              name="address"
              as="textarea"
              rows={3}
              value={form.address}
              onChange={onChange}
            />
          </Row>

          <div className="admin-profile-form-actions">
            {hasProfile && (
              <Button
                type="button"
                variant="outline-danger"
                disabled={saving || deleting}
                onClick={onDelete}
              >
                {deleting ? (
                  <Spinner size="sm" className="me-2" />
                ) : (
                  <i className="bi bi-trash me-2" />
                )}
                Xóa hồ sơ
              </Button>
            )}

            <Button
              type="submit"
              variant="primary"
              disabled={saving || deleting}
            >
              {saving ? (
                <Spinner size="sm" className="me-2" />
              ) : (
                <i className="bi bi-check-lg me-2" />
              )}
              {hasProfile ? "Cập nhật hồ sơ" : "Tạo hồ sơ"}
            </Button>
          </div>
        </Form>
      </Card.Body>
    </Card>
  );
}
