import { useCallback, useEffect, useState } from "react";
import api from "../../../api/axios";

export default function useProducts(areaId) {
  const [products, setProducts] = useState([]);

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadProducts = useCallback(async () => {
    /*
     * Chưa có areaId
     */
    if (!areaId) {
      setProducts([]);
      setLoading(false);
      setError("Chưa xác định được khu vực để tải sản phẩm.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await api.get(`/admin/areas/${areaId}/products`);

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

      // Quan trọng: vẫn giữ UI hoạt động
      setProducts([]);
    } finally {
      setLoading(false);
    }
  }, [areaId]);

  useEffect(() => {
    loadProducts();
  }, [loadProducts]);

  /*
   * =====================================================
   * CREATE
   * =====================================================
   */
  const createProduct = async (formData) => {
    if (!areaId) {
      setError("Chưa xác định được khu vực.");
      return false;
    }

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await api.post(`/admin/areas/${areaId}/products`, formData);

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

  /*
   * =====================================================
   * UPDATE
   * =====================================================
   */
  const updateProduct = async (productId, formData) => {
    if (!areaId) {
      setError("Chưa xác định được khu vực.");
      return false;
    }

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      await api.patch(`/admin/areas/${areaId}/products/${productId}`, formData);

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

  /*
   * =====================================================
   * DELETE
   * =====================================================
   */
  const deleteProduct = async (productId) => {
    if (!areaId) {
      setError("Chưa xác định được khu vực.");
      return false;
    }

    setError("");
    setSuccess("");

    try {
      await api.delete(`/admin/areas/${areaId}/products/${productId}`);

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
