import { useEffect, useState } from "react";
import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

export default function ServiceFormModal({
  show,
  saving,
  editingService,
  error,
  onClose,
  onSubmit,
}) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [durationMinutes, setDurationMinutes] = useState("");
  const [maxPassengers, setMaxPassengers] = useState("");
  const [status, setStatus] = useState("ACTIVE");
  const [image, setImage] = useState(null);

  useEffect(() => {
    if (!show) {
      return;
    }

    if (editingService) {
      setName(editingService.name || "");
      setDescription(editingService.description || "");
      setPrice(editingService.price ?? "");
      setDurationMinutes(editingService.durationMinutes ?? "");
      setMaxPassengers(editingService.maxPassengers ?? "");
      setStatus(editingService.status || "ACTIVE");
      setImage(null);
    } else {
      setName("");
      setDescription("");
      setPrice("");
      setDurationMinutes("");
      setMaxPassengers("");
      setStatus("ACTIVE");
      setImage(null);
    }
  }, [show, editingService]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formData = new FormData();

    formData.append("name", name);
    formData.append("description", description);
    formData.append("price", price);
    formData.append("durationMinutes", durationMinutes);
    formData.append("maxPassengers", maxPassengers);

    if (editingService) {
      formData.append("status", status);
    }

    if (image) {
      formData.append("image", image);
    }

    await onSubmit(formData);
  };

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      size="lg"
      backdrop={saving ? "static" : true}
      keyboard={!saving}
    >
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton={!saving}>
          <Modal.Title>
            {editingService ? "Chỉnh sửa dịch vụ" : "Tạo dịch vụ"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          {/* NAME */}
          <Form.Group className="mb-3">
            <Form.Label>
              Tên dịch vụ <span className="text-danger">*</span>
            </Form.Label>

            <Form.Control
              type="text"
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="Nhập tên dịch vụ"
              maxLength={150}
              required
              disabled={saving}
            />
          </Form.Group>

          {/* DESCRIPTION */}
          <Form.Group className="mb-3">
            <Form.Label>Mô tả</Form.Label>

            <Form.Control
              as="textarea"
              rows={4}
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              placeholder="Nhập mô tả dịch vụ"
              maxLength={5000}
              disabled={saving}
            />
          </Form.Group>

          {/* PRICE */}
          <Form.Group className="mb-3">
            <Form.Label>
              Giá <span className="text-danger">*</span>
            </Form.Label>

            <Form.Control
              type="number"
              min="0"
              step="0.01"
              value={price}
              onChange={(event) => setPrice(event.target.value)}
              placeholder="Nhập giá dịch vụ"
              required
              disabled={saving}
            />
          </Form.Group>

          <div className="row">
            {/* DURATION */}
            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>
                  Thời lượng (phút) <span className="text-danger">*</span>
                </Form.Label>

                <Form.Control
                  type="number"
                  min="1"
                  value={durationMinutes}
                  onChange={(event) => setDurationMinutes(event.target.value)}
                  placeholder="Ví dụ: 60"
                  required
                  disabled={saving}
                />
              </Form.Group>
            </div>

            {/* MAX PASSENGERS */}
            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>
                  Số khách tối đa <span className="text-danger">*</span>
                </Form.Label>

                <Form.Control
                  type="number"
                  min="1"
                  value={maxPassengers}
                  onChange={(event) => setMaxPassengers(event.target.value)}
                  placeholder="Ví dụ: 10"
                  required
                  disabled={saving}
                />
              </Form.Group>
            </div>
          </div>

          {/* STATUS */}
          {editingService && (
            <Form.Group className="mb-3">
              <Form.Label>Trạng thái</Form.Label>

              <Form.Select
                value={status}
                onChange={(event) => setStatus(event.target.value)}
                disabled={saving}
              >
                <option value="ACTIVE">Đang hoạt động</option>

                <option value="INACTIVE">Ngừng hoạt động</option>
              </Form.Select>
            </Form.Group>
          )}

          {/* IMAGE */}
          <Form.Group className="mb-3">
            <Form.Label>Hình ảnh</Form.Label>

            <Form.Control
              type="file"
              accept="image/*"
              onChange={(event) => setImage(event.target.files?.[0] || null)}
              disabled={saving}
            />

            {editingService?.imageUrl && !image && (
              <div className="service-form-current-image">
                <small className="text-muted">Hình ảnh hiện tại:</small>

                <img
                  src={editingService.imageUrl}
                  alt={editingService.name}
                  className="service-form-image-preview"
                />
              </div>
            )}
          </Form.Group>
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
            ) : editingService ? (
              "Cập nhật"
            ) : (
              "Tạo dịch vụ"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
