import { useCallback, useEffect, useState } from "react";
import api from "../../../api/axios";

export default function useProducts() {
  const [products, setProducts] = useState([]);

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadProducts = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await api.get("/admin/products");

      const data = response.data;

      setProducts(
        Array.isArray(data)
          ? data
          : Array.isArray(data?.data)
            ? data.data
            : Array.isArray(data?.content)
              ? data.content
              : [],
      );
    } catch (error) {
      console.error("Load products error:", error);

      if (error.code === "ERR_NETWORK") {
        setError("Backend chưa chạy. Chưa thể tải dữ liệu sản phẩm.");
      } else {
        setError(
          error.response?.data?.message || "Không thể tải danh sách sản phẩm.",
        );
      }

      setProducts([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadProducts();
  }, [loadProducts]);

  const createProduct = async (formData) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await api.post("/admin/products", formData);

      setSuccess("Tạo sản phẩm thành công.");

      await loadProducts();

      return true;
    } catch (error) {
      console.error("Create product error:", error);

      if (error.code === "ERR_NETWORK") {
        setError("Backend chưa chạy. Không thể tạo sản phẩm.");
      } else {
        setError(error.response?.data?.message || "Không thể tạo sản phẩm.");
      }

      return false;
    } finally {
      setSaving(false);
    }
  };

  const updateProduct = async (productId, formData) => {
    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await api.patch(`/admin/products/${productId}`, formData);

      setSuccess("Cập nhật sản phẩm thành công.");

      await loadProducts();

      return true;
    } catch (error) {
      console.error("Update product error:", error);

      if (error.code === "ERR_NETWORK") {
        setError("Backend chưa chạy. Không thể cập nhật sản phẩm.");
      } else {
        setError(
          error.response?.data?.message || "Không thể cập nhật sản phẩm.",
        );
      }

      return false;
    } finally {
      setSaving(false);
    }
  };

  const deleteProduct = async (productId) => {
    setError("");
    setSuccess("");

    try {
      await api.delete(`/admin/products/${productId}`);

      setSuccess("Xóa sản phẩm thành công.");

      await loadProducts();

      return true;
    } catch (error) {
      console.error("Delete product error:", error);

      if (error.code === "ERR_NETWORK") {
        setError("Backend chưa chạy. Không thể xóa sản phẩm.");
      } else {
        setError(error.response?.data?.message || "Không thể xóa sản phẩm.");
      }

      return false;
    }
  };

  return {
    products,

    loading,
    saving,

    error,
    success,

    setError,
    setSuccess,

    loadProducts,
    createProduct,
    updateProduct,
    deleteProduct,
  };
}
