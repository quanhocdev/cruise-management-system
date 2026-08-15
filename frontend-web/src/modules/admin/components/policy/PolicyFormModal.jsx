// src/modules/admin/components/policy/PolicyFormModal.jsx

import { useEffect, useState } from "react";
import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

export default function PolicyFormModal({
  show,
  saving,
  editingPolicy,
  error,
  onClose,
  onSubmit,
}) {
  const [type, setType] = useState("BOOKING");
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [status, setStatus] = useState("ACTIVE");

  useEffect(() => {
    if (!show) {
      return;
    }

    if (editingPolicy) {
      setType(editingPolicy.type || "BOOKING");
      setTitle(editingPolicy.title || "");
      setContent(editingPolicy.content || "");
      setStatus(editingPolicy.status || "ACTIVE");
    } else {
      setType("BOOKING");
      setTitle("");
      setContent("");
      setStatus("ACTIVE");
    }
  }, [show, editingPolicy]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    const data = editingPolicy
      ? {
          title,
          content,
          status,
        }
      : {
          type,
          title,
          content,
        };

    await onSubmit(data);
  };

  const getTypeLabel = (value) => {
    if (value === "BOOKING") {
      return "Chính sách đặt tour";
    }

    if (value === "CANCEL") {
      return "Chính sách hủy tour";
    }

    return value;
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
            {editingPolicy ? "Chỉnh sửa chính sách" : "Tạo chính sách"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          {/* TYPE */}
          <Form.Group className="mb-3">
            <Form.Label>
              Loại chính sách <span className="text-danger">*</span>
            </Form.Label>

            {editingPolicy ? (
              <Form.Control value={getTypeLabel(type)} disabled />
            ) : (
              <Form.Select
                value={type}
                onChange={(event) => setType(event.target.value)}
                disabled={saving}
                required
              >
                <option value="BOOKING">Chính sách đặt tour</option>

                <option value="CANCEL">Chính sách hủy tour</option>
              </Form.Select>
            )}
          </Form.Group>

          {/* TITLE */}
          <Form.Group className="mb-3">
            <Form.Label>
              Tiêu đề <span className="text-danger">*</span>
            </Form.Label>

            <Form.Control
              type="text"
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              placeholder="Nhập tiêu đề chính sách"
              maxLength={200}
              required
              disabled={saving}
            />
          </Form.Group>

          {/* CONTENT */}
          <Form.Group className="mb-3">
            <Form.Label>
              Nội dung <span className="text-danger">*</span>
            </Form.Label>

            <Form.Control
              as="textarea"
              rows={8}
              value={content}
              onChange={(event) => setContent(event.target.value)}
              placeholder="Nhập nội dung chính sách"
              required
              disabled={saving}
            />
          </Form.Group>

          {/* STATUS - EDIT ONLY */}
          {editingPolicy && (
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
            ) : editingPolicy ? (
              "Cập nhật"
            ) : (
              "Tạo chính sách"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
