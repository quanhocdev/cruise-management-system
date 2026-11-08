// src/modules/admin/components/cruise/CruiseDeleteModal.jsx

import { Modal, Spinner } from "react-bootstrap";
import { Trash2 } from "lucide-react";

export default function CruiseDeleteModal({
  cruise,
  loading,
  onConfirm,
  onClose,
}) {
  return (
    <Modal
      show={Boolean(cruise)}
      onHide={loading ? undefined : onClose}
      centered
      size="sm"
      contentClassName="mc-modal mc-modal--confirm"
    >
      <Modal.Body>
        <span className="mc-confirm-icon">
          <Trash2 size={24} />
        </span>

        <h3>Xóa du thuyền?</h3>

        <p>
          Bạn sắp xóa <strong>{cruise?.name}</strong>. Hành động này không thể
          hoàn tác.
        </p>

        <div className="mc-confirm-actions">
          <button
            type="button"
            className="mc-btn mc-btn--ghost"
            onClick={onClose}
            disabled={loading}
          >
            Hủy
          </button>

          <button
            type="button"
            className="mc-btn mc-btn--danger"
            onClick={onConfirm}
            disabled={loading}
          >
            {loading ? <Spinner size="sm" animation="border" /> : "Xóa"}
          </button>
        </div>
      </Modal.Body>
    </Modal>
  );
}
