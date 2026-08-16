import { useCallback, useState } from "react";
import convenienceProductService from "../services/convenienceProductService";

export default function useProduct() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  /**
   * LOAD PRODUCTS (Read-only)
   */
  const loadProducts = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await convenienceProductService.getAllProducts();
      console.log("RAW DATA FROM SERVICE:", data); // Add dòng này để xem hình dáng dữ liệu thực sự
      const productList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setProducts(productList);
    } catch (err) {
      console.error("LOAD CONVENIENCE PRODUCTS ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách sản phẩm tiện ích.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const clearMessages = useCallback(() => {
    setError("");
  }, []);

  return {
    products,
    loading,
    error,
    loadProducts,
    clearMessages,
  };
}
