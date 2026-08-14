import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

export default function CruiseFormModal({
  show,
  saving,
  editingCruise,
  form,
  error,
  onClose,
  onSubmit,
  onChange,
}) {
  const isEditing = Boolean(editingCruise);

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      size="lg"
      backdrop={saving ? "static" : true}
      keyboard={!saving}
    >
      <Form onSubmit={onSubmit}>
        <Modal.Header closeButton={!saving}>
          <Modal.Title>
            {isEditing ? "Chỉnh sửa du thuyền" : "Tạo du thuyền"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          {/* =========================
              NAME
             ========================= */}
          <Form.Group className="mb-3">
            <Form.Label>Tên du thuyền</Form.Label>

            <Form.Control
              type="text"
              name="name"
              value={form.name}
              onChange={onChange}
              placeholder="Nhập tên du thuyền"
              maxLength={150}
              disabled={saving}
            />
          </Form.Group>

          {/* =========================
              CODE
             ========================= */}
          <Form.Group className="mb-3">
            <Form.Label>Mã du thuyền</Form.Label>

            <Form.Control
              type="text"
              name="code"
              value={form.code}
              onChange={onChange}
              placeholder="Ví dụ: CRUISE-001"
              maxLength={50}
              disabled={saving}
            />

            <Form.Text className="text-muted">
              Mã du thuyền phải là duy nhất.
            </Form.Text>
          </Form.Group>

          {/* =========================
              MAX PASSENGERS
             ========================= */}
          <Form.Group className="mb-3">
            <Form.Label>Sức chứa tối đa</Form.Label>

            <Form.Control
              type="number"
              name="maxPassengers"
              value={form.maxPassengers}
              onChange={onChange}
              placeholder="Nhập số hành khách tối đa"
              min="1"
              disabled={saving}
            />
          </Form.Group>

          {/* =========================
              DESCRIPTION
             ========================= */}
          <Form.Group className="mb-3">
            <Form.Label>Mô tả</Form.Label>

            <Form.Control
              as="textarea"
              rows={4}
              name="description"
              value={form.description}
              onChange={onChange}
              placeholder="Nhập mô tả du thuyền..."
              maxLength={5000}
              disabled={saving}
            />
          </Form.Group>

          {/* =========================
              IMAGE
             ========================= */}
          <Form.Group className="mb-3">
            <Form.Label>Hình ảnh</Form.Label>

            <Form.Control
              type="file"
              name="image"
              accept="image/*"
              onChange={onChange}
              disabled={saving}
            />

            <Form.Text className="text-muted">
              Chọn hình ảnh đại diện cho du thuyền.
            </Form.Text>
          </Form.Group>

          {/* =========================
              CURRENT IMAGE
             ========================= */}
          {isEditing && editingCruise.imageUrl && (
            <div className="mb-3">
              <Form.Label>Hình ảnh hiện tại</Form.Label>

              <div>
                <img
                  src={editingCruise.imageUrl}
                  alt={editingCruise.name}
                  className="cruise-form-preview"
                />
              </div>
            </div>
          )}

          {/* =========================
              STATUS
             ========================= */}
          {isEditing && (
            <Form.Group className="mb-3">
              <Form.Label>Trạng thái</Form.Label>

              <Form.Select
                name="status"
                value={form.status}
                onChange={onChange}
                disabled={saving}
              >
                <option value="ACTIVE">ACTIVE</option>

                <option value="INACTIVE">INACTIVE</option>
              </Form.Select>
            </Form.Group>
          )}
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={onClose} disabled={saving}>
            Hủy
          </Button>

          <Button variant="primary" type="submit" disabled={saving}>
            {saving ? (
              <>
                <Spinner size="sm" animation="border" className="me-2" />
                Đang lưu...
              </>
            ) : isEditing ? (
              "Lưu thay đổi"
            ) : (
              "Tạo du thuyền"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
