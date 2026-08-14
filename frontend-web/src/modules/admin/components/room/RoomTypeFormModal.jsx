import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

export default function RoomTypeFormModal({
  show,
  saving,
  editingRoomType,
  form,
  error,
  onClose,
  onSubmit,
  onChange,
}) {
  const isEditing = Boolean(editingRoomType);

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      backdrop={saving ? "static" : true}
      keyboard={!saving}
    >
      <Form onSubmit={onSubmit}>
        {/* =================================================
            HEADER
           ================================================= */}

        <Modal.Header closeButton={!saving}>
          <Modal.Title>
            {isEditing ? "Chỉnh sửa loại phòng" : "Thêm loại phòng"}
          </Modal.Title>
        </Modal.Header>

        {/* =================================================
            BODY
           ================================================= */}

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          {/* NAME */}

          <Form.Group className="mb-3">
            <Form.Label>Tên loại phòng</Form.Label>

            <Form.Control
              type="text"
              name="name"
              value={form.name}
              onChange={onChange}
              placeholder="Ví dụ: Standard, Deluxe, Suite..."
              maxLength={100}
              disabled={saving}
              autoFocus
            />

            <Form.Text className="text-muted">
              Tên loại phòng không được trùng.
            </Form.Text>
          </Form.Group>

          {/* DESCRIPTION */}

          <Form.Group className="mb-3">
            <Form.Label>Mô tả</Form.Label>

            <Form.Control
              as="textarea"
              rows={5}
              name="description"
              value={form.description}
              onChange={onChange}
              placeholder="Nhập mô tả loại phòng..."
              maxLength={5000}
              disabled={saving}
            />

            <Form.Text className="text-muted">Tối đa 5000 ký tự.</Form.Text>
          </Form.Group>
        </Modal.Body>

        {/* =================================================
            FOOTER
           ================================================= */}

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
              "Thêm loại phòng"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
