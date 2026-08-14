import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

export default function CruiseAreaFormModal({
  show,
  saving,
  editingArea,
  form,
  error,
  onClose,
  onSubmit,
  onChange,
}) {
  const isEditing = Boolean(editingArea);

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
            {isEditing ? "Chỉnh sửa khu vực" : "Thêm khu vực"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          {/* NAME */}
          <Form.Group className="mb-3">
            <Form.Label>Tên khu vực</Form.Label>

            <Form.Control
              type="text"
              name="name"
              value={form.name}
              onChange={onChange}
              placeholder="Ví dụ: Khu vực A, Boong tàu, Ban công, Sân thượng, Sảnh..."
              maxLength={150}
              disabled={saving}
            />
          </Form.Group>

          {/* DESCRIPTION */}
          <Form.Group className="mb-3">
            <Form.Label>Mô tả</Form.Label>

            <Form.Control
              as="textarea"
              rows={4}
              name="description"
              value={form.description}
              onChange={onChange}
              placeholder="Nhập mô tả vị trí/khu vực..."
              maxLength={2000}
              disabled={saving}
            />
          </Form.Group>

          {/* IMAGE */}
          <Form.Group className="mb-3">
            <Form.Label>Hình ảnh</Form.Label>

            <Form.Control
              type="file"
              name="image"
              accept="image/*"
              onChange={onChange}
              disabled={saving}
            />

            {isEditing && editingArea.imageUrl && (
              <div className="mt-3">
                <p className="text-muted mb-2">Hình ảnh hiện tại:</p>

                <img
                  src={editingArea.imageUrl}
                  alt={editingArea.name}
                  style={{
                    width: "180px",
                    height: "120px",
                    objectFit: "cover",
                    borderRadius: "8px",
                  }}
                />
              </div>
            )}
          </Form.Group>

          {/* STATUS */}
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
                <Spinner animation="border" size="sm" className="me-2" />
                Đang lưu...
              </>
            ) : isEditing ? (
              "Lưu thay đổi"
            ) : (
              "Thêm khu vực"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
