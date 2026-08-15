// src/modules/admin/components/policy/BookingPolicyFormModal.jsx

import { useEffect, useState } from "react";
import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

export default function BookingPolicyFormModal({
  show,
  saving,
  editingRule,
  error,
  onClose,
  onSubmit,
}) {
  const [daysBeforeDeparture, setDaysBeforeDeparture] = useState("");

  const [discountPercent, setDiscountPercent] = useState("");

  const [status, setStatus] = useState("ACTIVE");

  useEffect(() => {
    if (!show) {
      return;
    }

    if (editingRule) {
      setDaysBeforeDeparture(editingRule.daysBeforeDeparture ?? "");

      setDiscountPercent(editingRule.discountPercent ?? "");

      setStatus(editingRule.status || "ACTIVE");
    } else {
      setDaysBeforeDeparture("");
      setDiscountPercent("");
      setStatus("ACTIVE");
    }
  }, [show, editingRule]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    const data = editingRule
      ? {
          daysBeforeDeparture: Number(daysBeforeDeparture),
          discountPercent: Number(discountPercent),
          status,
        }
      : {
          daysBeforeDeparture: Number(daysBeforeDeparture),
          discountPercent: Number(discountPercent),
        };

    await onSubmit(data);
  };

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      backdrop={saving ? "static" : true}
      keyboard={!saving}
    >
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton={!saving}>
          <Modal.Title>
            {editingRule
              ? "Chỉnh sửa quy tắc đặt tour"
              : "Thêm quy tắc đặt tour"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          {/* DAYS */}
          <Form.Group className="mb-3">
            <Form.Label>
              Số ngày trước khởi hành <span className="text-danger">*</span>
            </Form.Label>

            <Form.Control
              type="number"
              min="0"
              step="1"
              value={daysBeforeDeparture}
              onChange={(event) => setDaysBeforeDeparture(event.target.value)}
              placeholder="Ví dụ: 30"
              required
              disabled={saving}
            />

            <Form.Text className="text-muted">
              Số ngày tối thiểu trước ngày khởi hành để áp dụng mức giảm giá.
            </Form.Text>
          </Form.Group>

          {/* DISCOUNT */}
          <Form.Group className="mb-3">
            <Form.Label>
              Phần trăm giảm giá <span className="text-danger">*</span>
            </Form.Label>

            <div className="input-group">
              <Form.Control
                type="number"
                min="0"
                max="100"
                step="0.01"
                value={discountPercent}
                onChange={(event) => setDiscountPercent(event.target.value)}
                placeholder="Ví dụ: 20"
                required
                disabled={saving}
              />

              <span className="input-group-text">%</span>
            </div>
          </Form.Group>

          {/* STATUS */}
          {editingRule && (
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
            ) : editingRule ? (
              "Cập nhật"
            ) : (
              "Thêm quy tắc"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
