// src/modules/operation/hooks/useProductTourAssignments.js
import { useCallback, useState } from "react";
import productTourAssignmentService from "../services/productTourAssignmentService";

export default function useProductTourAssignments() {
  const [productAssignments, setProductAssignments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadProductAssignments = useCallback(async (tourId) => {
    setLoading(true);
    setError("");
    try {
      const data = await productTourAssignmentService.getByTour(tourId);
      const list = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setProductAssignments(list);
      return list;
    } catch (err) {
      console.error("LOAD PRODUCT ASSIGNMENTS ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách phân công tiện ích.",
      );
      return [];
    } finally {
      setLoading(false);
    }
  }, []);

  const assignProduct = useCallback(async (payload) => {
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      const result = await productTourAssignmentService.assign(payload);
      setProductAssignments((prev) => {
        const exists = prev.some((item) => item.id === result.id);
        return exists ? prev : [...prev, result];
      });
      setSuccess("Phân công tiện ích thành công.");
      return result;
    } catch (err) {
      console.error("ASSIGN PRODUCT ERROR:", err);
      setError(err.response?.data?.message || "Không thể phân công tiện ích.");
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const deleteProductAssignment = useCallback(async (id) => {
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      await productTourAssignmentService.delete(id);
      setProductAssignments((prev) => prev.filter((item) => item.id !== id));
      setSuccess("Đã xóa phân công tiện ích.");
    } catch (err) {
      console.error("DELETE PRODUCT ASSIGNMENT ERROR:", err);
      setError(
        err.response?.data?.message || "Không thể xóa phân công tiện ích.",
      );
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    productAssignments,
    productLoading: loading,
    productError: error,
    productSuccess: success,
    loadProductAssignments,
    assignProduct,
    deleteProductAssignment,
  };
}
