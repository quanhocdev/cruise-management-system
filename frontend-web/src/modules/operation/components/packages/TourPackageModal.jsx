// src/modules/operation/components/packages/TourPackageModal.jsx

import React, { useState, useEffect } from "react";
import { X, ShieldCheck } from "lucide-react";
import "../../styles/packages/TourPackageModal.css";
import activityCruiseTourAssignmentService from "../../services/activityCruiseTourAssignmentService";
import activityVisitTourAssignmentService from "../../services/activityVisitTourAssignmentService";
import productTourAssignmentService from "../../services/productTourAssignmentService";
import serviceTourAssignmentService from "../../services/serviceTourAssignmentService";

const TourPackageModal = ({
  tourId,
  roomTypes = [],
  initialData,
  onClose,
  onSave,
}) => {
  const [name, setName] = useState("");
  const [roomTypeId, setRoomTypeId] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [maxPassengers, setMaxPassengers] = useState("");
  const [status, setStatus] = useState("ACTIVE");
  const [selectedBenefits, setSelectedBenefits] = useState({}); // key: referenceId, value: benefit object

  // Khai báo state lưu quyền lợi trong Modal
  const [configuredActivities, setConfiguredActivities] = useState([]);
  const [configuredActivityVisits, setConfiguredActivityVisits] = useState([]);
  const [configuredProducts, setConfiguredProducts] = useState([]);
  const [configuredServices, setConfiguredServices] = useState([]);

  // Gọi API lấy dữ liệu cấu hình khi modal mở và có tourId
  useEffect(() => {
    if (!tourId) {
      console.log("❌ TourPackageModal: Không có tourId được truyền vào!");
      return;
    }

    const fetchAllConfigurations = async () => {
      try {
        const [acts, visits, prods, srvs] = await Promise.all([
          activityCruiseTourAssignmentService
            .getConfiguredByTour(tourId)
            .catch(() => []),
          activityVisitTourAssignmentService
            .getConfiguredByTour(tourId)
            .catch(() => []),
          productTourAssignmentService
            .getConfiguredByTour(tourId)
            .catch(() => []),
          serviceTourAssignmentService
            .getConfiguredByTour(tourId)
            .catch(() => []),
        ]);

        setConfiguredActivities(
          Array.isArray(acts) ? acts : acts?.content || [],
        );
        setConfiguredActivityVisits(
          Array.isArray(visits) ? visits : visits?.content || [],
        );
        setConfiguredProducts(
          Array.isArray(prods) ? prods : prods?.content || [],
        );
        setConfiguredServices(Array.isArray(srvs) ? srvs : srvs?.content || []);
      } catch (err) {
        console.error(
          "Lỗi tổng thể khi tải danh sách quyền lợi cấu hình:",
          err,
        );
      }
    };

    fetchAllConfigurations();
  }, [tourId]);

  useEffect(() => {
    if (initialData) {
      setName(initialData.name || "");
      setRoomTypeId(initialData.roomTypeId || "");
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
            quantity: b.quantity ?? 1, // Đã đổi từ freeQuantity sang quantity
            discountPercent: b.discountPercent ?? 0,
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
          quantity: 1, // Đã đổi từ freeQuantity sang quantity
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
      roomTypeId: roomTypeId ? roomTypeId : null,
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
      <div className="tour-package-modal-container large">
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
              <label>Hạng phòng phân phối (Room Type)</label>
              <select
                value={roomTypeId}
                onChange={(e) => setRoomTypeId(e.target.value)}
              >
                <option value="">
                  -- Chọn hạng phòng (Áp dụng toàn tour) --
                </option>
                {roomTypes.map((rt) => (
                  <option key={rt.id} value={rt.id}>
                    {rt.name}
                  </option>
                ))}
              </select>
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
                rows={2}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Nhập mô tả các quyền lợi tổng quan..."
              />
            </div>
          </div>

          <div className="benefits-section-title">
            <ShieldCheck size={18} />
            <h3>Cấu hình Quyền lợi & Ưu đãi đi kèm</h3>
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
                      <span>{s.serviceName || s.name}</span>
                    </label>
                    {selectedBenefits[s.id] && (
                      <div className="benefit-inputs-group">
                        <div className="input-with-label">
                          <span>Số lượng:</span>
                          <input
                            type="number"
                            min="0"
                            value={selectedBenefits[s.id].quantity}
                            onChange={(e) =>
                              handleBenefitChange(
                                s.id,
                                "quantity",
                                e.target.value,
                              )
                            }
                          />
                        </div>
                        <div className="input-with-label">
                          <span>Giảm (%):</span>
                          <input
                            type="number"
                            min="0"
                            max="100"
                            value={selectedBenefits[s.id].discountPercent}
                            onChange={(e) =>
                              handleBenefitChange(
                                s.id,
                                "discountPercent",
                                e.target.value,
                              )
                            }
                          />
                        </div>
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
                      <span>{p.productName || p.name}</span>
                    </label>
                    {selectedBenefits[p.id] && (
                      <div className="benefit-inputs-group">
                        <div className="input-with-label">
                          <span>Số lượng:</span>
                          <input
                            type="number"
                            min="0"
                            value={selectedBenefits[p.id].quantity}
                            onChange={(e) =>
                              handleBenefitChange(
                                p.id,
                                "quantity",
                                e.target.value,
                              )
                            }
                          />
                        </div>
                        <div className="input-with-label">
                          <span>Giảm (%):</span>
                          <input
                            type="number"
                            min="0"
                            max="100"
                            value={selectedBenefits[p.id].discountPercent}
                            onChange={(e) =>
                              handleBenefitChange(
                                p.id,
                                "discountPercent",
                                e.target.value,
                              )
                            }
                          />
                        </div>
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
                      <span>{a.activityName || a.name}</span>
                    </label>
                    {selectedBenefits[a.id] && (
                      <div className="benefit-inputs-group">
                        <div className="input-with-label">
                          <span>Số lượng:</span>
                          <input
                            type="number"
                            min="0"
                            value={selectedBenefits[a.id].quantity}
                            onChange={(e) =>
                              handleBenefitChange(
                                a.id,
                                "quantity",
                                e.target.value,
                              )
                            }
                          />
                        </div>
                        <div className="input-with-label">
                          <span>Giảm (%):</span>
                          <input
                            type="number"
                            min="0"
                            max="100"
                            value={selectedBenefits[a.id].discountPercent}
                            onChange={(e) =>
                              handleBenefitChange(
                                a.id,
                                "discountPercent",
                                e.target.value,
                              )
                            }
                          />
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}

            {/* ACTIVITIES VISIT */}
            <div className="benefit-group">
              <h4>Hoạt động trên bờ (Activity Visit)</h4>
              {configuredActivityVisits.length === 0 ? (
                <p className="no-benefit-text">
                  Chưa có hoạt động trên bờ nào được cấu hình.
                </p>
              ) : (
                configuredActivityVisits.map((v) => {
                  const benefitId = v.visitTourId || v.id;
                  return (
                    <div
                      key={benefitId}
                      className={`benefit-item ${selectedBenefits[benefitId] ? "selected" : ""}`}
                    >
                      <label className="benefit-checkbox-label">
                        <input
                          type="checkbox"
                          checked={!!selectedBenefits[benefitId]}
                          onChange={() =>
                            toggleBenefit(benefitId, "ACTIVITY_VISIT")
                          }
                        />
                        <span>{v.visitName || v.name || "Điểm tham quan"}</span>
                      </label>
                      {selectedBenefits[benefitId] && (
                        <div className="benefit-inputs-group">
                          <div className="input-with-label">
                            <span>Số lượng:</span>
                            <input
                              type="number"
                              min="0"
                              value={selectedBenefits[benefitId].quantity}
                              onChange={(e) =>
                                handleBenefitChange(
                                  benefitId,
                                  "quantity",
                                  e.target.value,
                                )
                              }
                            />
                          </div>
                          <div className="input-with-label">
                            <span>Giảm (%):</span>
                            <input
                              type="number"
                              min="0"
                              max="100"
                              value={
                                selectedBenefits[benefitId].discountPercent
                              }
                              onChange={(e) =>
                                handleBenefitChange(
                                  benefitId,
                                  "discountPercent",
                                  e.target.value,
                                )
                              }
                            />
                          </div>
                        </div>
                      )}
                    </div>
                  );
                })
              )}
            </div>
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
