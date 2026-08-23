// src/modules/convenience/tour-config/ProductTourConfigModal.jsx
import React, { useEffect, useState } from "react";
import { Package, Save, Boxes, X } from "lucide-react";

import ProductSelect from "./ProductSelect";
import "../../styles/tour-config/ProductTourConfigModal.css";

const ProductTourConfigModal = ({
  assignment,
  onClose,
  onSubmit,
  submitting = false,
}) => {
  const [formData, setFormData] = useState({
    productId: "",
    quantity: "",
  });

  const [errors, setErrors] = useState({});

  // =====================================================
  // RESET
  // =====================================================

  useEffect(() => {
    if (!assignment) {
      return;
    }

    setFormData({
      productId: assignment.productId || "",
      quantity: assignment.quantity != null ? String(assignment.quantity) : "",
    });

    setErrors({});
  }, [assignment]);

  if (!assignment) {
    return null;
  }

  // =====================================================
  // CHANGE
  // =====================================================

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));

    setErrors((previous) => ({
      ...previous,
      [name]: "",
    }));
  };

  // =====================================================
  // PRODUCT
  // =====================================================

  const handleProductChange = (productId) => {
    setFormData((previous) => ({
      ...previous,
      productId,
    }));

    setErrors((previous) => ({
      ...previous,
      productId: "",
    }));
  };

  // =====================================================
  // VALIDATE
  // =====================================================

  const validate = () => {
    const nextErrors = {};

    if (!formData.productId) {
      nextErrors.productId = "Vui lòng chọn sản phẩm";
    }

    if (!formData.quantity || Number(formData.quantity) <= 0) {
      nextErrors.quantity = "Số lượng phải lớn hơn 0";
    }

    setErrors(nextErrors);

    return Object.keys(nextErrors).length === 0;
  };

  // =====================================================
  // SUBMIT
  // =====================================================

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!validate()) {
      return;
    }

    const payload = {
      productId: formData.productId,
      quantity: Number(formData.quantity),
    };

    await onSubmit?.(assignment.id, payload);
  };

  const isEditing = assignment.status === "NOT_STARTED";

  return (
    <div
      className="product-tour-config-overlay"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !submitting) {
          onClose?.();
        }
      }}
    >
      <div className="product-tour-config-modal">
        {/* HEADER */}

        <div className="product-tour-config-modal-header">
          <div>
            <span className="product-tour-config-modal-eyebrow">
              {isEditing ? "Chỉnh sửa cấu hình" : "Cấu hình sản phẩm"}
            </span>

            <h2>{assignment.tourCode}</h2>

            <p>
              {assignment.tourName}
              {" · "}
              {assignment.cruiseAreaName}
            </p>
          </div>

          <button
            type="button"
            className="product-tour-config-modal-close"
            onClick={onClose}
            disabled={submitting}
            aria-label="Đóng"
          >
            <X size={20} />
          </button>
        </div>

        {/* FORM */}

        <form
          className="product-tour-config-modal-form"
          onSubmit={handleSubmit}
        >
          {/* PRODUCT */}

          <div className="product-tour-config-field">
            <label>
              <Package size={15} />
              Sản phẩm
              <span>*</span>
            </label>

            <ProductSelect
              value={formData.productId}
              onChange={handleProductChange}
              disabled={submitting}
            />

            {errors.productId && (
              <span className="product-tour-config-field-error">
                {errors.productId}
              </span>
            )}
          </div>

          {/* QUANTITY */}

          <div className="product-tour-config-field">
            <label>
              <Boxes size={15} />
              Số lượng
              <span>*</span>
            </label>

            <input
              type="number"
              name="quantity"
              min="1"
              step="1"
              value={formData.quantity}
              onChange={handleChange}
              disabled={submitting}
              placeholder="Ví dụ: 50"
            />

            {errors.quantity && (
              <span className="product-tour-config-field-error">
                {errors.quantity}
              </span>
            )}

            <small>Số lượng không được vượt quá tồn kho của sản phẩm.</small>
          </div>

          {/* FOOTER */}

          <div className="product-tour-config-modal-footer">
            <button
              type="button"
              className="product-tour-config-cancel"
              onClick={onClose}
              disabled={submitting}
            >
              Hủy
            </button>

            <button
              type="submit"
              className="product-tour-config-submit"
              disabled={submitting}
            >
              <Save size={17} />

              {submitting
                ? "Đang lưu..."
                : isEditing
                  ? "Lưu thay đổi"
                  : "Lưu cấu hình"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ProductTourConfigModal;
