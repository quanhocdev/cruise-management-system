import { Button, Spinner, Table } from "react-bootstrap";

export default function ProductTable({ products, loading, onEdit, onDelete }) {
  if (loading) {
    return (
      <div className="product-table-loading">
        <Spinner animation="border" />
        <span>Đang tải danh sách sản phẩm...</span>
      </div>
    );
  }

  if (!products || products.length === 0) {
    return (
      <div className="product-table-empty">
        <div className="product-table-empty-icon">📦</div>

        <h5>Chưa có sản phẩm</h5>

        <p>Khu vực này hiện chưa có sản phẩm nào.</p>
      </div>
    );
  }

  const formatPrice = (price) => {
    if (price === null || price === undefined) {
      return "-";
    }

    return Number(price).toLocaleString("vi-VN") + " ₫";
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case "ACTIVE":
        return "Đang hoạt động";

      case "INACTIVE":
        return "Ngừng hoạt động";

      default:
        return status || "-";
    }
  };

  return (
    <div className="product-table-wrapper">
      <Table responsive hover bordered className="product-table align-middle">
        <thead>
          <tr>
            <th className="product-table-image-column">Hình ảnh</th>

            <th>Sản phẩm</th>

            <th>Mô tả</th>

            <th>Giá</th>

            <th>Số lượng</th>

            <th>Trạng thái</th>

            <th className="product-table-action-column">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {products.map((product) => (
            <tr key={product.id}>
              {/* IMAGE */}
              <td>
                {product.imageUrl ? (
                  <img
                    src={product.imageUrl}
                    alt={product.name}
                    className="product-table-image"
                  />
                ) : (
                  <div className="product-table-no-image">📦</div>
                )}
              </td>

              {/* NAME */}
              <td>
                <div className="product-table-product-name">{product.name}</div>

                {product.areaName && (
                  <small className="text-muted">{product.areaName}</small>
                )}
              </td>

              {/* DESCRIPTION */}
              <td>
                <div className="product-table-description">
                  {product.description || "-"}
                </div>
              </td>

              {/* PRICE */}
              <td>
                <strong>{formatPrice(product.price)}</strong>
              </td>

              {/* QUANTITY */}
              <td>
                <span
                  className={
                    product.quantity === 0 ? "product-quantity-empty" : ""
                  }
                >
                  {product.quantity ?? 0}
                </span>
              </td>

              {/* STATUS */}
              <td>
                <span
                  className={`product-status-badge ${
                    product.status === "ACTIVE"
                      ? "product-status-active"
                      : "product-status-inactive"
                  }`}
                >
                  {getStatusLabel(product.status)}
                </span>
              </td>

              {/* ACTION */}
              <td>
                <div className="product-table-actions">
                  <Button
                    size="sm"
                    variant="outline-primary"
                    onClick={() => onEdit(product)}
                  >
                    Sửa
                  </Button>

                  <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDelete(product)}
                  >
                    Xóa
                  </Button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
}
