import { Alert, Button, Form, Modal } from "react-bootstrap";

export default function PolicyFormModal({
  show,
  saving,
  editingPolicy,
  form,
  error,
  onClose,
  onSubmit,
  onChange,
}) {
  const isEditing = Boolean(editingPolicy);

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
            {isEditing ? "Cập nhật chính sách" : "Tạo chính sách"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          <Form.Group className="mb-3">
            <Form.Label>Loại chính sách</Form.Label>

            <Form.Select
              name="type"
              value={form.type}
              onChange={onChange}
              disabled={isEditing || saving}
            >
              <option value="">-- Chọn loại chính sách --</option>

              <option value="BOOKING">Đăng ký / giảm giá</option>

              <option value="CANCEL">Hủy / hoàn tiền</option>
            </Form.Select>

            {isEditing && (
              <Form.Text className="text-muted">
                Không thể thay đổi loại của chính sách sau khi đã tạo.
              </Form.Text>
            )}
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Tiêu đề</Form.Label>

            <Form.Control
              type="text"
              name="title"
              value={form.title}
              onChange={onChange}
              placeholder="Nhập tiêu đề chính sách..."
              maxLength={200}
              disabled={saving}
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Nội dung</Form.Label>

            <Form.Control
              as="textarea"
              rows={7}
              name="content"
              value={form.content}
              onChange={onChange}
              placeholder="Nhập nội dung chính sách..."
              disabled={saving}
            />
          </Form.Group>

          {isEditing && (
            <Form.Group>
              <Form.Label>Trạng thái</Form.Label>

              <Form.Select
                name="status"
                value={form.status}
                onChange={onChange}
                disabled={saving}
              >
                <option value="ACTIVE">Đang hoạt động</option>

                <option value="INACTIVE">Ngừng hoạt động</option>
              </Form.Select>
            </Form.Group>
          )}
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={onClose} disabled={saving}>
            Hủy
          </Button>

          <Button type="submit" variant="primary" disabled={saving}>
            {saving
              ? "Đang lưu..."
              : isEditing
                ? "Lưu thay đổi"
                : "Tạo chính sách"}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
