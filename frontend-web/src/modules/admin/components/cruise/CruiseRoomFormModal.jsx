import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

export default function CruiseRoomFormModal({
  show,
  saving,
  editingRoom,
  form,
  roomTypes,
  error,
  onClose,
  onSubmit,
  onChange,
}) {
  const isEditing = Boolean(editingRoom);

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      backdrop={saving ? "static" : true}
      keyboard={!saving}
    >
      <Form onSubmit={onSubmit}>
        <Modal.Header closeButton={!saving}>
          <Modal.Title>
            {isEditing ? "Chỉnh sửa phòng" : "Thêm phòng"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          <Form.Group className="mb-3">
            <Form.Label>
              Mã phòng <span className="text-danger">*</span>
            </Form.Label>

            <Form.Control
              type="text"
              name="code"
              value={form.code}
              onChange={onChange}
              placeholder="Ví dụ: 101, A101, DELUXE-01"
              disabled={saving}
              maxLength={50}
              required
            />

            <Form.Text className="text-muted">
              Chỉ sử dụng chữ cái, số, dấu gạch ngang hoặc gạch dưới.
            </Form.Text>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>
              Loại phòng <span className="text-danger">*</span>
            </Form.Label>

            <Form.Select
              name="roomTypeId"
              value={form.roomTypeId}
              onChange={onChange}
              disabled={saving}
              required
            >
              <option value="">-- Chọn loại phòng --</option>

              {roomTypes?.map((roomType) => (
                <option key={roomType.id} value={roomType.id}>
                  {roomType.name}
                </option>
              ))}
            </Form.Select>
          </Form.Group>

          {isEditing && (
            <Form.Group className="mb-3">
              <Form.Label>Trạng thái</Form.Label>

              <Form.Select
                name="status"
                value={form.status}
                onChange={onChange}
                disabled={saving}
              >
                <option value="ACTIVE">Hoạt động</option>

                <option value="INACTIVE">Không hoạt động</option>
              </Form.Select>
            </Form.Group>
          )}
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={onClose} disabled={saving}>
            Hủy
          </Button>

          <Button variant="primary" type="submit" disabled={saving}>
            {saving && (
              <Spinner size="sm" animation="border" className="me-2" />
            )}

            {isEditing ? "Lưu thay đổi" : "Thêm phòng"}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
