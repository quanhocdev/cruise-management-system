// src/modules/convenience/tour-config/ProductSelect.jsx
import React, { useEffect } from "react";
import { ChevronDown, Loader2 } from "lucide-react";

import useProduct from "../../hooks/useProduct";
import "../../styles/tour-config/ProductSelect.css";

const ProductSelect = ({
  value = "",
  onChange,
  disabled = false,
  error = "",
}) => {
  const { products, loading, error: loadError, loadProducts } = useProduct();

  // =====================================================
  // LOAD PRODUCTS
  // =====================================================

  useEffect(() => {
    loadProducts();
  }, [loadProducts]);

  // =====================================================
  // CHANGE
  // =====================================================

  const handleChange = (event) => {
    const productId = event.target.value;

    onChange?.(productId);
  };

  return (
    <div className="convenience-product-select">
      <div
        className={`convenience-product-select-wrapper ${
          error ? "has-error" : ""
        }`}
      >
        <select
          value={value}
          onChange={handleChange}
          disabled={disabled || loading}
          className="convenience-product-select-input"
        >
          <option value="">
            {loading ? "Đang tải sản phẩm..." : "Chọn sản phẩm"}
          </option>

          {products.map((product) => (
            <option key={product.id} value={product.id}>
              {product.name}
            </option>
          ))}
        </select>

        {loading ? (
          <Loader2 size={17} className="convenience-product-select-loading" />
        ) : (
          <ChevronDown size={17} className="convenience-product-select-icon" />
        )}
      </div>

      {error && (
        <span className="convenience-product-select-error">{error}</span>
      )}

      {!error && loadError && (
        <span className="convenience-product-select-error">{loadError}</span>
      )}

      {!loading && !loadError && products.length === 0 && (
        <span className="convenience-product-select-hint">
          Chưa có sản phẩm khả dụng
        </span>
      )}
    </div>
  );
};

export default ProductSelect;
