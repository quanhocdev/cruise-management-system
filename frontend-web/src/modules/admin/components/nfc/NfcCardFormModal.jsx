import React, { useState, useEffect } from "react";
import "../../styles/NfcCardFormModal.css";

export default function NfcCardFormModal({
  card,
  isOpen,
  onClose,
  onSave,
  saving,
}) {
  const isEditMode = !!card;

  const [formData, setFormData] = useState({
    cardUid: "",
    status: "AVAILABLE",
  });

  useEffect(() => {
    if (card && isOpen) {
      setFormData({
        cardUid: card.cardUid || "",
        status: card.status || "AVAILABLE",
      });
    } else {
      setFormData({
        cardUid: "",
        status: "AVAILABLE",
      });
    }
  }, [card, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.cardUid.trim()) {
      alert("Vui lòng nhập UID của thẻ!");
      return;
    }
    onSave(formData);
  };

  return (
    <div className="nfc-modal-overlay">
      <div className="nfc-modal-content">
        <div className="nfc-modal-header">
          <h2 className="nfc-modal-title">
            {isEditMode ? "Chỉnh sửa thẻ NFC" : "Thêm thẻ NFC mới"}
          </h2>
          <button onClick={onClose} className="nfc-modal-close">
            &times;
          </button>
        </div>

        <form onSubmit={handleSubmit} className="nfc-modal-body">
          <div className="nfc-form-group">
            <label className="nfc-form-label">
              Mã UID thẻ <span style={{ color: "#dc2626" }}>*</span>
            </label>
            <input
              type="text"
              name="cardUid"
              value={formData.cardUid}
              onChange={handleChange}
              placeholder="Ví dụ: 04:EA:4B:B2:A1:34:80"
              className="nfc-form-input"
              required
            />
          </div>

          <div className="nfc-form-group" style={{ marginBottom: "0" }}>
            <label className="nfc-form-label">Trạng thái</label>
            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="nfc-form-select"
            >
              <option value="AVAILABLE">Có sẵn (AVAILABLE)</option>
              <option value="ASSIGNED">Đã gán (ASSIGNED)</option>
              <option value="INACTIVE">Không hoạt động (INACTIVE)</option>
            </select>
          </div>

          <div className="nfc-modal-footer">
            <button
              type="button"
              onClick={onClose}
              disabled={saving}
              className="nfc-btn-secondary"
            >
              Hủy
            </button>
            <button
              type="submit"
              disabled={saving}
              className="nfc-btn-primary"
              style={{
                opacity: saving ? 0.6 : 1,
                cursor: saving ? "not-allowed" : "pointer",
              }}
            >
              {saving ? "Đang lưu..." : "Lưu thay đổi"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
