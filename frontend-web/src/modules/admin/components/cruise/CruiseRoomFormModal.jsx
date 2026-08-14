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
        {/* =================================================
            HEADER
           ================================================= */}

        <Modal.Header closeButton={!saving}>
          <Modal.Title>
            {isEditing ? "Chỉnh sửa phòng" : "Thêm phòng"}
          </Modal.Title>
        </Modal.Header>

        {/* =================================================
            BODY
           ================================================= */}

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          {/* =================================================
              CREATE
             ================================================= */}

          {!isEditing && (
            <>
              {/* ROOM TYPE */}

              <Form.Group className="mb-3">
                <Form.Label>Loại phòng</Form.Label>

                <Form.Select
                  name="roomTypeId"
                  value={form.roomTypeId}
                  onChange={onChange}
                  disabled={saving}
                >
                  <option value="">-- Chọn loại phòng --</option>

                  {roomTypes?.map((roomType) => (
                    <option key={roomType.id} value={roomType.id}>
                      {roomType.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>

              {/* QUANTITY */}

              <Form.Group className="mb-3">
                <Form.Label>Số lượng phòng</Form.Label>

                <Form.Control
                  type="number"
                  name="quantity"
                  value={form.quantity}
                  onChange={onChange}
                  min={1}
                  max={1000}
                  placeholder="Ví dụ: 10"
                  disabled={saving}
                />

                <Form.Text className="text-muted">
                  Hệ thống sẽ tự động đánh số phòng tiếp theo trên tầng.
                </Form.Text>
              </Form.Group>
            </>
          )}

          {/* =================================================
              EDIT
             ================================================= */}

          {isEditing && (
            <>
              {/* CODE */}

              <Form.Group className="mb-3">
                <Form.Label>Mã phòng</Form.Label>

                <Form.Control
                  type="text"
                  name="code"
                  value={form.code}
                  onChange={onChange}
                  maxLength={50}
                  disabled={saving}
                />
              </Form.Group>

              {/* ROOM TYPE */}

              <Form.Group className="mb-3">
                <Form.Label>Loại phòng</Form.Label>

                <Form.Select
                  name="roomTypeId"
                  value={form.roomTypeId}
                  onChange={onChange}
                  disabled={saving}
                >
                  <option value="">-- Chọn loại phòng --</option>

                  {roomTypes?.map((roomType) => (
                    <option key={roomType.id} value={roomType.id}>
                      {roomType.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>

              {/* STATUS */}

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
            </>
          )}
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
              "Tạo phòng"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
