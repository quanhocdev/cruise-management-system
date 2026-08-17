import React from "react";
import { Image as ImageIcon, Package } from "lucide-react";

const ProductTable = ({ products, loading }) => {
  if (loading) {
    return (
      <div className="convenience-loading">Đang tải danh sách sản phẩm...</div>
    );
  }

  if (!products || products.length === 0) {
    return <div className="convenience-empty">Chưa có sản phẩm nào.</div>;
  }

  return (
    <div className="table-responsive">
      <table
        className="convenience-table"
        style={{ tableLayout: "fixed", width: "100%" }}
      >
        <thead>
          <tr>
            <th style={{ width: "80px", textAlign: "center" }}>Hình ảnh</th>
            <th style={{ width: "25%" }}>Tên sản phẩm</th>
            <th>Mô tả</th>
            <th style={{ width: "160px", textAlign: "right" }}>Đơn giá</th>
            <th style={{ width: "120px", textAlign: "center" }}>Số lượng</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id || product.productId}>
              {/* Cột 1: Hình ảnh (Căn giữa) */}
              <td style={{ textAlign: "center" }}>
                <div
                  style={{
                    width: "48px",
                    height: "48px",
                    margin: "0 auto",
                    flexShrink: 0,
                  }}
                >
                  {product.imageUrl ? (
                    <img
                      src={product.imageUrl}
                      alt={product.name}
                      className="product-image-thumb"
                      style={{
                        width: "48px",
                        height: "48px",
                        maxWidth: "48px",
                        maxHeight: "48px",
                        objectFit: "cover",
                        borderRadius: "6px",
                        display: "block",
                      }}
                    />
                  ) : (
                    <div
                      className="product-image-placeholder"
                      style={{
                        width: "48px",
                        height: "48px",
                        borderRadius: "6px",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                      }}
                    >
                      <ImageIcon size={20} />
                    </div>
                  )}
                </div>
              </td>

              {/* Cột 2: Tên sản phẩm */}
              <td>
                <span
                  className="font-semibold"
                  style={{ display: "block", wordBreak: "break-word" }}
                >
                  {product.name}
                </span>
              </td>

              {/* Cột 3: Mô tả (Tự co giãn chiếm phần lớn không gian) */}
              <td>
                <div
                  className="text-muted"
                  style={{
                    maxHeight: "42px",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    display: "-webkit-box",
                    WebkitLineClamp: 2,
                    WebkitBoxOrient: "vertical",
                    fontSize: "13px",
                    lineHeight: "1.3",
                  }}
                  title={product.description}
                >
                  {product.description || "—"}
                </div>
              </td>

              {/* Cột 4: Đơn giá (Căn phải chuẩn hiển thị tiền tệ) */}
              <td
                className="font-semibold"
                style={{ textAlign: "right", whiteSpace: "nowrap" }}
              >
                {product.price != null
                  ? `${Number(product.price).toLocaleString("vi-VN")} VNĐ`
                  : "—"}
              </td>

              {/* Cột 5: Số lượng (Căn giữa) */}
              <td style={{ textAlign: "center", whiteSpace: "nowrap" }}>
                <span
                  className="flex-align-center font-medium"
                  style={{ justifyContent: "center" }}
                >
                  <Package size={14} className="icon-mr" />
                  {product.quantity ?? product.stockQuantity ?? 0}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProductTable;
