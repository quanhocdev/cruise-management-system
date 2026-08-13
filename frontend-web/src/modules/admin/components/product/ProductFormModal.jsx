import { useEffect, useState } from "react";
import { Alert, Button, Form, Image, Modal, Spinner } from "react-bootstrap";

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
    price: "",
    quantity: "",
    status: "ACTIVE",
    image: null,
  });

  const [previewUrl, setPreviewUrl] = useState("");

  /*
   * =====================================================
   * RESET / LOAD FORM
   * =====================================================
   */
  useEffect(() => {
    if (!show) {
      return;
    }

    if (editingProduct) {
      setForm({
        name: editingProduct.name || "",
        description: editingProduct.description || "",
        price: editingProduct.price ?? "",
        quantity: editingProduct.quantity ?? "",
        status: editingProduct.status || "ACTIVE",
        image: null,
      });

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

      setPreviewUrl("");
    }
  }, [show, editingProduct]);

  /*
   * =====================================================
   * INPUT CHANGE
   * =====================================================
   */
  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  /*
   * =====================================================
   * IMAGE CHANGE
   * =====================================================
   */
  const handleImageChange = (event) => {
    const file = event.target.files?.[0] || null;

    setForm((previous) => ({
      ...previous,
      image: file,
    }));

    if (file) {
      const url = URL.createObjectURL(file);

      setPreviewUrl(url);
    } else {
      setPreviewUrl(editingProduct?.imageUrl || "");
    }
  };

  /*
   * =====================================================
   * SUBMIT
   * =====================================================
   */
  const handleSubmit = async (event) => {
    event.preventDefault();

    /*
     * Frontend validation
     */
    if (!form.name.trim()) {
      return;
    }

    if (form.price === "" || Number(form.price) < 0) {
      return;
    }

    if (form.quantity === "" || Number(form.quantity) < 0) {
      return;
    }

    const formData = new FormData();

    formData.append("name", form.name.trim());

    formData.append("description", form.description.trim());

    formData.append("price", form.price);

    formData.append("quantity", form.quantity);

    /*
     * Status chỉ cần khi UPDATE
     */
    if (editingProduct) {
      formData.append("status", form.status);
    }

    /*
     * Image chỉ append khi user chọn file
     */
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

          {/* =================================================
              NAME
             ================================================= */}
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

          {/* =================================================
              DESCRIPTION
             ================================================= */}
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

          {/* =================================================
              PRICE + QUANTITY
             ================================================= */}
          <div className="row">
            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>
                  Giá <span className="text-danger">*</span>
                </Form.Label>

                <Form.Control
                  type="number"
                  name="price"
                  value={form.price}
                  onChange={handleChange}
                  min="0"
                  step="0.01"
                  placeholder="0"
                  disabled={saving}
                  required
                />
              </Form.Group>
            </div>

            <div className="col-md-6">
              <Form.Group className="mb-3">
                <Form.Label>
                  Số lượng <span className="text-danger">*</span>
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

          {/* =================================================
              STATUS
             ================================================= */}
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

          {/* =================================================
              IMAGE
             ================================================= */}
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

          {/* =================================================
              IMAGE PREVIEW
             ================================================= */}
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
