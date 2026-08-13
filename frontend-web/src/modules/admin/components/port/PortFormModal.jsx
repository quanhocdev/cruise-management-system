import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

import PortMap from "./PortMap";

export default function PortFormModal({
  show,
  saving,
  editingPort,
  form,
  error,

  onClose,
  onSubmit,
  onChange,
  onLocationChange,
}) {
  return (
    <Modal show={show} onHide={onClose} size="xl" centered>
      <Modal.Header closeButton>
        <Modal.Title>{editingPort ? "Cập nhật cảng" : "Tạo cảng"}</Modal.Title>
      </Modal.Header>

      <Form onSubmit={onSubmit}>
        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          <Form.Group className="mb-3">
            <Form.Label>
              Tên cảng <span className="text-danger">*</span>
            </Form.Label>

            <Form.Control
              type="text"
              name="name"
              value={form.name}
              onChange={onChange}
              placeholder="Ví dụ: Cảng Cát Lái"
              maxLength={150}
              required
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>
              Vị trí cảng <span className="text-danger">*</span>
            </Form.Label>

            <PortMap
              latitude={form.latitude !== "" ? Number(form.latitude) : null}
              longitude={form.longitude !== "" ? Number(form.longitude) : null}
              onLocationChange={onLocationChange}
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Mô tả</Form.Label>

            <Form.Control
              as="textarea"
              rows={4}
              name="description"
              value={form.description}
              onChange={onChange}
              placeholder="Mô tả về cảng..."
              maxLength={1000}
            />
          </Form.Group>

          {editingPort && (
            <Form.Group className="mb-3">
              <Form.Label>Trạng thái</Form.Label>

              <Form.Select
                name="status"
                value={form.status}
                onChange={onChange}
              >
                <option value="ACTIVE">ACTIVE</option>

                <option value="INACTIVE">INACTIVE</option>
              </Form.Select>
            </Form.Group>
          )}

          <Alert variant="light" className="mb-0">
            <strong>Lưu ý:</strong> Địa chỉ, thành phố và quốc gia sẽ được hệ
            thống tự động xác định từ tọa độ thông qua Nominatim.
          </Alert>
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
            ) : editingPort ? (
              "Cập nhật"
            ) : (
              "Tạo cảng"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
