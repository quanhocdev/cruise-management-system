import { useState } from "react";
import { Alert, Button } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

import useProducts from "../hooks/useProducts";

import ProductTable from "../components/product/ProductTable";
import ProductFormModal from "../components/product/ProductFormModal";

import "../styles/ManagerProduct.css";

export default function ManagerProduct() {
  const { areaId } = useParams();
  const navigate = useNavigate();

  const {
    products,
    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    createProduct,
    updateProduct,
    deleteProduct,
  } = useProducts(areaId);

  const [showModal, setShowModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);

  /*
   * =====================================================
   * OPEN CREATE
   * =====================================================
   */
  const handleOpenCreate = () => {
    setEditingProduct(null);

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  /*
   * =====================================================
   * OPEN EDIT
   * =====================================================
   */
  const handleOpenEdit = (product) => {
    setEditingProduct(product);

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  /*
   * =====================================================
   * CLOSE MODAL
   * =====================================================
   */
  const handleCloseModal = () => {
    if (saving) {
      return;
    }

    setShowModal(false);
    setEditingProduct(null);
    setError("");
  };

  /*
   * =====================================================
   * CREATE / UPDATE
   * =====================================================
   */
  const handleSubmit = async (formData) => {
    let result;

    if (editingProduct) {
      result = await updateProduct(editingProduct.id, formData);
    } else {
      result = await createProduct(formData);
    }

    if (result) {
      setShowModal(false);
      setEditingProduct(null);
    }
  };

  /*
   * =====================================================
   * DELETE
   * =====================================================
   */
  const handleDelete = async (product) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa sản phẩm "${product.name}" không?\n\n` +
        "Sản phẩm sẽ bị xóa khỏi cơ sở dữ liệu và không thể khôi phục.",
    );

    if (!confirmed) {
      return;
    }

    await deleteProduct(product.id);
  };

  /*
   * =====================================================
   * BACK
   * =====================================================
   */
  const handleBack = () => {
    navigate(-1);
  };

  /*
   * =====================================================
   * RENDER
   * =====================================================
   */
  return (
    <div className="manager-product-page container-fluid py-4">
      {/* =================================================
          HEADER
         ================================================= */}
      <div className="manager-product-header">
        <div className="manager-product-header-left">
          <Button
            variant="outline-secondary"
            size="sm"
            className="manager-product-back-button"
            onClick={handleBack}
          >
            ← Quay lại
          </Button>

          <div>
            <h2 className="manager-product-title">Quản lý sản phẩm</h2>

            <p className="manager-product-description">
              Quản lý các sản phẩm được cung cấp tại khu vực này.
            </p>
          </div>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Tạo sản phẩm
        </Button>
      </div>

      {/* =================================================
          SUCCESS
         ================================================= */}
      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {/* =================================================
          ERROR
         ================================================= */}
      {error && !showModal && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      {/* =================================================
          AREA ID
         ================================================= */}
      {!areaId && <Alert variant="danger">Không xác định được khu vực.</Alert>}

      {/* =================================================
          PRODUCT TABLE
         ================================================= */}
      <ProductTable
        products={products}
        loading={loading}
        error={error}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
      />

      {/* =================================================
          FORM MODAL
         ================================================= */}
      <ProductFormModal
        show={showModal}
        saving={saving}
        editingProduct={editingProduct}
        error={error}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
      />
    </div>
  );
}
