// src/modules/admin/components/policy/RefundRuleFormModal.jsx

import { useEffect, useState } from "react";
import { Alert, Button, Form, Modal, Spinner } from "react-bootstrap";

import { hasOverlapWith } from "../../utils/refundRuleUtils";

const DESCRIPTION_MAX = 500;

export default function RefundRuleFormModal({
  show,
  saving,
  editingRule,
  existingRules,
  error,
  onClose,
  onSubmit,
}) {
  const [minDays, setMinDays] = useState("");
  const [maxDays, setMaxDays] = useState("");
  const [percentage, setPercentage] = useState("");
  const [description, setDescription] = useState("");

  const [validationError, setValidationError] = useState("");

  useEffect(() => {
    if (!show) {
      return;
    }

    setValidationError("");

    if (editingRule) {
      setMinDays(editingRule.minDaysBeforeDeparture ?? "");
      setMaxDays(editingRule.maxDaysBeforeDeparture ?? "");
      setPercentage(editingRule.refundPercentage ?? "");
      setDescription(editingRule.description || "");
    } else {
      setMinDays("");
      setMaxDays("");
      setPercentage("");
      setDescription("");
    }
  }, [show, editingRule]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    const min = Number(minDays);
    const max = maxDays === "" ? null : Number(maxDays);

    if (max !== null && max < min) {
      setValidationError(
        "Số ngày tối đa phải lớn hơn hoặc bằng số ngày tối thiểu.",
      );
      return;
    }

    const candidate = {
      minDaysBeforeDeparture: min,
      maxDaysBeforeDeparture: max,
    };

    if (hasOverlapWith(existingRules, candidate, editingRule?.id)) {
      setValidationError(
        "Khoảng ngày bị chồng lấn với một luật đã có. Hãy điều chỉnh lại.",
      );
      return;
    }

    setValidationError("");

    // PATCH nhưng request là bản ghi đầy đủ nên luôn gửi toàn bộ trường
    await onSubmit({
      ...candidate,
      refundPercentage: Number(percentage),
      description: description.trim() || null,
    });
  };

  const displayError = validationError || error;

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
            {editingRule ? "Chỉnh sửa luật hoàn tiền" : "Thêm luật hoàn tiền"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {displayError && <Alert variant="danger">{displayError}</Alert>}

          <div className="refund-rule-form-row">
            {/* MIN */}
            <Form.Group className="mb-3">
              <Form.Label>
                Từ (ngày) <span className="text-danger">*</span>
              </Form.Label>

              <Form.Control
                type="number"
                min="0"
                step="1"
                value={minDays}
                onChange={(event) => setMinDays(event.target.value)}
                placeholder="Ví dụ: 7"
                required
                disabled={saving}
              />
            </Form.Group>

            {/* MAX */}
            <Form.Group className="mb-3">
              <Form.Label>Đến (ngày)</Form.Label>

              <Form.Control
                type="number"
                min="0"
                step="1"
                value={maxDays}
                onChange={(event) => setMaxDays(event.target.value)}
                placeholder="Để trống = không giới hạn"
                disabled={saving}
              />
            </Form.Group>
          </div>

          <Form.Text className="text-muted d-block mb-3">
            Khoảng số ngày trước ngày khởi hành. Để trống ô "Đến" nếu luật áp
            dụng từ mốc tối thiểu trở lên.
          </Form.Text>

          {/* PERCENT */}
          <Form.Group className="mb-3">
            <Form.Label>
              Phần trăm hoàn tiền <span className="text-danger">*</span>
            </Form.Label>

            <div className="input-group">
              <Form.Control
                type="number"
                min="0"
                max="100"
                step="0.01"
                value={percentage}
                onChange={(event) => setPercentage(event.target.value)}
                placeholder="Ví dụ: 80"
                required
                disabled={saving}
              />

              <span className="input-group-text">%</span>
            </div>
          </Form.Group>

          {/* DESCRIPTION */}
          <Form.Group className="mb-3">
            <Form.Label>Mô tả</Form.Label>

            <Form.Control
              as="textarea"
              rows={3}
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              placeholder="Ví dụ: Hủy trước 7 đến 14 ngày hoàn 80%"
              maxLength={DESCRIPTION_MAX}
              disabled={saving}
            />

            <Form.Text className="text-muted">
              {description.length}/{DESCRIPTION_MAX}
            </Form.Text>
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
            ) : editingRule ? (
              "Cập nhật"
            ) : (
              "Thêm luật"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
