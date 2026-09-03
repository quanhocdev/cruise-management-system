// src/modules/operation/components/tour-configuration/ProductConfigurationTable.jsx

import React from "react";
import { CheckCircle2, Package, XCircle } from "lucide-react";
import {
  formatVND,
  getTourStatusMeta,
  isTourItemConfigured,
} from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/ProductConfigurationTable.css";

const ProductConfigurationTable = ({ products = [] }) => {
  const safeProducts = Array.isArray(products) ? products : [];

  // =========================================================
  // SỐ LƯỢNG THỰC SỰ ĐÃ CẤU HÌNH (status !== WAITING_CONFIG)
  // =========================================================

  const configuredCount = safeProducts.filter((item) =>
    isTourItemConfigured(item.status),
  ).length;

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

        {/* ✅ hiển thị rõ số đã cấu hình / tổng số đã phân công */}
        <span className="product-configuration-table-count">
          {configuredCount}/{safeProducts.length} đã cấu hình
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
                <th>Trạng thái</th>
              </tr>
            </thead>

            <tbody>
              {safeProducts.map((item, index) => {
                // =========================================================
                // TRẠNG THÁI THẬT LẤY TỪ ProductTourStatus
                // (WAITING_CONFIG / CONFIGURED / NOT_STARTED /
                //  IN_PROGRESS / OUT_OF_STOCK / COMPLETED)
                // =========================================================

                const statusMeta = getTourStatusMeta(item.status);
                const configured = isTourItemConfigured(item.status);

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
                        STATUS
                        ================================================= */}
                    <td>
                      <span
                        className={`product-configuration-status ${statusMeta.className}`}
                      >
                        {configured ? (
                          <CheckCircle2 size={14} />
                        ) : (
                          <XCircle size={14} />
                        )}
                        {statusMeta.label}
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
