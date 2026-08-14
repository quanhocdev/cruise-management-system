import { useState } from "react";
import { Alert, Button } from "react-bootstrap";

import useProducts from "../hooks/useProducts";

import ProductTable from "../components/product/ProductTable";
import ProductFormModal from "../components/product/ProductFormModal";

import "../styles/ManagerProduct.css";

export default function ManagerProduct() {
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
  } = useProducts();

  const [showModal, setShowModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);

  const handleOpenCreate = () => {
    setEditingProduct(null);

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  const handleOpenEdit = (product) => {
    setEditingProduct(product);

    setError("");
    setSuccess("");

    setShowModal(true);
  };

  const handleCloseModal = () => {
    if (saving) {
      return;
    }

    setShowModal(false);
    setEditingProduct(null);
    setError("");
  };

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

  const handleDelete = async (product) => {
    const confirmed = window.confirm(
      `Bạn có chắc muốn xóa sản phẩm "${product.name}" không?\n\n` +
        "Sản phẩm sẽ bị xóa khỏi hệ thống và không thể khôi phục.",
    );

    if (!confirmed) {
      return;
    }

    await deleteProduct(product.id);
  };

  return (
    <div className="manager-product-page container-fluid py-4">
      <div className="manager-product-header">
        <div>
          <h2 className="manager-product-title">Quản lý sản phẩm</h2>

          <p className="manager-product-description">
            Quản lý danh mục sản phẩm dùng chung trong hệ thống.
          </p>
        </div>

        <Button variant="primary" onClick={handleOpenCreate}>
          + Tạo sản phẩm
        </Button>
      </div>

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {error && !showModal && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      <ProductTable
        products={products}
        loading={loading}
        onEdit={handleOpenEdit}
        onDelete={handleDelete}
      />

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
