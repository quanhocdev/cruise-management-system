// src/modules/operation/components/tour-configuration/ProductConfigurationTable.jsx

import React from "react";
import { CheckCircle2, Package, XCircle } from "lucide-react";
import {
  isProductConfigured,
  formatVND,
} from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/ProductConfigurationTable.css";

const ProductConfigurationTable = ({ products = [] }) => {
  const safeProducts = products || [];

  const getStatusLabel = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "Chờ cấu hình";
      case "NOT_STARTED":
        return "Đã cấu hình";
      case "IN_PROGRESS":
        return "Đang hoạt động";
      case "COMPLETED":
        return "Đã kết thúc";
      default:
        return status || "Không xác định";
    }
  };

  return (
    <section className="product-configuration-table-section">
      <div className="product-configuration-table-header">
        <div className="product-configuration-table-title">
          <div className="product-configuration-table-icon">
            <Package size={20} />
          </div>

          <div>
            <h2>Sản phẩm</h2>
            <p>Các sản phẩm được Operation phân công cho tour.</p>
          </div>
        </div>

        <span className="product-configuration-table-count">
          {safeProducts.length} phân công
        </span>
      </div>

      {safeProducts.length === 0 ? (
        <div className="product-configuration-table-empty">
          <XCircle size={24} />
          <span>Tour chưa được phân công sản phẩm.</span>
        </div>
      ) : (
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
                const configured = isProductConfigured(item);
                const totalPrice =
                  item.price != null && item.quantity != null
                    ? item.price * item.quantity
                    : null;

                return (
                  <tr key={item.id || `product-${index}`}>
                    <td>
                      <div className="product-configuration-name">
                        <strong>
                          {item.productName || "Chưa chọn sản phẩm"}
                        </strong>

                        {item.productId && <span>{item.productId}</span>}
                      </div>
                    </td>

                    <td>{item.quantity != null ? item.quantity : "—"}</td>

                    <td>{formatVND(item.price)}</td>

                    <td>{formatVND(totalPrice)}</td>

                    <td>
                      <span
                        className={`product-configuration-status ${
                          configured ? "configured" : "waiting"
                        }`}
                      >
                        {configured ? (
                          <CheckCircle2 size={14} />
                        ) : (
                          <XCircle size={14} />
                        )}

                        {configured
                          ? "Đã cấu hình"
                          : getStatusLabel(item.status)}
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
