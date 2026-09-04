import React from "react";
import { X } from "lucide-react";
import "../../styles/packages/PackageBenefitDetailModal.css";

const PackageBenefitDetailModal = ({ packageData, onClose }) => {
  if (!packageData) return null;

  return (
    <div className="tour-package-modal-overlay">
      <div className="tour-package-modal-container">
        <div className="tour-package-modal-header">
          <h2>Chi tiết quyền lợi: {packageData.name}</h2>
          <button type="button" className="close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <div className="benefit-details-content" style={{ padding: "20px" }}>
          {!packageData.benefits || packageData.benefits.length === 0 ? (
            <p className="no-benefit-text">
              Gói tour này chưa được cấu hình quyền lợi đi kèm.
            </p>
          ) : (
            <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
              {packageData.benefits.map((b, idx) => (
                <li
                  key={b.id || idx}
                  style={{
                    padding: "10px 12px",
                    marginBottom: "8px",
                    background: "#f8f9fa",
                    borderRadius: "6px",
                    border: "1px solid #e9ecef",
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                  }}
                >
                  <div>
                    <strong style={{ color: "#2563eb" }}>[{b.type}]</strong>
                    <span style={{ marginLeft: "8px", color: "#374151" }}>
                      Mã tham chiếu: {b.referenceId?.slice(0, 8)}...
                    </span>
                  </div>
                  <div
                    style={{ display: "flex", gap: "12px", fontSize: "14px" }}
                  >
                    <span
                      style={{
                        background: "#e0f2fe",
                        padding: "2px 8px",
                        borderRadius: "4px",
                      }}
                    >
                      SL: <strong>{b.quantity ?? 0}</strong>
                    </span>
                    <span
                      style={{
                        background: "#dcfce7",
                        padding: "2px 8px",
                        borderRadius: "4px",
                      }}
                    >
                      Giảm: <strong>{b.discountPercent ?? 0}%</strong>
                    </span>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="tour-package-modal-footer">
          <button type="button" className="btn-cancel" onClick={onClose}>
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};

export default PackageBenefitDetailModal;
