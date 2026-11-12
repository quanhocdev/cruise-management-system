// src/modules/admin/components/policy/PolicyRuleModal.jsx

import { useEffect, useState } from "react";
import { Alert, Button, Modal, Spinner } from "react-bootstrap";

import BookingPolicyTable from "./BookingPolicyTable";
import BookingPolicyFormModal from "./BookingPolicyFormModal";
import RefundRulesPanel from "./RefundRulesPanel";

export default function PolicyRuleModal({
  show,
  policy,
  loading,
  saving,
  error,
  bookingRules,
  onClose,
  onCreateBookingRule,
  onUpdateBookingRule,
  onDeleteBookingRule,
}) {
  const [showForm, setShowForm] = useState(false);
  const [editingRule, setEditingRule] = useState(null);
  const [refundSaving, setRefundSaving] = useState(false);

  useEffect(() => {
    if (!show) {
      setShowForm(false);
      setEditingRule(null);
      setRefundSaving(false);
    }
  }, [show]);

  if (!policy) {
    return null;
  }

  const isBooking = policy.type === "BOOKING";
  const busy = isBooking ? saving : refundSaving;

  const handleOpenCreate = () => {
    setEditingRule(null);
    setShowForm(true);
  };

  const handleOpenEdit = (rule) => {
    setEditingRule(rule);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    if (saving) {
      return;
    }

    setShowForm(false);
    setEditingRule(null);
  };

  const handleSubmit = async (data) => {
    const result = editingRule
      ? await onUpdateBookingRule(policy.id, editingRule.id, data)
      : await onCreateBookingRule(policy.id, data);

    if (result) {
      setShowForm(false);
      setEditingRule(null);
    }
  };

  const handleDelete = async (rule) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa quy tắc giảm giá ${rule.discountPercent}% ` +
        `cho mốc ${rule.daysBeforeDeparture} ngày trước khởi hành không?`,
    );

    if (!confirmed) {
      return;
    }

    await onDeleteBookingRule(policy.id, rule.id);
  };

  return (
    <>
      <Modal
        show={show}
        onHide={onClose}
        centered
        size="xl"
        backdrop={busy ? "static" : true}
        keyboard={!busy}
      >
        <Modal.Header closeButton={!busy}>
          <div>
            <Modal.Title>Quản lý quy tắc</Modal.Title>

            <div className="text-muted mt-1">
              {isBooking ? "Chính sách đặt tour" : "Chính sách hủy tour"} —{" "}
              <strong>{policy.title}</strong>
            </div>
          </div>
        </Modal.Header>

        <Modal.Body>
          {isBooking ? (
            <>
              {error && <Alert variant="danger">{error}</Alert>}

              <div className="policy-rule-header mb-3">
                <div>
                  <h5 className="mb-1">Quy tắc giảm giá</h5>

                  <p className="text-muted mb-0">
                    Thiết lập mức giảm giá dựa trên số ngày trước khi khởi hành.
                  </p>
                </div>

                <Button
                  variant="primary"
                  onClick={handleOpenCreate}
                  disabled={loading || saving}
                >
                  + Thêm quy tắc
                </Button>
              </div>

              {loading ? (
                <div className="policy-rule-loading">
                  <Spinner animation="border" />
                  <span>Đang tải quy tắc...</span>
                </div>
              ) : (
                <BookingPolicyTable
                  rules={bookingRules}
                  loading={false}
                  onEdit={handleOpenEdit}
                  onDelete={handleDelete}
                />
              )}
            </>
          ) : (
            <RefundRulesPanel
              policyId={policy.id}
              onSavingChange={setRefundSaving}
            />
          )}
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={onClose} disabled={busy}>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {isBooking && (
        <BookingPolicyFormModal
          show={showForm}
          saving={saving}
          editingRule={editingRule}
          error={error}
          onClose={handleCloseForm}
          onSubmit={handleSubmit}
        />
      )}
    </>
  );
}
