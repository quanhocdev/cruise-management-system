// src/modules/operation/components/packages/TourPackageModal.jsx

import React, { useState, useEffect } from "react";
import { X, Check, ShieldCheck } from "lucide-react";
import useActivityCruiseTourAssignments from "../../hooks/useActivityCruiseTourAssignments";
import useActivityVisitTourAssignments from "../../hooks/useActivityVisitTourAssignments";
import useProductTourAssignments from "../../hooks/useProductTourAssignments";
import useServiceTourAssignments from "../../hooks/useServiceTourAssignments";
import "../../styles/packages/TourPackageModal.css";

const TourPackageModal = ({ tourId, initialData, onClose, onSave }) => {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [maxPassengers, setMaxPassengers] = useState("");
  const [status, setStatus] = useState("ACTIVE");
  const [selectedBenefits, setSelectedBenefits] = useState({}); // key: referenceId, value: benefit object

  // Gọi hook lấy sẵn các item đã cấu hình của tour để cho phép gom quyền lợi
  const { configuredActivities } = useActivityCruiseTourAssignments();
  const { configuredActivityVisits } = useActivityVisitTourAssignments();
  const { configuredProducts } = useProductTourAssignments();
  const { configuredServices } = useServiceTourAssignments();

  useEffect(() => {
    if (initialData) {
      setName(initialData.name || "");
      setDescription(initialData.description || "");
      setPrice(initialData.price || "");
      setMaxPassengers(initialData.maxPassengers || "");
      setStatus(initialData.status || "ACTIVE");

      if (initialData.benefits) {
        const map = {};
        initialData.benefits.forEach((b) => {
          map[b.referenceId] = {
            type: b.type,
            referenceId: b.referenceId,
            freeQuantity: b.freeQuantity || 1,
            discountPercent: b.discountPercent || 0,
          };
        });
        setSelectedBenefits(map);
      }
    }
  }, [initialData]);

  const toggleBenefit = (refId, type) => {
    setSelectedBenefits((prev) => {
      const copy = { ...prev };
      if (copy[refId]) {
        delete copy[refId];
      } else {
        copy[refId] = {
          type,
          referenceId: refId,
          freeQuantity: 1,
          discountPercent: 0,
        };
      }
      return copy;
    });
  };

  const handleBenefitChange = (refId, field, value) => {
    setSelectedBenefits((prev) => ({
      ...prev,
      [refId]: {
        ...prev[refId],
        [field]: value === "" ? "" : Number(value),
      },
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = {
      tourId,
      name,
      description,
      price: price ? Number(price) : 0,
      maxPassengers: maxPassengers ? Number(maxPassengers) : null,
      status,
      benefits: Object.values(selectedBenefits),
    };
    onSave(payload);
  };

  return (
    <div className="tour-package-modal-overlay">
      <div className="tour-package-modal-container">
        <div className="tour-package-modal-header">
          <h2>{initialData ? "Chỉnh sửa Gói Tour" : "Tạo mới Gói Tour"}</h2>
          <button type="button" className="close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="tour-package-modal-form">
          <div className="form-grid">
            <div className="form-group full-width">
              <label>Tên gói tour *</label>
              <input
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Ví dụ: Gói Tiêu chuẩn 3 Ngày 2 Đêm"
              />
            </div>

            <div className="form-group">
              <label>Giá gói (VND) *</label>
              <input
                type="number"
                required
                min="0"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                placeholder="0"
              />
            </div>

            <div className="form-group">
              <label>Số khách tối đa</label>
              <input
                type="number"
                min="1"
                value={maxPassengers}
                onChange={(e) => setMaxPassengers(e.target.value)}
                placeholder="Không giới hạn"
              />
            </div>

            <div className="form-group">
              <label>Trạng thái</label>
              <select
                value={status}
                onChange={(e) => setStatus(e.target.value)}
              >
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
              </select>
            </div>

            <div className="form-group full-width">
              <label>Mô tả chi tiết</label>
              <textarea
                rows={3}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Nhập mô tả các quyền lợi tổng quan..."
              />
            </div>
          </div>

          <div className="benefits-section-title">
            <ShieldCheck size={18} />
            <h3>Chọn Quyền lợi đi kèm (Services, Products, Activities)</h3>
          </div>

          <div className="benefits-selection-list">
            {/* SERVICES */}
            {configuredServices.length > 0 && (
              <div className="benefit-group">
                <h4>Dịch vụ (Services)</h4>
                {configuredServices.map((s) => (
                  <div
                    key={s.id}
                    className={`benefit-item ${selectedBenefits[s.id] ? "selected" : ""}`}
                  >
                    <label className="benefit-checkbox-label">
                      <input
                        type="checkbox"
                        checked={!!selectedBenefits[s.id]}
                        onChange={() => toggleBenefit(s.id, "SERVICE")}
                      />
                      <span>{s.serviceName}</span>
                    </label>
                    {selectedBenefits[s.id] && (
                      <div className="benefit-inputs">
                        <input
                          type="number"
                          placeholder="Số lượng"
                          value={selectedBenefits[s.id].freeQuantity}
                          onChange={(e) =>
                            handleBenefitChange(
                              s.id,
                              "freeQuantity",
                              e.target.value,
                            )
                          }
                        />
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}

            {/* PRODUCTS */}
            {configuredProducts.length > 0 && (
              <div className="benefit-group">
                <h4>Sản phẩm (Products)</h4>
                {configuredProducts.map((p) => (
                  <div
                    key={p.id}
                    className={`benefit-item ${selectedBenefits[p.id] ? "selected" : ""}`}
                  >
                    <label className="benefit-checkbox-label">
                      <input
                        type="checkbox"
                        checked={!!selectedBenefits[p.id]}
                        onChange={() => toggleBenefit(p.id, "PRODUCT")}
                      />
                      <span>{p.productName}</span>
                    </label>
                    {selectedBenefits[p.id] && (
                      <div className="benefit-inputs">
                        <input
                          type="number"
                          placeholder="Số lượng"
                          value={selectedBenefits[p.id].freeQuantity}
                          onChange={(e) =>
                            handleBenefitChange(
                              p.id,
                              "freeQuantity",
                              e.target.value,
                            )
                          }
                        />
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}

            {/* ACTIVITIES CRUISE */}
            {configuredActivities.length > 0 && (
              <div className="benefit-group">
                <h4>Hoạt động trên tàu (Activity Cruise)</h4>
                {configuredActivities.map((a) => (
                  <div
                    key={a.id}
                    className={`benefit-item ${selectedBenefits[a.id] ? "selected" : ""}`}
                  >
                    <label className="benefit-checkbox-label">
                      <input
                        type="checkbox"
                        checked={!!selectedBenefits[a.id]}
                        onChange={() => toggleBenefit(a.id, "ACTIVITY_CRUISE")}
                      />
                      <span>{a.activityName}</span>
                    </label>
                  </div>
                ))}
              </div>
            )}

            {/* ACTIVITIES VISIT */}
            {configuredActivityVisits.length > 0 && (
              <div className="benefit-group">
                <h4>Hoạt động trên bờ (Activity Visit)</h4>
                {configuredActivityVisits.map((v) => (
                  <div
                    key={v.id}
                    className={`benefit-item ${selectedBenefits[v.id] ? "selected" : ""}`}
                  >
                    <label className="benefit-checkbox-label">
                      <input
                        type="checkbox"
                        checked={!!selectedBenefits[v.id]}
                        onChange={() => toggleBenefit(v.id, "ACTIVITY_VISIT")}
                      />
                      <span>{v.visitName || "Điểm dừng tham quan"}</span>
                    </label>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="tour-package-modal-footer">
            <button type="button" className="btn-cancel" onClick={onClose}>
              Hủy
            </button>
            <button type="submit" className="btn-submit">
              Lưu gói tour
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TourPackageModal;
