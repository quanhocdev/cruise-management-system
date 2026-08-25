// src/modules/operation/components/tour-configuration/ProductConfigurationTable.jsx

import React from "react";
import { CheckCircle2, Package, XCircle } from "lucide-react";
import { formatVND } from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/ProductConfigurationTable.css";

const ProductConfigurationTable = ({ products = [] }) => {
  const safeProducts = Array.isArray(products) ? products : [];

  return (
    <section className="product-configuration-table-section">
      {/* =========================================================
          HEADER
          ========================================================= */}
      <div className="product-configuration-table-header">
        <div className="product-configuration-table-title">
          <div className="product-configuration-table-icon">
            <Package size={20} />
          </div>

          <div>
            <h2>Sản phẩm</h2>

            <p>Các sản phẩm đã được cấu hình cho Tour.</p>
          </div>
        </div>

        <span className="product-configuration-table-count">
          {safeProducts.length} sản phẩm
        </span>
      </div>

      {/* =========================================================
          EMPTY
          ========================================================= */}
      {safeProducts.length === 0 ? (
        <div className="product-configuration-table-empty">
          <XCircle size={24} />

          <span>Tour chưa có sản phẩm được cấu hình.</span>
        </div>
      ) : (
        /* =======================================================
           TABLE
           ======================================================= */
        <div className="product-configuration-table-wrapper">
          <table className="product-configuration-table">
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th>Số lượng</th>
                <th>Đơn giá</th>
                <th>Tổng tiền</th>
                <th>Trạng thái</th>
              </tr>
            </thead>

            <tbody>
              {safeProducts.map((item, index) => {
                const totalPrice =
                  item.price != null && item.quantity != null
                    ? Number(item.price) * Number(item.quantity)
                    : null;

                return (
                  <tr key={item.id || `product-${index}`}>
                    {/* =================================================
                        PRODUCT
                        ================================================= */}
                    <td>
                      <div className="product-configuration-name">
                        <strong>{item.productName || "Chưa xác định"}</strong>

                        {item.productId && <span>{item.productId}</span>}
                      </div>
                    </td>

                    {/* =================================================
                        QUANTITY
                        ================================================= */}
                    <td>{item.quantity != null ? item.quantity : "—"}</td>

                    {/* =================================================
                        UNIT PRICE
                        ================================================= */}
                    <td>{formatVND(item.price)}</td>

                    {/* =================================================
                        TOTAL
                        ================================================= */}
                    <td>{formatVND(totalPrice)}</td>

                    {/* =================================================
                        STATUS
                        ================================================= */}
                    <td>
                      <span className="product-configuration-status configured">
                        <CheckCircle2 size={14} />
                        Đã cấu hình
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
};

export default ProductConfigurationTable;
