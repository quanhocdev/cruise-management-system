import { useEffect, useState } from "react";
import { Alert, Button, Form, Image, Modal, Spinner } from "react-bootstrap";

// Hàm hỗ trợ format hiển thị số (ví dụ: 500000 -> "500.000")
const formatNumber = (val) => {
  if (!val && val !== 0) return "";
  const cleanStr = String(val).replace(/\D/g, "");
  return cleanStr ? new Intl.NumberFormat("vi-VN").format(cleanStr) : "";
};

// Hàm parse chuỗi format về lại số thuần (ví dụ: "500.000" -> 500000)
const parseNumber = (val) => {
  if (!val) return "";
  return String(val).replace(/\D/g, "");
};

export default function ProductFormModal({
  show,
  saving,
  editingProduct,
  error,
  onClose,
  onSubmit,
}) {
  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "", // Lưu giá trị raw number (ví dụ: "500000")
    quantity: "",
    status: "ACTIVE",
    image: null,
  });

  const [displayPrice, setDisplayPrice] = useState(""); // Lưu giá trị hiển thị dạng đẹp ("500.000")
  const [previewUrl, setPreviewUrl] = useState("");

  useEffect(() => {
    if (!show) return;

    if (editingProduct) {
      const rawPrice = editingProduct.price ?? "";
      setForm({
        name: editingProduct.name || "",
        description: editingProduct.description || "",
        price: rawPrice,
        quantity: editingProduct.quantity ?? "",
        status: editingProduct.status || "ACTIVE",
        image: null,
      });
      setDisplayPrice(formatNumber(rawPrice));
      setPreviewUrl(editingProduct.imageUrl || "");
    } else {
      setForm({
        name: "",
        description: "",
        price: "",
        quantity: "",
        status: "ACTIVE",
        image: null,
      });
      setDisplayPrice("");
      setPreviewUrl("");
    }
  }, [show, editingProduct]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  // Xử lý riêng cho Input Giá tiền khi người dùng gõ
  const handlePriceChange = (event) => {
    const rawValue = parseNumber(event.target.value);
    setForm((prev) => ({ ...prev, price: rawValue }));
    setDisplayPrice(formatNumber(rawValue));
  };

  // Nút bấm gõ nhanh thêm 3 số 0 (phím tắt +000)
  const handleAddZeros = (count) => {
    const currentPrice = form.price || "0";
    const newPrice = currentPrice + "0".repeat(count);
    // Giới hạn max tránh tràn số (VD max 1 tỷ)
    if (Number(newPrice) > 1000000000) return;
    setForm((prev) => ({ ...prev, price: newPrice }));
    setDisplayPrice(formatNumber(newPrice));
  };

  const handleImageChange = (event) => {
    const file = event.target.files?.[0] || null;
    setForm((previous) => ({
      ...previous,
      image: file,
    }));

    if (file) {
      setPreviewUrl(URL.createObjectURL(file));
    } else {
      setPreviewUrl(editingProduct?.imageUrl || "");
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!form.name.trim()) return;

    const numericPrice = Number(form.price || 0);
    const numericQuantity = Number(form.quantity || 0);

    if (form.price === "" || numericPrice < 0) return;
    if (form.quantity === "" || numericQuantity < 0) return;

    const formData = new FormData();

    formData.append("name", form.name.trim());
    formData.append(
      "description",
      form.description ? form.description.trim() : "",
    );
    formData.append("price", numericPrice.toString());

    // 🔴 ĐỔI TỪ "quantity" THÀNH "stockQuantity" Ở ĐÂY
    formData.append("stockQuantity", numericQuantity.toString());

    if (editingProduct) {
      formData.append("status", form.status);
    }

    if (form.image) {
      formData.append("image", form.image);
    }

    await onSubmit(formData);
  };
  return (
    <Modal show={show} onHide={onClose} centered size="lg" backdrop="static">
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton={!saving}>
          <Modal.Title>
            {editingProduct ? "Chỉnh sửa sản phẩm" : "Tạo sản phẩm"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          <Form.Group className="mb-3">
            <Form.Label>
              Tên sản phẩm <span className="text-danger">*</span>
            </Form.Label>
            <Form.Control
              type="text"
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="Nhập tên sản phẩm"
              maxLength={150}
              disabled={saving}
              required
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Mô tả</Form.Label>
            <Form.Control
              as="textarea"
              rows={4}
              name="description"
              value={form.description}
              onChange={handleChange}
              placeholder="Nhập mô tả sản phẩm"
              maxLength={5000}
              disabled={saving}
            />
            <Form.Text muted>{form.description.length}/5000</Form.Text>
          </Form.Group>

          <div className="row">
            {/* INPUT GIÁ TIỀN ĐÃ ĐƯỢC NÂNG CẤP */}
            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>
                  Giá (VNĐ) <span className="text-danger">*</span>
                </Form.Label>
                <div className="input-group">
                  <Form.Control
                    type="text"
                    name="price"
                    value={displayPrice}
                    onChange={handlePriceChange}
                    placeholder="0"
                    disabled={saving}
                    required
                  />
                  <span className="input-group-text">₫</span>
                </div>

                {/* Các nút thêm nhanh 3 số 0 / 6 số 0 */}
                <div className="d-flex gap-1 mt-1">
                  <Button
                    type="button"
                    variant="outline-secondary"
                    size="sm"
                    className="py-0 px-2"
                    style={{ fontSize: "0.75rem" }}
                    onClick={() => handleAddZeros(3)}
                    disabled={saving || !form.price}
                  >
                    +000 (.000)
                  </Button>
                  <Button
                    type="button"
                    variant="outline-secondary"
                    size="sm"
                    className="py-0 px-2"
                    style={{ fontSize: "0.75rem" }}
                    onClick={() => handleAddZeros(6)}
                    disabled={saving || !form.price}
                  >
                    +000.000
                  </Button>
                </div>
              </Form.Group>
            </div>

            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>
                  Số lượng tồn kho <span className="text-danger">*</span>
                </Form.Label>
                <Form.Control
                  type="number"
                  name="quantity"
                  value={form.quantity}
                  onChange={handleChange}
                  min="0"
                  step="1"
                  placeholder="0"
                  disabled={saving}
                  required
                />
              </Form.Group>
            </div>
          </div>

          {editingProduct && (
            <Form.Group className="mb-3">
              <Form.Label>Trạng thái</Form.Label>
              <Form.Select
                name="status"
                value={form.status}
                onChange={handleChange}
                disabled={saving}
              >
                <option value="ACTIVE">Đang hoạt động</option>
                <option value="INACTIVE">Ngừng hoạt động</option>
              </Form.Select>
            </Form.Group>
          )}

          <Form.Group className="mb-3">
            <Form.Label>Hình ảnh</Form.Label>
            <Form.Control
              type="file"
              accept="image/*"
              onChange={handleImageChange}
              disabled={saving}
            />
            <Form.Text muted>Chọn hình ảnh sản phẩm.</Form.Text>
          </Form.Group>

          {previewUrl && (
            <div className="product-form-image-preview">
              <Form.Label>Xem trước</Form.Label>
              <div>
                <Image
                  src={previewUrl}
                  alt="Product preview"
                  thumbnail
                  className="product-form-preview-image"
                />
              </div>
            </div>
          )}
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={onClose} disabled={saving}>
            Hủy
          </Button>

          <Button variant="primary" type="submit" disabled={saving}>
            {saving ? (
              <>
                <Spinner size="sm" animation="border" className="me-2" />
                Đang lưu...
              </>
            ) : editingProduct ? (
              "Cập nhật"
            ) : (
              "Tạo sản phẩm"
            )}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
