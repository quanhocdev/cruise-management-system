// src/modules/admin/components/policy/PolicyRuleModal.jsx

import { useEffect, useState } from "react";
import { Alert, Button, Modal, Spinner } from "react-bootstrap";

import BookingPolicyTable from "./BookingPolicyTable";
import BookingPolicyFormModal from "./BookingPolicyFormModal";

import CancelPolicyTable from "./CancelPolicyTable";
import CancelPolicyFormModal from "./CancelPolicyFormModal";

export default function PolicyRuleModal({
  show,
  policy,
  loading,
  saving,
  error,
  bookingRules,
  cancelRules,
  onClose,

  onCreateBookingRule,
  onUpdateBookingRule,
  onDeleteBookingRule,

  onCreateCancelRule,
  onUpdateCancelRule,
  onDeleteCancelRule,
}) {
  const [showForm, setShowForm] = useState(false);
  const [editingRule, setEditingRule] = useState(null);

  useEffect(() => {
    if (!show) {
      setShowForm(false);
      setEditingRule(null);
    }
  }, [show]);

  if (!policy) {
    return null;
  }

  const isBooking = policy.type === "BOOKING";

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
    let result;

    if (isBooking) {
      if (editingRule) {
        result = await onUpdateBookingRule(policy.id, editingRule.id, data);
      } else {
        result = await onCreateBookingRule(policy.id, data);
      }
    } else {
      if (editingRule) {
        result = await onUpdateCancelRule(policy.id, editingRule.id, data);
      } else {
        result = await onCreateCancelRule(policy.id, data);
      }
    }

    if (result) {
      setShowForm(false);
      setEditingRule(null);
    }
  };

  const handleDelete = async (rule) => {
    if (isBooking) {
      const confirmed = window.confirm(
        `Bạn có chắc muốn xóa quy tắc giảm giá ${rule.discountPercent}% ` +
          `cho mốc ${rule.daysBeforeDeparture} ngày trước khởi hành không?`,
      );

      if (!confirmed) {
        return;
      }

      await onDeleteBookingRule(policy.id, rule.id);

      return;
    }

    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa quy tắc hoàn tiền ${rule.refundPercent}% ` +
        `cho mốc ${rule.daysBefore} ngày trước khởi hành không?`,
    );

    if (!confirmed) {
      return;
    }

    await onDeleteCancelRule(policy.id, rule.id);
  };

  const getPolicyTypeLabel = () => {
    if (isBooking) {
      return "Chính sách đặt tour";
    }

    return "Chính sách hủy tour";
  };

  return (
    <>
      <Modal
        show={show}
        onHide={onClose}
        centered
        size="xl"
        backdrop={saving ? "static" : true}
        keyboard={!saving}
      >
        <Modal.Header closeButton={!saving}>
          <div>
            <Modal.Title>Quản lý quy tắc</Modal.Title>

            <div className="text-muted mt-1">
              {getPolicyTypeLabel()} — <strong>{policy.title}</strong>
            </div>
          </div>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          <div className="policy-rule-header mb-3">
            <div>
              <h5 className="mb-1">
                {isBooking ? "Quy tắc giảm giá" : "Quy tắc hoàn tiền"}
              </h5>

              <p className="text-muted mb-0">
                {isBooking
                  ? "Thiết lập mức giảm giá dựa trên số ngày trước khi khởi hành."
                  : "Thiết lập mức hoàn tiền dựa trên số ngày trước khi khởi hành."}
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
          ) : isBooking ? (
            <BookingPolicyTable
              rules={bookingRules}
              loading={false}
              onEdit={handleOpenEdit}
              onDelete={handleDelete}
            />
          ) : (
            <CancelPolicyTable
              rules={cancelRules}
              loading={false}
              onEdit={handleOpenEdit}
              onDelete={handleDelete}
            />
          )}
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={onClose} disabled={saving}>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {/* BOOKING FORM */}
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

      {/* CANCEL FORM */}
      {!isBooking && (
        <CancelPolicyFormModal
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
